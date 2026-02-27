package levelup42.trivia.infraestructure.adapter.out.persistence.repository;

import levelup42.trivia.domain.model.SessionStatus;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DataGameSessionRepository extends JpaRepository<GameSessionEntity, UUID> {
    List<GameSessionEntity> findBySubjetAndStatus(String subjet, SessionStatus status);
}
