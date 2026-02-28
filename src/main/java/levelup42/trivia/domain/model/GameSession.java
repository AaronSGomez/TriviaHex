package levelup42.trivia.domain.model;

import java.time.Instant;
import java.util.UUID;

public class GameSession {

    private final UUID id;
    private final UUID playerId;
    private final String subjet;

    private int totalQuestions;
    private int answeredQuestions;
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
        this.answeredQuestions = 0;
        this.correctAnswers = 0;
        this.score = 0;
        this.startedAt = Instant.now();
        this.status = SessionStatus.IN_PROGRESS;
    }

    // Constructor completo para rehidratar desde la base de datos
    public GameSession(UUID id, UUID playerId, String subjet, int totalQuestions, int answeredQuestions,
                       int correctAnswers, int score, Instant startedAt, Instant finishedAt, SessionStatus status) {
        this.id = id;
        this.playerId = playerId;
        this.subjet = subjet;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = answeredQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.status = status;
    }

    public void registerCorrectAnswer(int points) {
        this.correctAnswers++;
        this.score+=points;
        this.answeredQuestions++;
    }

    public void registerIncorrectAnswer() {
        this.answeredQuestions++;
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

    public int getAnsweredQuestions() {return answeredQuestions;}

    public int getCorrectAnswers() {return correctAnswers;}

    public int getScore() {return score;}

    public Instant getStartedAt() {return startedAt;}

    public Instant getFinishedAt() {return finishedAt;}

    public SessionStatus getStatus() {return status;}

}
