package levelup42.trivia.domain.port.out;

import levelup42.trivia.domain.model.GameSession;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepositoryPort {

    GameSession save(GameSession session);

    Optional<GameSession> findById(UUID id);

    List<GameSession> findByPlayerId(UUID playerId);

    List<GameSession> findAllFinishedOrderedByScoreDesc();
}
