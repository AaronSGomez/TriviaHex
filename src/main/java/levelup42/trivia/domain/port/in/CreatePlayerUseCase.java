package levelup42.trivia.domain.port.in;

import levelup42.trivia.domain.model.Player;

public interface CreatePlayerUseCase {
    Player createPlayer(Player player);
}
