package levelup42.trivia.domain.port.in.player;

import levelup42.trivia.domain.model.Player;

public interface CreatePlayerUseCase {
    Player createPlayer(Player player);
}
