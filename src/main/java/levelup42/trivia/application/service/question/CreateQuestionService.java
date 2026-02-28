package levelup42.trivia.application.service.question;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.question.CreateQuestionUseCase;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreateQuestionService implements CreateQuestionUseCase {

    private final QuestionRepositoryPort repoPort;

    public CreateQuestionService(QuestionRepositoryPort repoPort) {
        this.repoPort = repoPort;
    }

    @Override
    public List<Question> findAll() {
        return repoPort.findAll();
    }

    @Override
    public Optional<Question> findById(Long id) {
        return repoPort.findById(id);
    }

    @Override
    public Question save(Question q) {
        return repoPort.saveQuestion(q);
    }

    @Override
    public void delete(Long id) {
        repoPort.deleteQuestion(id);
    }
}
