package levelup42.trivia.domain.port.in;


import java.util.UUID;

public interface CreateGameSessionUseCase {

    UUID createSession(UUID playerId, String subject, int totalQuestions);
}