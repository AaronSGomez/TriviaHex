package levelup42.trivia.application.service.question;

import levelup42.trivia.domain.port.in.DeleteQuestionUseCase;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class DeleteQuestionService implements DeleteQuestionUseCase {

    private final QuestionRepositoryPort questionRepositoryPort;

    public DeleteQuestionService(QuestionRepositoryPort questionRepositoryPort) {
        this.questionRepositoryPort = questionRepositoryPort;
    }

    @Override
    public void deleteQuestion(Long id) {
        questionRepositoryPort.deleteQuestion(id);
    }
}
