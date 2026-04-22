package levelup42.trivia.application.service.auth;

import levelup42.trivia.domain.exception.UserAlreadyExistsException;
import levelup42.trivia.domain.exception.InvalidGoogleTokenException;
import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.auth.AuthenticatedPlayer;
import levelup42.trivia.domain.port.in.auth.AuthUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import levelup42.trivia.infraestructure.security.CustomUserDetails;
import levelup42.trivia.infraestructure.security.google.GoogleTokenVerifierService;
import levelup42.trivia.infraestructure.security.firebase.FirebaseTokenVerifierService;
import levelup42.trivia.infraestructure.security.jwt.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
public class AuthService implements AuthUseCase {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final PlayerRepositoryPort playerRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GoogleTokenVerifierService googleTokenVerifierService;
    private final FirebaseTokenVerifierService firebaseTokenVerifierService;

    public AuthService(
            PlayerRepositoryPort playerRepositoryPort,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            GoogleTokenVerifierService googleTokenVerifierService,
            FirebaseTokenVerifierService firebaseTokenVerifierService
    ) {
        this.playerRepositoryPort = playerRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.googleTokenVerifierService = googleTokenVerifierService;
        this.firebaseTokenVerifierService = firebaseTokenVerifierService;
    }

    @Override
    public Player register(String mail, String name, String password, Player.Role role) {
        if (playerRepositoryPort.findByMail(mail).isPresent()) {
            throw new UserAlreadyExistsException("A user with email " + mail + " already exists.");
        }

        Player userToCreate = new Player(
                UUID.randomUUID(),
                name, // A placeholder or require name in AuthRequest
                mail,
                passwordEncoder.encode(password),
                role
        );

        return playerRepositoryPort.savePlayer(userToCreate);
    }

    @Override
    public String login(String mail, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(mail, password)
        );
        
        // If authenticationManager doesn't throw an exception, user is authenticated
        var user = playerRepositoryPort.findByMail(mail).orElseThrow();
        
        var userDetails = new CustomUserDetails(
                new levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity(
                        user.getId() != null ? user.getId() : UUID.randomUUID(),
                        user.getName(),
                        user.getMail(),
                        user.getPassword(),
                        user.getRole(),
                        user.getCreatedAt() != null ? user.getCreatedAt() : java.time.Instant.now()
                )
        );
        
        return jwtService.generateToken(userDetails);
    }

    @Override
    public AuthenticatedPlayer loginWithGoogle(String idToken) {
        // Try Firebase token first (new web flow)
        var firebaseProfile = firebaseTokenVerifierService.verify(idToken);
        
        if (firebaseProfile.isPresent()) {
            var profile = firebaseProfile.get();
            return createOrUpdatePlayer(profile.email(), profile.name());
        }

        // Fallback to Google token (legacy flow or mobile)
        var googleProfile = googleTokenVerifierService.verify(idToken)
                .orElseThrow(() -> new InvalidGoogleTokenException("No se pudo validar la cuenta de Google."));

        return createOrUpdatePlayer(googleProfile.email(), googleProfile.name());
    }

    private AuthenticatedPlayer createOrUpdatePlayer(String email, String name) {
        Player player = playerRepositoryPort.findByMail(email)
                .map(existing -> {
                    if (!Objects.equals(existing.getName(), name)) {
                        Player updated = new Player(
                                existing.getId(),
                                name,
                                existing.getMail(),
                                existing.getPassword(),
                                existing.getRole()
                        );
                        return playerRepositoryPort.savePlayer(updated);
                    }
                    return existing;
                })
                .orElseGet(() -> {
                    Player newUser = new Player(
                            UUID.randomUUID(),
                            name,
                            email,
                            passwordEncoder.encode(UUID.randomUUID().toString()),
                            Player.Role.USER
                    );
                    return playerRepositoryPort.savePlayer(newUser);
                });

        String token = jwtService.generateToken(toUserDetails(player));
        log.info("user_google_login_success mail={} role={} userId={}", player.getMail(), player.getRole(), player.getId());
        return new AuthenticatedPlayer(token, player);
    }

    private CustomUserDetails toUserDetails(Player user) {
        return new CustomUserDetails(
                new levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity(
                        user.getId() != null ? user.getId() : UUID.randomUUID(),
                        user.getName(),
                        user.getMail(),
                        user.getPassword(),
                        user.getRole(),
                        user.getCreatedAt() != null ? user.getCreatedAt() : java.time.Instant.now()
                )
        );
    }
}

