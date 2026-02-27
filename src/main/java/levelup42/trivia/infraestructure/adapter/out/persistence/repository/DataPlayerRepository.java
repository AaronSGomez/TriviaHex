package levelup42.trivia.infraestructure.adapter.out.persistence.repository;

import levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DataPlayerRepository extends JpaRepository<PlayerEntity, UUID> {
}
