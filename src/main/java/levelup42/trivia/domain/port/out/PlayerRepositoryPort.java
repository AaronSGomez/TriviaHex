package levelup42.trivia.domain.port.out;

import levelup42.trivia.domain.model.Player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerRepositoryPort {
    Player savePlayer(Player player);
    Optional<Player> findById(UUID id);
    void deletePlayer(UUID id);
}
