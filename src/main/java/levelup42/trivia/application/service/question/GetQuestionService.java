package levelup42.trivia.application.service.question;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.GetQuestionUseCase;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetQuestionService implements GetQuestionUseCase {

    private final QuestionRepositoryPort questionRepositoryPort;

    public GetQuestionService(QuestionRepositoryPort questionRepositoryPort) {
        this.questionRepositoryPort = questionRepositoryPort;
    }

    @Override
    public List<Question> getAllQuestions() {
        return questionRepositoryPort.findAll();
    }

    @Override
    public Optional<Question> getQuestionById(Long id) {
        return questionRepositoryPort.findById(id);
    }
}
