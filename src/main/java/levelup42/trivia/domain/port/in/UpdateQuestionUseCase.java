package levelup42.trivia.domain.port.in;

import levelup42.trivia.domain.model.Question;

public interface UpdateQuestionUseCase {
    Question updateQuestion(Long id, Question questionDetails);
}
