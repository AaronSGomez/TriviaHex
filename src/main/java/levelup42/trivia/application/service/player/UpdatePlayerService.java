package levelup42.trivia.application.service.player;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.UpdatePlayerUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;

import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UpdatePlayerService implements UpdatePlayerUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;

    public UpdatePlayerService(PlayerRepositoryPort playerRepositoryPort) {
        this.playerRepositoryPort = playerRepositoryPort;
    }

    @Override
    public Player updatePlayer(UUID id, Player playerDetails) {
        Player existingPlayer = playerRepositoryPort.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("Player with id: ["+id+"] not found"));

        Player updatedPlayer= new Player(
                id,
                playerDetails.getName(),
                playerDetails.getMail(),
                existingPlayer.getPassword(),
                existingPlayer.getRole()
        );

        return playerRepositoryPort.savePlayer(updatedPlayer);
    }
}
