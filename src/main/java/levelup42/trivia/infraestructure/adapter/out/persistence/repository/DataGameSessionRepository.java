package levelup42.trivia.infraestructure.adapter.out.persistence.repository;

import levelup42.trivia.domain.model.SessionStatus;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface DataGameSessionRepository extends JpaRepository<GameSessionEntity, UUID> {
    interface LegacyGameSessionRow {
        UUID getId();
        UUID getPlayerId();
        String getSubject();
        Integer getTotalQuestions();
        Integer getAnsweredQuestions();
        Integer getCorrectAnswers();
        Integer getSkippedAnswers();
        Integer getScore();
        Instant getStartedAt();
        Instant getFinishedAt();
        Integer getStatus();
    }

    @Query(value = "SELECT id, player_id AS playerId, subject, total_questions AS totalQuestions, " +
        "answered_questions AS answeredQuestions, correct_answers AS correctAnswers, skipped_answers AS skippedAnswers, " +
        "score, started_at AS startedAt, finished_at AS finishedAt, status " +
            "FROM gamesession WHERE subject = :subject AND status = :status", nativeQuery = true)
        List<LegacyGameSessionRow> findBySubjectAndStatus(@Param("subject") String subject, @Param("status") int status);

    @Query(value = "SELECT id, player_id AS playerId, subject, total_questions AS totalQuestions, " +
        "answered_questions AS answeredQuestions, correct_answers AS correctAnswers, skipped_answers AS skippedAnswers, " +
        "score, started_at AS startedAt, finished_at AS finishedAt, status " +
        "FROM gamesession WHERE player_id = :playerId", nativeQuery = true)
    List<LegacyGameSessionRow> findByPlayerId(@Param("playerId") UUID playerId);

    @Query(value = "SELECT id, player_id AS playerId, subject, total_questions AS totalQuestions, " +
        "answered_questions AS answeredQuestions, correct_answers AS correctAnswers, skipped_answers AS skippedAnswers, " +
        "score, started_at AS startedAt, finished_at AS finishedAt, status " +
            "FROM gamesession WHERE status = :status ORDER BY score DESC", nativeQuery = true)
        List<LegacyGameSessionRow> findAllByStatusOrderByScoreDesc(@Param("status") int status);

    @Query(value = "SELECT id, player_id AS playerId, subject, total_questions AS totalQuestions, " +
        "answered_questions AS answeredQuestions, correct_answers AS correctAnswers, skipped_answers AS skippedAnswers, " +
        "score, started_at AS startedAt, finished_at AS finishedAt, status " +
            "FROM gamesession WHERE status = :status AND finished_at >= :weekStart AND finished_at < :weekEnd ORDER BY score DESC", nativeQuery = true)
        List<LegacyGameSessionRow> findWeeklyLeaderboard(@Param("status") int status,
                  @Param("weekStart") Instant weekStart,
                  @Param("weekEnd") Instant weekEnd);
}
