package levelup42.trivia.domain.port.in;

import levelup42.trivia.domain.model.Question;

import java.util.List;
import java.util.Optional;

public interface GetQuestionUseCase {
    List<Question> getAllQuestions();
    Optional<Question> getQuestionById(Long id);
}
