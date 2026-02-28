package levelup42.trivia.domain.port.in.gamesession;

import levelup42.trivia.domain.model.GameSession;

import java.util.List;
import java.util.UUID;

public interface GetGameSessionUseCase {

    GameSession getSessionById(UUID id);

    List<GameSession> getPlayerHistory(UUID playerId);

    List<GameSession> getLeaderboard();
}
