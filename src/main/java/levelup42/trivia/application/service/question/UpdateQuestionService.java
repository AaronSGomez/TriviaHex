package levelup42.trivia.application.service.question;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.UpdateQuestionUseCase;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class UpdateQuestionService implements UpdateQuestionUseCase {

    private final QuestionRepositoryPort questionRepositoryPort;

    public UpdateQuestionService(QuestionRepositoryPort questionRepositoryPort) {
        this.questionRepositoryPort = questionRepositoryPort;
    }

    @Override
    public Question updateQuestion(Long id, Question questionDetails) {
        Question existingQuestion = questionRepositoryPort.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found with id: " + id));

        // Create a new Question instance with updated fields, retaining the original ID
        Question updatedQuestion = new Question(
                existingQuestion.getId(), // Keep original ID
                questionDetails.getStatement() != null ? questionDetails.getStatement() : existingQuestion.getStatement(),
                questionDetails.getOptionA() != null ? questionDetails.getOptionA() : existingQuestion.getOptionA(),
                questionDetails.getOptionB() != null ? questionDetails.getOptionB() : existingQuestion.getOptionB(),
                questionDetails.getOptionC() != null ? questionDetails.getOptionC() : existingQuestion.getOptionC(),
                questionDetails.getOptionD() != null ? questionDetails.getOptionD() : existingQuestion.getOptionD(),
                questionDetails.getCorrectOption() != null ? questionDetails.getCorrectOption() : existingQuestion.getCorrectOption(),
                questionDetails.getExplanation() != null ? questionDetails.getExplanation() : existingQuestion.getExplanation(),
                questionDetails.getSubject() != null ? questionDetails.getSubject() : existingQuestion.getSubject(),
                questionDetails.getTopic() != null ? questionDetails.getTopic() : existingQuestion.getTopic(),
                questionDetails.getDifficulty() != null ? questionDetails.getDifficulty() : existingQuestion.getDifficulty(),
                questionDetails.isActive() // If this is false, it might deactivate it
        );

        return questionRepositoryPort.saveQuestion(updatedQuestion);
    }
}
