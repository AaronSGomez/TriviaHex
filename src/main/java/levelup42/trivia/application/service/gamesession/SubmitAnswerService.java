package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.gamesession.SubmitAnswerUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SubmitAnswerService implements SubmitAnswerUseCase {

    private final GameSessionRepositoryPort sessionRepository;
    private final QuestionRepositoryPort questionRepository;

    public SubmitAnswerService(GameSessionRepositoryPort sessionRepository, QuestionRepositoryPort questionRepository) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public AnswerResult answerQuestion(UUID sessionId, Long questionId, String selectedOption, Integer timeElapsedSeconds) {
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
        String dbOption = question.getCorrectOption() != null ? question.getCorrectOption().trim() : "";
        boolean isCorrect = dbOption.equalsIgnoreCase(selectedOption.trim());
        boolean isSkipped = "SKIP".equalsIgnoreCase(selectedOption.trim());

        if (isSkipped) {
            session.registerSkippedAnswer();
            isCorrect = false;
        } else if (isCorrect) {
            int points = 100;
            if (timeElapsedSeconds != null && timeElapsedSeconds > 3) {
                points = 100 - ((timeElapsedSeconds - 3) * 10);
                points = Math.max(10, points);
            }
            session.registerCorrectAnswer(points);
        } else {
            session.registerIncorrectAnswer();
        }

        sessionRepository.save(session);
        // Register the question as asked for this session
        sessionRepository.registerAskedQuestion(sessionId, questionId);

        return new AnswerResult(
                isCorrect,
                question.getCorrectOption(),
                question.getExplanation(),
                session.getScore(),
                session.isFinished()
        );
    }
}
