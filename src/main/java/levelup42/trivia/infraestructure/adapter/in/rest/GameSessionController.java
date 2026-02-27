package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.port.in.CreateGameSessionUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/session")
public class GameSessionController {

    private final CreateGameSessionUseCase createGameSessionUseCase;

    public GameSessionController(CreateGameSessionUseCase createGameSessionUseCase) {
        this.createGameSessionUseCase = createGameSessionUseCase;
    }

    @PostMapping
    public UUID create(@RequestParam UUID playerID,
                       @RequestParam String subjet,
                       @RequestParam int totalQuestions) {
        return createGameSessionUseCase.createSession(playerID,subjet,totalQuestions);
    }

}
