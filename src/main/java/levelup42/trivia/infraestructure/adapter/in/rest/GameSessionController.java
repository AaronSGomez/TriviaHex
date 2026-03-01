package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.gamesession.StartGameSessionUseCase;
import levelup42.trivia.domain.port.in.gamesession.SubmitAnswerUseCase;
import levelup42.trivia.domain.port.in.gamesession.FinishGameSessionUseCase;
import levelup42.trivia.domain.port.in.gamesession.GetGameSessionUseCase;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.GameSessionRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.SubmitAnswerRequest;
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

    public GameSessionController(StartGameSessionUseCase startGameSessionUseCase, SubmitAnswerUseCase submitAnswerUseCase, FinishGameSessionUseCase finishGameSessionUseCase, GetGameSessionUseCase getGameSessionUseCase) {
        this.startGameSessionUseCase = startGameSessionUseCase;
        this.submitAnswerUseCase = submitAnswerUseCase;
        this.finishGameSessionUseCase = finishGameSessionUseCase;
        this.getGameSessionUseCase = getGameSessionUseCase;
    }

    @PostMapping
    public GameSession create(@RequestBody GameSessionRequest gameSessionRequest) {
        return startGameSessionUseCase.createSession(
                gameSessionRequest.getPlayerId(),
                gameSessionRequest.getSubjet(),
                gameSessionRequest.getTotalQuestions());
    }

    @PostMapping("/{sessionId}/answer")
    public SubmitAnswerUseCase.AnswerResult answerQuestion(@PathVariable UUID sessionId,
            @RequestBody SubmitAnswerRequest submitAnswerRequest) {
        return submitAnswerUseCase.answerQuestion(sessionId,
                submitAnswerRequest.getQuestionId(), submitAnswerRequest.getSelectedOption(), submitAnswerRequest.getTimeElapsedSeconds());
    }

    @PostMapping("/{sessionId}/finish")
    public GameSession finishSession(@PathVariable UUID sessionId) {
        return finishGameSessionUseCase.finishSession(sessionId);
    }

    @GetMapping("/{sessionId}")
    public GameSession getSessionById(@PathVariable UUID sessionId) {
        return getGameSessionUseCase.getSessionById(sessionId);
    }

    @GetMapping("/player/{playerId}")
    public List<GameSession> getPlayerHistory(@PathVariable UUID playerId) {
        return getGameSessionUseCase.getPlayerHistory(playerId);
    }

    @GetMapping("/leaderboard")
    public List<GameSession> getLeaderboard() {
        return getGameSessionUseCase.getLeaderboard();
    }
}
