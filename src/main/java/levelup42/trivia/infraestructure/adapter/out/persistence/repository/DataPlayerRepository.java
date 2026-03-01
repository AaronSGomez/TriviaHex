package levelup42.trivia.infraestructure.adapter.out.persistence.repository;

import levelup42.trivia.domain.model.SessionStatus;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DataPlayerRepository extends JpaRepository<PlayerEntity, UUID> {
}
