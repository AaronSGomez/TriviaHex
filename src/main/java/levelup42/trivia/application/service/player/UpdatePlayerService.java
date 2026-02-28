package levelup42.trivia.application.service.player;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.UpdatePlayerUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;

import java.util.UUID;

public class UpdatePlayerService implements UpdatePlayerUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;

    public UpdatePlayerService(PlayerRepositoryPort playerRepositoryPort) {
        this.playerRepositoryPort = playerRepositoryPort;
    }

    @Override
    public Player updatePlayer(UUID id, Player playerDetails) {
        Player existingPlayer = playerRepositoryPort.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Player with id: ["+id+"] not found"));

        // Create a new Player
        Player updatedPlayer= new Player(
                existingPlayer.getId(),
                existingPlayer.getName(),
                existingPlayer.getMail()
        );

        return playerRepositoryPort.savePlayer(updatedPlayer);
    }
}
