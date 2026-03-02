package levelup42.trivia.infraestructure.adapter.in.rest.dto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class GameSessionRequest {

    @NotNull(message = "Player ID cannot be null")
    private final UUID playerId;

    @NotBlank(message = "Subject cannot be empty")
    private final String subject;

    @Min(value = 1, message = "Total questions must be at least 1")
    private int totalQuestions;


    public GameSessionRequest(UUID playerId, String subject, int totalQuestions) {
        this.playerId = playerId;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
    }

    // getters

    public UUID getPlayerId() {return playerId;}

    public String getSubject() {return subject;}

    public int getTotalQuestions() {return totalQuestions;}

}
