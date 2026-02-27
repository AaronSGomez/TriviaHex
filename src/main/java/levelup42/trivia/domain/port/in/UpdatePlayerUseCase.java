package levelup42.trivia.domain.port.in;

import levelup42.trivia.domain.model.Player;

import java.util.UUID;

public interface UpdatePlayerUseCase {
    Player updatePlayer(UUID id, Player playerDetails);
}
