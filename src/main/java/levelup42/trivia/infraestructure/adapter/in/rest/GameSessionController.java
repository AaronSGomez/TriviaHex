package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.gamesession.StartGameSessionUseCase;
import levelup42.trivia.domain.port.in.gamesession.SubmitAnswerUseCase;
import levelup42.trivia.domain.port.in.gamesession.FinishGameSessionUseCase;
import levelup42.trivia.domain.port.in.gamesession.GetGameSessionUseCase;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.GameSessionRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.GameSessionResponse;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.SubmitAnswerRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/session")
public class GameSessionController {

    private final StartGameSessionUseCase startGameSessionUseCase;
    private final SubmitAnswerUseCase submitAnswerUseCase;
    private final FinishGameSessionUseCase finishGameSessionUseCase;
    private final GetGameSessionUseCase getGameSessionUseCase;
    private final levelup42.trivia.domain.port.in.gamesession.GetNextQuestionUseCase getNextQuestionUseCase;

    public GameSessionController(StartGameSessionUseCase startGameSessionUseCase, SubmitAnswerUseCase submitAnswerUseCase, FinishGameSessionUseCase finishGameSessionUseCase, GetGameSessionUseCase getGameSessionUseCase, levelup42.trivia.domain.port.in.gamesession.GetNextQuestionUseCase getNextQuestionUseCase) {
        this.startGameSessionUseCase = startGameSessionUseCase;
        this.submitAnswerUseCase = submitAnswerUseCase;
        this.finishGameSessionUseCase = finishGameSessionUseCase;
        this.getGameSessionUseCase = getGameSessionUseCase;
        this.getNextQuestionUseCase = getNextQuestionUseCase;
    }

    @PostMapping
    public GameSessionResponse create(@Valid @RequestBody GameSessionRequest gameSessionRequest) {
        GameSession created = startGameSessionUseCase.createSession(
                gameSessionRequest.getPlayerId(),
                gameSessionRequest.getSubject(),
                gameSessionRequest.getTotalQuestions());
        return GameSessionResponse.fromDomain(created);
    }

    @PostMapping("/{sessionId}/answer")
    public SubmitAnswerUseCase.AnswerResult answerQuestion(@PathVariable UUID sessionId,
            @Valid @RequestBody SubmitAnswerRequest submitAnswerRequest) {
        return submitAnswerUseCase.answerQuestion(sessionId,
                submitAnswerRequest.getQuestionId(), submitAnswerRequest.getSelectedOption(), submitAnswerRequest.getTimeElapsedSeconds());
    }

    @PostMapping("/{sessionId}/finish")
    public GameSessionResponse finishSession(@PathVariable UUID sessionId) {
        return GameSessionResponse.fromDomain(finishGameSessionUseCase.finishSession(sessionId));
    }

    @GetMapping("/{sessionId}")
    public GameSessionResponse getSessionById(@PathVariable UUID sessionId) {
        return GameSessionResponse.fromDomain(getGameSessionUseCase.getSessionById(sessionId));
    }

    @GetMapping("/player/{playerId}")
    public List<GameSessionResponse> getPlayerHistory(@PathVariable UUID playerId) {
        return getGameSessionUseCase.getPlayerHistory(playerId).stream().map(GameSessionResponse::fromDomain).toList();
    }

    @GetMapping("/leaderboard")
    public List<GameSessionResponse> getLeaderboard() {
        return getGameSessionUseCase.getLeaderboard().stream().map(GameSessionResponse::fromDomain).toList();
    }

    @GetMapping("/leaderboard/weekly")
    public List<GameSessionResponse> getWeeklyLeaderboard() {
        return getGameSessionUseCase.getWeeklyLeaderboard().stream().map(GameSessionResponse::fromDomain).toList();
    }

    @GetMapping("/{sessionId}/next-question")
    public org.springframework.http.ResponseEntity<levelup42.trivia.infraestructure.adapter.in.rest.dto.CurrentQuestionResponse> getNextQuestion(@PathVariable UUID sessionId) {
        return getNextQuestionUseCase.getNextQuestion(sessionId)
                .map(question -> org.springframework.http.ResponseEntity.ok(levelup42.trivia.infraestructure.adapter.in.rest.dto.CurrentQuestionResponse.fromDomain(question)))
                .orElse(org.springframework.http.ResponseEntity.notFound().build());
    }
}
