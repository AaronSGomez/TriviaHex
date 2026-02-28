package levelup42.trivia.application.service.player;

import levelup42.trivia.domain.port.in.player.DeletePlayerUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;

import java.util.UUID;

public class DeletePlayerService implements DeletePlayerUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;

    public DeletePlayerService(PlayerRepositoryPort playerRepositoryPort) {
        this.playerRepositoryPort = playerRepositoryPort;
    }

    @Override
    public void deletePlayer(UUID id) {
        playerRepositoryPort.deletePlayer(id);
    }
}
