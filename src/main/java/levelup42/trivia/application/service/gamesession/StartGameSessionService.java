package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.gamesession.StartGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StartGameSessionService implements StartGameSessionUseCase {

    private final GameSessionRepositoryPort sessionRepository;

    public StartGameSessionService(GameSessionRepositoryPort sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    @Override
    public GameSession createSession(UUID playerId, String subject, int totalQuestions) {
        GameSession session = new GameSession(
                UUID.randomUUID(),
                playerId,
                subject,
                totalQuestions
        );
        return sessionRepository.save(session);
    }
}
