package levelup42.trivia.application.service.player;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.GetPlayerUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class GetPlayerService implements GetPlayerUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;

    public GetPlayerService(PlayerRepositoryPort playerRepositoryPort) {
        this.playerRepositoryPort = playerRepositoryPort;
    }

    @Override
    public List<Player> getAllPlayers() {
        return playerRepositoryPort.findAll();
    }

    @Override
    public Player getPlayerById(UUID id) {
        return playerRepositoryPort.findById(id).orElseThrow();
    }
}
