package levelup42.trivia.domain.port.in.gamesession;

import java.util.UUID;

public interface SubmitAnswerUseCase {
    
    AnswerResult answerQuestion(UUID sessionId, Long questionId, String selectedOption, Integer timeElapsedSeconds);
    
    // DTO para el resultado
    public record AnswerResult(
        boolean isCorrect,
        String correctAnswer,
        String explanation,
        int currentScore,
        boolean isSessionFinished
    ) {}
}
