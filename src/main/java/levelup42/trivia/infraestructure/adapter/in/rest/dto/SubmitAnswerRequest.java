package levelup42.trivia.infraestructure.adapter.in.rest.dto;

public class SubmitAnswerRequest {

    private Long questionId;
    private String selectedOption;
    private Integer timeElapsedSeconds;


    public SubmitAnswerRequest(Long questionId, String selectedOption, Integer timeElapsedSeconds) {
        this.questionId = questionId;
        this.selectedOption = selectedOption;
        this.timeElapsedSeconds = timeElapsedSeconds;
    }


    public Long getQuestionId() {
        return questionId;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public Integer getTimeElapsedSeconds() {
        return timeElapsedSeconds;
    }
}
