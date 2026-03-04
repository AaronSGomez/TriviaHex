package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.GetPlayerUseCase;

import levelup42.trivia.infraestructure.adapter.in.rest.dto.PlayerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * PlayerController exposes read-only endpoints for player data.
 * Player registration is handled exclusively via /api/auth/register (AuthController),
 * which returns a JWT token and validates email uniqueness.
 */
@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final GetPlayerUseCase getPlayerUseCase;

    public PlayerController(GetPlayerUseCase getPlayerUseCase) {
        this.getPlayerUseCase = getPlayerUseCase;
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(getPlayerUseCase.getAllPlayers().stream()
                .map(PlayerResponse::fromDomain)
                .toList());
    }

    @GetMapping("{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable UUID id) {
        Player player = getPlayerUseCase.getPlayerById(id);
        if (player != null) {
            return ResponseEntity.ok(PlayerResponse.fromDomain(player));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
