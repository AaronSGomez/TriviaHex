package levelup42.trivia.infraestructure.adapter.out.persistence.repository;

import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DataGameSessionQuestionRepository extends JpaRepository<GameSessionQuestionEntity, Long> {

    @Query("SELECT gsq.questionId FROM GameSessionQuestionEntity gsq WHERE gsq.sessionId = :sessionId")
    List<Long> findAskedQuestionIdsBySessionId(@Param("sessionId") UUID sessionId);

    @Query("SELECT gsq.questionId FROM GameSessionQuestionEntity gsq JOIN GameSessionEntity gs ON gs.id = gsq.sessionId WHERE gs.playerId = :playerId AND gs.subject = :subject AND gsq.correct = false ORDER BY gsq.answeredAt DESC")
    List<Long> findFailedQuestionIdsByPlayerAndSubject(@Param("playerId") UUID playerId, @Param("subject") String subject);

    @Query("SELECT DISTINCT gsq.questionId FROM GameSessionQuestionEntity gsq JOIN GameSessionEntity gs ON gs.id = gsq.sessionId WHERE gs.playerId = :playerId AND gs.subject = :subject AND gsq.correct = true AND gsq.answeredAt >= :since")
    List<Long> findCorrectQuestionIdsByPlayerAndSubjectSince(@Param("playerId") UUID playerId, @Param("subject") String subject, @Param("since") java.time.Instant since);

    @Query("SELECT DISTINCT gsq.questionId FROM GameSessionQuestionEntity gsq JOIN GameSessionEntity gs ON gs.id = gsq.sessionId WHERE gs.playerId = :playerId AND gs.subject = :subject AND gsq.answeredAt >= :since")
    List<Long> findAskedQuestionIdsByPlayerAndSubjectSince(@Param("playerId") UUID playerId, @Param("subject") String subject, @Param("since") java.time.Instant since);

    @Query("SELECT COUNT(DISTINCT gsq.questionId) FROM GameSessionQuestionEntity gsq JOIN GameSessionEntity gs ON gs.id = gsq.sessionId WHERE gs.playerId = :playerId AND gs.subject = :subject AND gsq.answeredAt >= :since")
    long countAskedByPlayerAndSubjectSince(@Param("playerId") UUID playerId, @Param("subject") String subject, @Param("since") java.time.Instant since);

    Optional<GameSessionQuestionEntity> findBySessionIdAndQuestionId(UUID sessionId, Long questionId);
}
