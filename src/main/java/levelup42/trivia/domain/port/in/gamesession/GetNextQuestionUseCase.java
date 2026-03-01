package levelup42.trivia.domain.port.in.gamesession;

import levelup42.trivia.domain.model.Question;
import java.util.Optional;
import java.util.UUID;

public interface GetNextQuestionUseCase {
    Optional<Question> getNextQuestion(UUID sessionId);
}
