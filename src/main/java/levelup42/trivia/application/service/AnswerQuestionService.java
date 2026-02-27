package levelup42.trivia.application.service;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.AnswerQuestionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AnswerQuestionService implements AnswerQuestionUseCase {

    private final GameSessionRepositoryPort sessionRepository;
    private final QuestionRepositoryPort questionRepository;

    public AnswerQuestionService(GameSessionRepositoryPort sessionRepository, QuestionRepositoryPort questionRepository) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public AnswerResult answerQuestion(UUID sessionId, Long questionId, String selectedOption) {
        // 1. Obtener la sesión
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.isFinished()) {
            throw new IllegalStateException("Game Session is already finished");
        }

        // 2. Obtener la pregunta
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found"));

        // 3. Evaluar respuesta
        boolean isCorrect = question.getCorrectOption().equalsIgnoreCase(selectedOption);

        if (isCorrect) {
            session.registerCorrectAnswer();
        } else {
            session.registerIncorrectAnswer();
        }

        // 4. Verificar si terminamos
        if (session.getAnsweredQuestions() >= session.getTotalQuestions()) {
            session.finish();
        }

        sessionRepository.save(session);

        return new AnswerResult(
                isCorrect,
                question.getCorrectOption(),
                question.getExplanation(),
                session.getScore(),
                session.isFinished()
        );
    }
}
