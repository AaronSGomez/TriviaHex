package levelup42.trivia.domain.model;

import java.time.Instant;
import java.util.UUID;

public class GameSession {

    private final UUID id;
    private final UUID playerId;
    private final String subject;

    private int totalQuestions;
    private int answeredQuestions;
    private int correctAnswers;
    private int skippedAnswers;
    private int score;

    private Instant startedAt;
    private Instant finishedAt;

    private SessionStatus status;

    public GameSession(UUID id, UUID playerId, String subject, int totalQuestions) {
        this.id = id;
        this.playerId = playerId;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = 0;
        this.correctAnswers = 0;
        this.skippedAnswers = 0;
        this.score = 0;
        this.startedAt = Instant.now();
        this.status = SessionStatus.IN_PROGRESS;
    }

    // Constructor completo para rehidratar desde la base de datos
    public GameSession(UUID id, UUID playerId, String subject, int totalQuestions, int answeredQuestions,
                       int correctAnswers, int skippedAnswers, int score, Instant startedAt, Instant finishedAt, SessionStatus status) {
        this.id = id;
        this.playerId = playerId;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = answeredQuestions;
        this.correctAnswers = correctAnswers;
        this.skippedAnswers = skippedAnswers;
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

    public void registerSkippedAnswer() {
        this.skippedAnswers++;
        this.answeredQuestions++;
    }

    public void finish(){
        this.status = SessionStatus.FINISHED;
        this.finishedAt = Instant.now();
    }

    public boolean isFinished() {
        return this.status == SessionStatus.FINISHED;
    }

    /**
     * Calculates the exam grade on a scale from 0 to 10.
     * Each correct answer adds a proportional value (10 / totalQuestions).
     * Each incorrect answer subtracts 1/3 of the value of a single question.
     * The grade is floored at 0 (cannot be negative).
     */
    public double getGrade() {
        if (totalQuestions == 0) return 0.0;
        
        double questionValue = 10.0 / totalQuestions;
        double penaltyValue = questionValue / 3.0;
        
        int incorrectAnswers = answeredQuestions - correctAnswers - skippedAnswers;
        
        double rawGrade = (correctAnswers * questionValue) - (incorrectAnswers * penaltyValue);
        
        // Ensure grade is between 0 and 10 and rounded to 2 decimal places
        double finalGrade = Math.max(0.0, Math.min(10.0, rawGrade));
        return Math.round(finalGrade * 100.0) / 100.0;
    }

    public boolean isPassed() {
        return getGrade() >= 5.0;
    }

    // getters

    public UUID getId() {return id;}

    public UUID getPlayerId() {return playerId;}

    public String getSubject() {return subject;}

    public int getTotalQuestions() {return totalQuestions;}

    public int getAnsweredQuestions() {return answeredQuestions;}

    public int getCorrectAnswers() {return correctAnswers;}

    public int getSkippedAnswers() {return skippedAnswers;}

    public int getScore() {return score;}

    public Instant getStartedAt() {return startedAt;}

    public Instant getFinishedAt() {return finishedAt;}

    public SessionStatus getStatus() {return status;}

}
