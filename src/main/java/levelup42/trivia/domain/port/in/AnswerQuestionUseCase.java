package levelup42.trivia.domain.port.in;

import java.util.UUID;

public interface AnswerQuestionUseCase {
    
    AnswerResult answerQuestion(UUID sessionId, Long questionId, String selectedOption);
    
    // DTO para el resultado
    public record AnswerResult(
        boolean isCorrect,
        String correctAnswer,
        String explanation,
        int currentScore,
        boolean isSessionFinished
    ) {}
}
