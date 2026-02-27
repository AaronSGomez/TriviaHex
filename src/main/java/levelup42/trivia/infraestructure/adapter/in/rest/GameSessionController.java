package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.port.in.CreateGameSessionUseCase;
import levelup42.trivia.domain.port.in.AnswerQuestionUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/session")
public class GameSessionController {

    private final CreateGameSessionUseCase createGameSessionUseCase;
    private final AnswerQuestionUseCase answerQuestionUseCase;

    public GameSessionController(CreateGameSessionUseCase createGameSessionUseCase, AnswerQuestionUseCase answerQuestionUseCase) {
        this.createGameSessionUseCase = createGameSessionUseCase;
        this.answerQuestionUseCase = answerQuestionUseCase;
    }

    @PostMapping
    public UUID create(@RequestParam UUID playerID,
                       @RequestParam String subjet,
                       @RequestParam int totalQuestions) {
        return createGameSessionUseCase.createSession(playerID,subjet,totalQuestions);
    }

    @PostMapping("/{sessionId}/answer")
    public AnswerQuestionUseCase.AnswerResult answerQuestion(
            @PathVariable UUID sessionId,
            @RequestParam Long questionId,
            @RequestParam String selectedOption) {
        return answerQuestionUseCase.answerQuestion(sessionId, questionId, selectedOption);
    }
}
