package levelup42.trivia.infraestructure.adapter.in.rest;

import jakarta.validation.Valid;
import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.auth.AuthUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.AuthDto.AuthenticationResponse;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.AuthDto.LoginRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.AuthDto.RegisterRequest;
import org.springframework.http.ResponseEntity;
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
        Player player = authUseCase.register(request.getMail(),request.getName(), request.getPassword(), Player.Role.ADMIN);
        // Automatically log them in after registration to return the token
        String token = authUseCase.login(request.getMail(), request.getPassword());
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(token)
                .id(player.getId())
                .name(player.getName())
                .mail(player.getMail())
                .build());
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> registerUser(@RequestBody RegisterRequest request) {
        Player player = authUseCase.register(request.getMail(), request.getName(), request.getPassword(), Player.Role.USER);
        // Automatically log them in after registration to return the token
        String token = authUseCase.login(request.getMail(), request.getPassword());
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(token)
                .id(player.getId())
                .name(player.getName())
                .mail(player.getMail())
                .build());
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
}
