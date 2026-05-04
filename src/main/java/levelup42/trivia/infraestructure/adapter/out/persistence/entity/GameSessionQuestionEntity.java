package levelup42.trivia.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "gamesession_question")
public class GameSessionQuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id", nullable = false)
    private UUID sessionId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;
    
    @Column(name = "correct")
    private Boolean correct;

    @Column(name = "answered_at")
    private java.time.Instant answeredAt;

    public GameSessionQuestionEntity() {
    }

    public GameSessionQuestionEntity(UUID sessionId, Long questionId) {
        this.sessionId = sessionId;
        this.questionId = questionId;
        this.correct = null;
        this.answeredAt = null;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Boolean getCorrect() { return correct; }

    public void setCorrect(Boolean correct) { this.correct = correct; }

    public java.time.Instant getAnsweredAt() { return answeredAt; }

    public void setAnsweredAt(java.time.Instant answeredAt) { this.answeredAt = answeredAt; }
}
