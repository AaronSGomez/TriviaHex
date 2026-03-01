package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.CreatePlayerUseCase;
import levelup42.trivia.domain.port.in.player.DeletePlayerUseCase;
import levelup42.trivia.domain.port.in.player.GetPlayerUseCase;
import levelup42.trivia.domain.port.in.player.UpdatePlayerUseCase;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.PlayerRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.PlayerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/players")
public class PlayerController {

    private final CreatePlayerUseCase createPlayerUseCase;
    private final GetPlayerUseCase getPlayerUseCase;
    private final UpdatePlayerUseCase updatePlayerUseCase;
    private final DeletePlayerUseCase deletePlayerUseCase;

    public PlayerController(
            CreatePlayerUseCase createPlayerUseCase,
            UpdatePlayerUseCase updatePlayerUseCase,
            DeletePlayerUseCase deletePlayerUseCase,
            GetPlayerUseCase getPlayerUseCase
    ) {
        this.createPlayerUseCase = createPlayerUseCase;
        this.updatePlayerUseCase = updatePlayerUseCase;
        this.deletePlayerUseCase = deletePlayerUseCase;
        this.getPlayerUseCase = getPlayerUseCase;
    }

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        Player playerToCreate = new Player(UUID.randomUUID(), request.getName(), request.getMail());
        Player createdPlayer = createPlayerUseCase.createPlayer(playerToCreate);
        return ResponseEntity.ok(PlayerResponse.fromDomain(createdPlayer));
    }

    @PostMapping("{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(@PathVariable UUID id, @Valid @RequestBody PlayerRequest request) {
        Player playerToUpdate = new Player(UUID.randomUUID(), request.getName(), request.getMail());
        Player updatedPlayer = updatePlayerUseCase.updatePlayer(id,playerToUpdate);
        return ResponseEntity.ok(PlayerResponse.fromDomain(updatedPlayer));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
        deletePlayerUseCase.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(getPlayerUseCase.getAllPlayers().stream().map(PlayerResponse::fromDomain).toList());
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
