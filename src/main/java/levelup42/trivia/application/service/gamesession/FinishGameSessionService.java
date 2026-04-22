package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.gamesession.FinishGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FinishGameSessionService implements FinishGameSessionUseCase {

    private static final Logger log = LoggerFactory.getLogger(FinishGameSessionService.class);

    private final GameSessionRepositoryPort sessionRepository;

    public FinishGameSessionService(GameSessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public GameSession finishSession(UUID sessionId) {
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!session.isFinished()) {
            session.finish();
            session = sessionRepository.save(session);
        }

        log.info("session_finished sessionId={} playerId={} score={} totalQuestions={}", sessionId, session.getPlayerId(), session.getScore(), session.getTotalQuestions());

        return session;
    }
}
