package levelup42.trivia.domain.port.out;

import levelup42.trivia.domain.model.Question;

import java.util.List;
import java.util.Optional;

public interface QuestionRepositoryPort {
    // CRUD
    Question saveQuestion(Question question); // create y update
    void deleteQuestion(Long id); // delete
    List<Question> findAll(); // read-All
    Optional<Question> findById(Long id); // read

}
