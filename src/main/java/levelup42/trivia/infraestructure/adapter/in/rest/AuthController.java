package levelup42.trivia.infraestructure.adapter.in.rest;

import jakarta.validation.Valid;
import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.auth.AuthUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.AuthDto.AuthenticationResponse;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.AuthDto.GoogleAuthRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.AuthDto.LoginRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.AuthDto.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCase authUseCase;
    private final PlayerRepositoryPort playerRepositoryPort;

    public AuthController(AuthUseCase authUseCase, PlayerRepositoryPort playerRepositoryPort) {
        this.authUseCase = authUseCase;
        this.playerRepositoryPort = playerRepositoryPort;
    }

    @PostMapping("/register/admin")
    public ResponseEntity<AuthenticationResponse> registerAdmin(@RequestBody RegisterRequest request) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Registro de administradores deshabilitado.");
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Registro de usuarios deshabilitado. Usa autenticación Google/Firebase.");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        String token = authUseCase.login(request.getMail(), request.getPassword());
        Player player = playerRepositoryPort.findByMail(request.getMail()).orElseThrow();
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(token)
                .id(player.getId())
                .name(player.getName())
                .mail(player.getMail())
                .build());
    }

    @PostMapping("/google")
    public ResponseEntity<AuthenticationResponse> googleLogin(@Valid @RequestBody GoogleAuthRequest request) {
        var authenticated = authUseCase.loginWithGoogle(request.getIdToken());
        var player = authenticated.player();

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(authenticated.token())
                .id(player.getId())
                .name(player.getName())
                .mail(player.getMail())
                .build());
    }
}
