package levelup42.trivia.infraestructure.repository;

import levelup42.trivia.infraestructure.adapter.out.persistence.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findBySubjectAndActiveTrue(String subject);
}