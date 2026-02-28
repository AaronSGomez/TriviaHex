package levelup42.trivia.domain.port.in.player;

import levelup42.trivia.domain.model.Player;

import java.util.List;
import java.util.UUID;

public interface GetPlayerUseCase {
    List<Player> getAllPlayers();
    Player getPlayerById(UUID id);
}
