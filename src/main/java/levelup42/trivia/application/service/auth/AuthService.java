package levelup42.trivia.application.service.auth;

import levelup42.trivia.domain.exception.UserAlreadyExistsException;
import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.auth.AuthUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import levelup42.trivia.infraestructure.security.CustomUserDetails;
import levelup42.trivia.infraestructure.security.jwt.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService implements AuthUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(
            PlayerRepositoryPort playerRepositoryPort,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.playerRepositoryPort = playerRepositoryPort;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
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
}
