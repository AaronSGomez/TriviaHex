package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.gamesession.StartGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class StartGameSessionService implements StartGameSessionUseCase {

    private static final Logger log = LoggerFactory.getLogger(StartGameSessionService.class);

    private final GameSessionRepositoryPort sessionRepository;

    public StartGameSessionService(GameSessionRepositoryPort sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    @Override
    public GameSession createSession(UUID playerId, String subject, int totalQuestions) {
        // Determine last finished session for this player and subject to compute cycle index
        int nextCycleIndex = 1;
        levelup42.trivia.domain.model.SessionType nextSessionType = levelup42.trivia.domain.model.SessionType.NORMAL;

        try {
            java.util.List<levelup42.trivia.domain.model.GameSession> sessions = sessionRepository.findByPlayerId(playerId);
            java.util.Optional<levelup42.trivia.domain.model.GameSession> lastFinished = sessions.stream()
                    .filter(s -> s.isFinished() && subject != null && subject.equalsIgnoreCase(s.getSubject()))
                    .sorted((a,b) -> {
                        java.time.Instant fa = a.getFinishedAt() != null ? a.getFinishedAt() : java.time.Instant.EPOCH;
                        java.time.Instant fb = b.getFinishedAt() != null ? b.getFinishedAt() : java.time.Instant.EPOCH;
                        return fa.compareTo(fb);  // Sort ascending, then findLast to get most recent
                    })
                    .reduce((first, second) -> second);  // Get the last element (most recent)

            if (lastFinished.isPresent()) {
                int lastIndex = lastFinished.get().getTestCycleIndex();
                levelup42.trivia.domain.model.SessionType lastType = lastFinished.get().getSessionType();
                
                if (lastType == levelup42.trivia.domain.model.SessionType.REVIEW) {
                    // After a REVIEW, reset cycle to 1 with NORMAL
                    nextCycleIndex = 1;
                    nextSessionType = levelup42.trivia.domain.model.SessionType.NORMAL;
                } else if (lastIndex < 5) {
                    // Continue with NORMAL sessions (1, 2, 3, 4 -> 5 becomes REVIEW)
                    nextCycleIndex = lastIndex + 1;
                    nextSessionType = (nextCycleIndex == 5) ? levelup42.trivia.domain.model.SessionType.REVIEW : levelup42.trivia.domain.model.SessionType.NORMAL;
                }
            }
        } catch (Exception e) {
            log.warn("Could not determine last session for player {} subject {}: {}", playerId, subject, e.getMessage());
        }

        GameSession session = new GameSession(
                UUID.randomUUID(),
                playerId,
                subject,
                totalQuestions
        );
        session.setTestCycleIndex(nextCycleIndex);
        session.setSessionType(nextSessionType);

        if (nextSessionType == levelup42.trivia.domain.model.SessionType.REVIEW) {
            try {
                java.util.List<Long> failedIds = sessionRepository.findFailedQuestionIdsByPlayerAndSubject(playerId, subject);
                session.setReviewQuestionCount(failedIds.size());
            } catch (Exception e) {
                log.warn("Could not determine review question count for player {} subject {}: {}", playerId, subject, e.getMessage());
            }
        }

        GameSession createdSession = sessionRepository.save(session);
        log.info("session_started sessionId={} playerId={} subject={} totalQuestions={} cycle={} type={}", createdSession.getId(), playerId, subject, totalQuestions, nextCycleIndex, nextSessionType);
        return createdSession;
    }
}
