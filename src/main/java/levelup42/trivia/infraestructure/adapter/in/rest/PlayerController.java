package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.CreatePlayerUseCase;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.PlayerRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.PlayerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final CreatePlayerUseCase createPlayerUseCase;

    public PlayerController(CreatePlayerUseCase createPlayerUseCase) {
        this.createPlayerUseCase = createPlayerUseCase;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        Player playerToCreate = new Player(UUID.randomUUID(), request.getName(), request.getMail());
        Player createdPlayer = createPlayerUseCase.createPlayer(playerToCreate);
        return ResponseEntity.ok(PlayerResponse.fromDomain(createdPlayer));
    }
}
