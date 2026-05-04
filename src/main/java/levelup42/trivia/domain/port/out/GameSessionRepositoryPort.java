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
    
    /**
     * Returns question IDs that the player failed in given subject, ordered by most recent failure.
     */
    List<Long> findFailedQuestionIdsByPlayerAndSubject(UUID playerId, String subject);

    /**
     * Register the answer result for a previously asked question in a session.
     */
    void registerAnswerResult(UUID sessionId, Long questionId, boolean correct, java.time.Instant answeredAt);

    List<Long> findCorrectQuestionIdsByPlayerAndSubjectSince(UUID playerId, String subject, java.time.Instant since);

    /**
     * Returns ALL question IDs (correct or incorrect) asked by player in given subject within time window.
     * Used to maximize question rotation and avoid repeating questions in 96-hour window.
     */
    List<Long> findAskedQuestionIdsByPlayerAndSubjectSince(UUID playerId, String subject, java.time.Instant since);

    /**
     * Returns COUNT of distinct questions asked by player in given subject within time window.
     * Used to dynamically adjust 96h window strategy: if count >= 80% of pool size, disable window.
     */
    long countAskedByPlayerAndSubjectSince(UUID playerId, String subject, java.time.Instant since);
}
