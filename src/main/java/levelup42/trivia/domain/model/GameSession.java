package levelup42.trivia.domain.model;

import java.time.Instant;
import java.util.UUID;

public class GameSession {

    private final UUID id;
    private final UUID playerId;
    private final String subjet;

    private int totalQuestions;
    private int correctAnswers;
    private int score;

    private Instant startedAt;
    private Instant finishedAt;

    private SessionStatus status;

    public GameSession(UUID id, UUID playerId, String subjet, int totalQuestions) {
        this.id = id;
        this.playerId = playerId;
        this.subjet = subjet;
        this.totalQuestions = totalQuestions;
        this.correctAnswers = 0;
        this.score = 0;
        this.startedAt = Instant.now();
        this.status = SessionStatus.IN_PROGRESS;
    }

    public void registerCorrectAnswer() {
        this.correctAnswers++;
        this.score+=100;
    }

    public void registerIncorrectAnswer() {
        // no registra nada
    }

    public void finish(){
        this.status = SessionStatus.FINISHED;
        this.finishedAt = Instant.now();
    }

    public boolean isFinished() {
        return this.status == SessionStatus.FINISHED;
    }

    // getters

    public UUID getId() {return id;}

    public UUID getPlayerId() {return playerId;}

    public String getSubjet() {return subjet;}

    public int getTotalQuestions() {return totalQuestions;}

    public int getCorrectAnswers() {return correctAnswers;}

    public int getScore() {return score;}

    public Instant getStartedAt() {return startedAt;}

    public Instant getFinishedAt() {return finishedAt;}

    public SessionStatus getStatus() {return status;}

}
