package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.gamesession.GetGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GetGameSessionService implements GetGameSessionUseCase {

    private final GameSessionRepositoryPort sessionRepository;

    public GetGameSessionService(GameSessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public GameSession getSessionById(UUID id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game Session not found"));
    }

    @Override
    public List<GameSession> getPlayerHistory(UUID playerId) {
        return sessionRepository.findByPlayerId(playerId);
    }

    @Override
    public List<GameSession> getLeaderboard() {
        return sessionRepository.findAllFinishedOrderedByScoreDesc();
    }
}
