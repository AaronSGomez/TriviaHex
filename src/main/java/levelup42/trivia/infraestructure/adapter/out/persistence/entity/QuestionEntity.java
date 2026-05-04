package levelup42.trivia.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "question")
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 2000)
    private String statement;

    @Column(length = 500)
    private String optionA;
    @Column(length = 500)
    private String optionB;
    @Column(length = 500)
    private String optionC;
    @Column(length = 500)
    private String optionD;

    @Column(length = 10)
    private String correctOption;

    @Column(length = 2000)
    private String explanation;

    @Column(length = 200)
    private String subject;
    @Column(length = 200)
    private String topic;
    @Column(length = 50)
    private String difficulty;

    private boolean active = true;

    // Getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }
    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }
    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }
    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }
    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }
    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }
    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}