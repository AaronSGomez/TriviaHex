package levelup42.trivia.application.service;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.CreateSessionUseCase;
import levelup42.trivia.domain.port.out.SessionRepositoryPort;

import java.util.UUID;

public class CreateSessionService implements CreateSessionUseCase {

    private final SessionRepositoryPort sessionRepository;

    public CreateSessionService(SessionRepositoryPort sessionRepository){
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
