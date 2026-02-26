package levelup42.trivia.domain.port.in;

import levelup42.trivia.domain.model.Question;

import java.util.List;
import java.util.Optional;

public interface CreateQuestionUseCase {
    List<Question> findAll();
    Optional<Question> findById(Long id);
    Question save(Question q);
    void delete(Long id);
}