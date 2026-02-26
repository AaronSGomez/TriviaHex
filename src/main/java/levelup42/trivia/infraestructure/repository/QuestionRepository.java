package levelup42.trivia.infraestructure.repository;

import levelup42.trivia.domain.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findBySubjectAndActiveTrue(String subject);
}