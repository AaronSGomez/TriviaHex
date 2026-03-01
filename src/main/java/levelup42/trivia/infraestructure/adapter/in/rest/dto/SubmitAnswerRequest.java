package levelup42.trivia.infraestructure.adapter.in.rest.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SubmitAnswerRequest {

    @NotNull(message = "Question ID cannot be null")
    private Long questionId;

    @NotBlank(message = "Selected option cannot be empty")
    private String selectedOption;

    @NotNull(message = "Time elapsed must be provided")
    @Min(value = 0, message = "Time elapsed cannot be negative")
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
