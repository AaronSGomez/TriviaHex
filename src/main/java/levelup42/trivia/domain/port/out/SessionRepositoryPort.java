package levelup42.trivia.domain.port.out;

import levelup42.trivia.domain.model.GameSession;

import java.util.Optional;
import java.util.UUID;

public interface SessionRepositoryPort {

    void save(GameSession session);
    Optional<GameSession> findById(UUID id);

}
