package levelup42.trivia.infraestructure.adapter.in.rest.dto;
import java.util.UUID;

public class GameSessionRequest {

    private final UUID playerId;
    private final String subjet;
    private int totalQuestions;


    public GameSessionRequest(UUID playerId, String subjet, int totalQuestions) {
        this.playerId = playerId;
        this.subjet = subjet;
        this.totalQuestions = totalQuestions;
    }

    // getters

    public UUID getPlayerId() {return playerId;}

    public String getSubjet() {return subjet;}

    public int getTotalQuestions() {return totalQuestions;}

}
