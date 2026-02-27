package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.CreateGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;

import java.util.UUID;

public class CreateGameSessionService implements CreateGameSessionUseCase {

    private final GameSessionRepositoryPort sessionRepository;

    public CreateGameSessionService(GameSessionRepositoryPort sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    @Override
    public UUID createSession(UUID playerId, String subject, int totalQuestions) {
        GameSession session = new GameSession(
                UUID.randomUUID(),
                playerId,
                subject,
                totalQuestions
        );
        sessionRepository.save(session);

        return session.getId();
    }

}
