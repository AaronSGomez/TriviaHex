package levelup42.trivia.domain.port.in.gamesession;

import levelup42.trivia.domain.model.GameSession;

import java.util.UUID;

public interface FinishGameSessionUseCase {

    GameSession finishSession(UUID sessionId);
}
