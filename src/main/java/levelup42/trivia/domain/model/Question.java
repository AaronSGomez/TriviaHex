package levelup42.trivia.domain.model;


public class Question {

    private Long id;

    private String statement;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    private String correctOption;

    private String explanation;

    private String subject;
    private String topic;
    private String difficulty;

    private boolean active = true;

    public Question() {
    }

    public Question(Long id, String statement, String optionA, String optionB, String optionC, String optionD,
            String correctOption, String explanation, String subject, String topic, String difficulty, boolean active) {
        this.id = id;
        this.statement = statement;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.explanation = explanation;
        this.subject = subject;
        this.topic = topic;
        this.difficulty = difficulty;
        this.active = active;
    }

    // getter
    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getSubject() {
        return subject;
    }

    public String getTopic() {
        return topic;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public boolean isActive() {
        return active;
    }
}