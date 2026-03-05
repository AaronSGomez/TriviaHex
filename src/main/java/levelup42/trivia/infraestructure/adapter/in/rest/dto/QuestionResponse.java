package levelup42.trivia.infraestructure.adapter.in.rest.dto;

import levelup42.trivia.domain.model.Question;

public class QuestionResponse {
    private final Long id;
    private final String statement;
    private final String optionA;
    private final String optionB;
    private final String optionC;
    private final String optionD;
    private final String correctOption;
    private final String explanation;
    private final String subject;
    private final String topic;
    private final String difficulty;
    private final boolean active;

    public QuestionResponse(Long id, String statement, String optionA, String optionB, String optionC, String optionD,
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

    public static QuestionResponse fromDomain(Question question) {
        return new QuestionResponse(
                question.getId(),
                question.getStatement(),
                question.getOptionA(),
                question.getOptionB(),
                question.getOptionC(),
                question.getOptionD(),
                question.getCorrectOption(),
                question.getExplanation(),
                question.getSubject(),
                question.getTopic(),
                question.getDifficulty(),
                question.isActive()
        );
    }

    public Long getId() { return id; }
    public String getStatement() { return statement; }
    public String getOptionA() { return optionA; }
    public String getOptionB() { return optionB; }
    public String getOptionC() { return optionC; }
    public String getOptionD() { return optionD; }
    public String getCorrectOption() { return correctOption; }
    public String getExplanation() { return explanation; }
    public String getSubject() { return subject; }
    public String getTopic() { return topic; }
    public String getDifficulty() { return difficulty; }
    public boolean isActive() { return active; }
}
