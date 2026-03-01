package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.gamesession.FinishGameSessionUseCase;
import levelup42.trivia.domain.port.in.gamesession.GetGameSessionUseCase;
import levelup42.trivia.domain.port.in.gamesession.StartGameSessionUseCase;
import levelup42.trivia.domain.port.in.gamesession.SubmitAnswerUseCase;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.GameSessionRequest;
import levelup42.trivia.infraestructure.config.exception.GlobalExceptionHandler;
import levelup42.trivia.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GameSessionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private StartGameSessionUseCase startGameSessionUseCase;

    @Mock
    private SubmitAnswerUseCase submitAnswerUseCase;

    @Mock
    private FinishGameSessionUseCase finishGameSessionUseCase;

    @Mock
    private GetGameSessionUseCase getGameSessionUseCase;

    @Mock
    private levelup42.trivia.domain.port.in.gamesession.GetNextQuestionUseCase getNextQuestionUseCase;

    @InjectMocks
    private GameSessionController gameSessionController;

    private UUID playerId;
    private UUID sessionId;
    private GameSession mockSession;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(gameSessionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        playerId = UUID.randomUUID();
        sessionId = UUID.randomUUID();
        mockSession = new GameSession(sessionId, playerId, "History", 5);
    }

    @Test
    void createSession_WithValidRequest_ReturnsCreatedSession() throws Exception {
        // Arrange
        when(startGameSessionUseCase.createSession(any(UUID.class), any(String.class), any(Integer.class)))
                .thenReturn(mockSession);

        String requestJson = String.format("{\"playerId\":\"%s\",\"subjet\":\"History\",\"totalQuestions\":5}", playerId.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.playerId").value(playerId.toString()))
                .andExpect(jsonPath("$.subjet").value("History"))
                .andExpect(jsonPath("$.totalQuestions").value(5))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    void createSession_WithInvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange - request without subject (violates @NotBlank)
        String requestJson = String.format("{\"playerId\":\"%s\",\"subjet\":\"\",\"totalQuestions\":5}", playerId.toString());

        // Act & Assert
        mockMvc.perform(post("/api/v1/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void answerQuestion_WithValidRequest_ReturnsAnswerResult() throws Exception {
        // Arrange
        Long questionId = 1L;
        String selectedOption = "Paris";
        int timeElapsed = 10;
        
        SubmitAnswerUseCase.AnswerResult mockResult = new SubmitAnswerUseCase.AnswerResult(
                true, "Paris", "Correct!", 10, false
        );
        
        when(submitAnswerUseCase.answerQuestion(eq(sessionId), eq(questionId), eq(selectedOption), eq(timeElapsed)))
                .thenReturn(mockResult);

        String requestJson = String.format("{\"questionId\":%d,\"selectedOption\":\"%s\",\"timeElapsedSeconds\":%d}", questionId, selectedOption, timeElapsed);

        // Act & Assert
        mockMvc.perform(post("/api/v1/session/" + sessionId + "/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isCorrect").value(true))
                .andExpect(jsonPath("$.explanation").value("Correct!"));
    }

    @Test
    void getSessionById_WhenFound_ReturnsSession() throws Exception {
        // Arrange
        when(getGameSessionUseCase.getSessionById(sessionId)).thenReturn(mockSession);

        // Act & Assert
        mockMvc.perform(get("/api/v1/session/" + sessionId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.playerId").value(playerId.toString()))
                .andExpect(jsonPath("$.subjet").value("History"))
                .andExpect(jsonPath("$.totalQuestions").value(5))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.grade").value(0.0))
                .andExpect(jsonPath("$.passed").value(false));
    }

    @Test
    void getSessionById_WhenNotFound_ReturnsNotFound() throws Exception {
        // Arrange
        when(getGameSessionUseCase.getSessionById(any(UUID.class)))
                .thenThrow(new ResourceNotFoundException("Session not found"));

        // Act & Assert
        mockMvc.perform(get("/api/v1/session/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Session not found"));
    }

    @Test
    void finishSession_ReturnsFinishedSession() throws Exception {
        // Arrange
        when(finishGameSessionUseCase.finishSession(sessionId)).thenReturn(mockSession);

        // Act & Assert
        mockMvc.perform(post("/api/v1/session/" + sessionId + "/finish")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(sessionId.toString()))
                .andExpect(jsonPath("$.playerId").value(playerId.toString()))
                .andExpect(jsonPath("$.grade").value(0.0))
                .andExpect(jsonPath("$.passed").value(false))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS")); // the mock is in progress
    }

    @Test
    void getPlayerHistory_ReturnsListOfSessions() throws Exception {
        // Arrange
        when(getGameSessionUseCase.getPlayerHistory(playerId)).thenReturn(List.of(mockSession));

        // Act & Assert
        mockMvc.perform(get("/api/v1/session/player/" + playerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sessionId.toString()))
                .andExpect(jsonPath("$[0].playerId").value(playerId.toString()))
                .andExpect(jsonPath("$[0].grade").value(0.0))
                .andExpect(jsonPath("$[0].passed").value(false));
    }

    @Test
    void getLeaderboard_ReturnsListOfSessions() throws Exception {
        // Arrange
        when(getGameSessionUseCase.getLeaderboard()).thenReturn(List.of(mockSession));

        // Act & Assert
        mockMvc.perform(get("/api/v1/session/leaderboard")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(sessionId.toString()))
                .andExpect(jsonPath("$[0].playerId").value(playerId.toString()))
                .andExpect(jsonPath("$[0].grade").value(0.0))
                .andExpect(jsonPath("$[0].passed").value(false));
    }

    @Test
    void getNextQuestion_WhenQuestionAvailable_ReturnsQuestion() throws Exception {
        // Arrange
        // Question constructor: (id, statement, optionA, optionB, optionC, optionD, correctOption, explanation, subject, topic, difficulty, active)
        levelup42.trivia.domain.model.Question mockQuestion = new levelup42.trivia.domain.model.Question(
                1L, "What is 2+2?", "3", "4", "5", "6", "4", "Basic math", "Math", "Addition", "Easy", true);
        when(getNextQuestionUseCase.getNextQuestion(sessionId)).thenReturn(java.util.Optional.of(mockQuestion));

        // Act & Assert
        mockMvc.perform(get("/api/v1/session/" + sessionId + "/next-question")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.statement").value("What is 2+2?"))
                .andExpect(jsonPath("$.optionA").value("3"))
                .andExpect(jsonPath("$.subject").value("Math"))
                .andExpect(jsonPath("$.correctOption").doesNotExist());
    }

    @Test
    void getNextQuestion_WhenNoQuestionAvailable_ReturnsNotFound() throws Exception {
        // Arrange
        when(getNextQuestionUseCase.getNextQuestion(sessionId)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/api/v1/session/" + sessionId + "/next-question")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
