package levelup42.trivia.application.service.player;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.CreatePlayerUseCase;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class CreatePlayerService implements CreatePlayerUseCase {

    private final PlayerRepositoryPort playerRepositoryPort;

    public CreatePlayerService(PlayerRepositoryPort playerRepositoryPort) {
        this.playerRepositoryPort = playerRepositoryPort;
    }

    @Override
    public Player createPlayer(Player player) {
        // En esta capa se aplicarían reglas de negocio si las hubiera
        return playerRepositoryPort.savePlayer(player);
    }
}
