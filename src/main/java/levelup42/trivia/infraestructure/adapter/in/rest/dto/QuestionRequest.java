package levelup42.trivia.infraestructure.adapter.in.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class QuestionRequest {

    @NotBlank(message = "The statement cannot be empty")
    private String statement;

    @NotBlank(message = "Option A cannot be empty")
    private String optionA;

    @NotBlank(message = "Option B cannot be empty")
    private String optionB;

    @NotBlank(message = "Option C cannot be empty")
    private String optionC;

    @NotBlank(message = "Option D cannot be empty")
    private String optionD;

    @NotBlank(message = "The correct option must be specified")
    private String correctOption;

    @NotBlank(message = "The explanation cannot be empty")
    private String explanation;

    @NotBlank(message = "The subject cannot be empty")
    private String subject;

    @NotBlank(message = "The topic cannot be empty")
    private String topic;

    @NotBlank(message = "The difficulty cannot be empty")
    private String difficulty;

    @NotNull(message = "Active status must be specified")
    private boolean active;

    public QuestionRequest() {
    }

    public QuestionRequest(String statement, String optionA, String optionB, String optionC, String optionD,
                           String correctOption, String explanation, String subject, String topic, String difficulty, boolean active) {
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
