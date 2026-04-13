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
    List<GameSessionEntity> findBySubjectAndStatus(String subject, SessionStatus status);
    
    List<GameSessionEntity> findByPlayerId(UUID playerId);
    
    List<GameSessionEntity> findAllByStatusOrderByScoreDesc(SessionStatus status);
    
    @Query("SELECT gs FROM GameSessionEntity gs " +
            "WHERE gs.status = :status " +
           "AND gs.finishedAt >= :weekStart " +
           "AND gs.finishedAt < :weekEnd " +
           "ORDER BY gs.score DESC")
        List<GameSessionEntity> findWeeklyLeaderboard(@Param("status") SessionStatus status,
                                  @Param("weekStart") Instant weekStart,
                                  @Param("weekEnd") Instant weekEnd);
}
