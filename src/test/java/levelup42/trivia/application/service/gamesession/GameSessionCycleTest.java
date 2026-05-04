package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.model.SessionType;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameSessionCycleTest {

    @Mock
    private GameSessionRepositoryPort sessionRepository;

    private StartGameSessionService startGameSessionService;
    private UUID playerId;
    private String subject;
    private List<GameSession> allSessions;

    @BeforeEach
    void setUp() {
        startGameSessionService = new StartGameSessionService(sessionRepository);
        playerId = UUID.randomUUID();
        subject = "Desarrollo de Interfaces";
        allSessions = new ArrayList<>();
        
        when(sessionRepository.findByPlayerId(playerId)).thenAnswer(invocation -> allSessions);
        when(sessionRepository.save(any(GameSession.class))).thenAnswer(invocation -> {
            GameSession session = invocation.getArgument(0);
            allSessions.add(session);
            return session;
        });
    }

    @Test
    void testFiveCyclesLeadToReview() {
        // Create 5 sessions for the same subject
        GameSession s1 = startGameSessionService.createSession(playerId, subject, 10);
        s1.finish();
        assertEquals(1, s1.getTestCycleIndex());
        assertEquals(SessionType.NORMAL, s1.getSessionType());

        GameSession s2 = startGameSessionService.createSession(playerId, subject, 10);
        s2.finish();
        assertEquals(2, s2.getTestCycleIndex());
        assertEquals(SessionType.NORMAL, s2.getSessionType());

        GameSession s3 = startGameSessionService.createSession(playerId, subject, 10);
        s3.finish();
        assertEquals(3, s3.getTestCycleIndex());
        assertEquals(SessionType.NORMAL, s3.getSessionType());

        GameSession s4 = startGameSessionService.createSession(playerId, subject, 10);
        s4.finish();
        assertEquals(4, s4.getTestCycleIndex());
        assertEquals(SessionType.NORMAL, s4.getSessionType());

        GameSession s5 = startGameSessionService.createSession(playerId, subject, 10);
        assertEquals(5, s5.getTestCycleIndex());
        assertEquals(SessionType.REVIEW, s5.getSessionType());
    }

    @Test
    void testReviewCycleResetsIndex() {
        // Create 5 sessions (1-4 NORMAL, 5 REVIEW)
        for (int i = 1; i <= 5; i++) {
            GameSession s = startGameSessionService.createSession(playerId, subject, 10);
            assertEquals(i, s.getTestCycleIndex());
            assertEquals(i == 5 ? SessionType.REVIEW : SessionType.NORMAL, s.getSessionType());
            s.finish();
        }

        // After REVIEW, next session should be cycle 1 NORMAL
        GameSession s6 = startGameSessionService.createSession(playerId, subject, 10);
        assertEquals(1, s6.getTestCycleIndex());
        assertEquals(SessionType.NORMAL, s6.getSessionType());
    }

    @Test
    void testMultipleSubjectsIndependent() {
        String subject2 = "Acceso a datos";

        // Create 5 sessions for subject1
        for (int i = 1; i <= 5; i++) {
            GameSession s = startGameSessionService.createSession(playerId, subject, 10);
            assertEquals(i, s.getTestCycleIndex());
            assertEquals(i == 5 ? SessionType.REVIEW : SessionType.NORMAL, s.getSessionType());
            s.finish();
        }

        // subject2 should start at cycle 1
        GameSession s_subj2 = startGameSessionService.createSession(playerId, subject2, 10);
        assertEquals(1, s_subj2.getTestCycleIndex());
        assertEquals(SessionType.NORMAL, s_subj2.getSessionType());
    }
}
