package levelup42.trivia.infraestructure.adapter.in.rest.dto;

import levelup42.trivia.domain.model.GameSession;

import java.time.Instant;
import java.util.UUID;

public class GameSessionResponse {
    private final UUID id;
    private final UUID playerId;
    private final String subjet;
    private final int totalQuestions;
    private final int answeredQuestions;
    private final int correctAnswers;
    private final int score;
    private final Instant startedAt;
    private final Instant finishedAt;
    private final String status; // Simplificamos el enum a String para el JSON
    private final double grade;
    private final boolean isPassed;

    public GameSessionResponse(UUID id, UUID playerId, String subjet, int totalQuestions, int answeredQuestions, 
                               int correctAnswers, int score, Instant startedAt, Instant finishedAt, String status, 
                               double grade, boolean isPassed) {
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
        this.grade = grade;
        this.isPassed = isPassed;
    }

    public static GameSessionResponse fromDomain(GameSession session) {
        return new GameSessionResponse(
                session.getId(),
                session.getPlayerId(),
                session.getSubjet(),
                session.getTotalQuestions(),
                session.getAnsweredQuestions(),
                session.getCorrectAnswers(),
                session.getScore(),
                session.getStartedAt(),
                session.getFinishedAt(),
                session.getStatus().name(),
                session.getGrade(),
                session.isPassed()
        );
    }

    public UUID getId() { return id; }
    public UUID getPlayerId() { return playerId; }
    public String getSubjet() { return subjet; }
    public int getTotalQuestions() { return totalQuestions; }
    public int getAnsweredQuestions() { return answeredQuestions; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getScore() { return score; }
    public Instant getStartedAt() { return startedAt; }
    public Instant getFinishedAt() { return finishedAt; }
    public String getStatus() { return status; }
    public double getGrade() { return grade; }
    public boolean isPassed() { return isPassed; }
}
