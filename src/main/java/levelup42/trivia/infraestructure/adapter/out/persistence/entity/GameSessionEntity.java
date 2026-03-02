package levelup42.trivia.infraestructure.adapter.out.persistence.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import levelup42.trivia.domain.model.SessionStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name= "gamesession")
public class GameSessionEntity {

    @Id
    private UUID id;
    private UUID playerId;
    private String subject;

    private int totalQuestions;
    private int answeredQuestions;
    private int correctAnswers;
    private int score;

    private Instant startedAt;
    private Instant finishedAt;

    private SessionStatus status;

    // JPA requiere constructor por defecto
    public GameSessionEntity() {
    }

    public GameSessionEntity(UUID id, UUID playerId, String subject, int totalQuestions) {
        this.id = id;
        this.playerId = playerId;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = 0;
        this.correctAnswers = 0;
        this.score = 0;
        this.startedAt = Instant.now();
        this.status = SessionStatus.IN_PROGRESS;
    }

    // Constructor completo para el mapper
    public GameSessionEntity(UUID id, UUID playerId, String subject, int totalQuestions, int answeredQuestions,
                             int correctAnswers, int score, Instant startedAt, Instant finishedAt, SessionStatus status) {
        this.id = id;
        this.playerId = playerId;
        this.subject = subject;
        this.totalQuestions = totalQuestions;
        this.answeredQuestions = answeredQuestions;
        this.correctAnswers = correctAnswers;
        this.score = score;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
        this.status = status;
    }

    public void registerCorrectAnswer() {
        this.correctAnswers++;
        this.score+=100;
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

    public String getSubject() {return subject;}

    public int getTotalQuestions() {return totalQuestions;}

    public int getAnsweredQuestions() {return answeredQuestions;}

    public int getCorrectAnswers() {return correctAnswers;}

    public int getScore() {return score;}

    public Instant getStartedAt() {return startedAt;}

    public Instant getFinishedAt() {return finishedAt;}

    public SessionStatus getStatus() {return status;}

}