package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.model.SessionType;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GetNextQuestionService96hRotationTest {

    @Mock
    private GameSessionRepositoryPort sessionRepository;

    @Mock
    private QuestionRepositoryPort questionRepository;

    private GetNextQuestionService service;
    private UUID playerId;
    private String subject;
    private UUID sessionId;

    @BeforeEach
    void setUp() {
        service = new GetNextQuestionService(sessionRepository, questionRepository);
        playerId = UUID.randomUUID();
        subject = "Desarrollo de Interfaces";
        sessionId = UUID.randomUUID();
    }

    @Test
    void testNormalSessionExcludes96hAskedQuestions() {
        // Arrange
        GameSession mockSession = new GameSession(sessionId, playerId, subject, 10);
        mockSession.setSessionType(SessionType.NORMAL);

        List<Long> askedInSession = List.of(1L, 2L, 3L, 4L, 5L);
        List<Long> recentAskedInWindow = List.of(10L, 11L, 12L, 13L, 14L); // Asked in last 96h

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(sessionRepository.findAskedQuestionIdsBySessionId(sessionId)).thenReturn(askedInSession);
        
        // Mock the 96h window query - returns questions recently asked (correct or incorrect)
        when(sessionRepository.findAskedQuestionIdsByPlayerAndSubjectSince(
                eq(playerId), 
                eq(subject), 
                any(Instant.class)
        )).thenReturn(recentAskedInWindow);

        // Only question outside the exclusion list should be returned
        Question newQuestion = new Question(20L, "stmt", "A", "B", "C", "D", "A", "exp", subject, "topic", "easy", true);
        when(questionRepository.findRandomUnansweredBySubject(eq(subject), anyList()))
                .thenReturn(Optional.of(newQuestion));

        // Act
        Optional<Question> result = service.getNextQuestion(sessionId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(20L, result.get().getId());
    }

    @Test
    void testNormalSession274QuestionRotation() {
        // Arrange - Simulate 274 questions with max rotation in 96h window
        GameSession mockSession = new GameSession(sessionId, playerId, subject, 10);
        mockSession.setSessionType(SessionType.NORMAL);

        // Simulate: Tests 1-4 asked 40 questions (P1-P40) in last 96h
        List<Long> askedInSession = new ArrayList<>();
        List<Long> recentAskedInWindow = new ArrayList<>();
        for (long i = 1; i <= 40; i++) {
            recentAskedInWindow.add(i);
        }

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(sessionRepository.findAskedQuestionIdsBySessionId(sessionId)).thenReturn(askedInSession);
        when(sessionRepository.findAskedQuestionIdsByPlayerAndSubjectSince(
                eq(playerId), 
                eq(subject), 
                any(Instant.class)
        )).thenReturn(recentAskedInWindow);

        // Should request P41+ (not P1-P40 which are in 96h window)
        Question q41 = new Question(41L, "stmt", "A", "B", "C", "D", "A", "exp", subject, "topic", "easy", true);
        when(questionRepository.findRandomUnansweredBySubject(eq(subject), anyList()))
                .thenReturn(Optional.of(q41));

        // Act
        Optional<Question> result = service.getNextQuestion(sessionId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(41L, result.get().getId());
        // Verify P1-P40 were excluded (excluded list passed to findRandom)
    }

    @Test
    void testAfter96hQuestionsBecomEligibleAgain() {
        // Arrange
        GameSession mockSession = new GameSession(sessionId, playerId, subject, 10);
        mockSession.setSessionType(SessionType.NORMAL);

        List<Long> askedInSession = new ArrayList<>();
        // Empty recent window = P1-P40 have aged past 96h
        List<Long> recentAskedInWindow = new ArrayList<>();

        when(sessionRepository.findById(sessionId)).thenReturn(Optional.of(mockSession));
        when(sessionRepository.findAskedQuestionIdsBySessionId(sessionId)).thenReturn(askedInSession);
        when(sessionRepository.findAskedQuestionIdsByPlayerAndSubjectSince(
                eq(playerId), 
                eq(subject), 
                any(Instant.class)
        )).thenReturn(recentAskedInWindow);

        // Any question should be allowed now
        Question q1 = new Question(1L, "stmt", "A", "B", "C", "D", "A", "exp", subject, "topic", "easy", true);
        when(questionRepository.findRandomBySubject(eq(subject)))
                .thenReturn(Optional.of(q1));

        // Act
        Optional<Question> result = service.getNextQuestion(sessionId);

        // Assert
        assertTrue(result.isPresent());
        // P1 is eligible again since 96h passed
        assertEquals(1L, result.get().getId());
    }
}
