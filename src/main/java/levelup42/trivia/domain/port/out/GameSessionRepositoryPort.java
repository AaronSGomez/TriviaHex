package levelup42.trivia.domain.port.out;

import levelup42.trivia.domain.model.GameSession;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameSessionRepositoryPort {

    GameSession save(GameSession session);

    Optional<GameSession> findById(UUID id);

    List<GameSession> findByPlayerId(UUID sessionId);

    List<GameSession> findAllFinishedOrderedByScoreDesc();

    List<GameSession> findWeeklyLeaderboard(Instant weekStart, Instant weekEnd);

    List<Long> findAskedQuestionIdsBySessionId(UUID sessionId);

    void registerAskedQuestion(UUID sessionId, Long questionId);
}
