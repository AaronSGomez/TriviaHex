package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.port.in.CreateSessionUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/session")
public class SessionController {

    private final CreateSessionUseCase createSessionUseCase;

    public SessionController(CreateSessionUseCase createSessionUseCase) {
        this.createSessionUseCase = createSessionUseCase;
    }

    @PostMapping
    public UUID create(@RequestParam UUID playerID,
                       @RequestParam String subjet,
                       @RequestParam int totalQuestions) {
        return createSessionUseCase.createSession(playerID,subjet,totalQuestions);
    }

}
