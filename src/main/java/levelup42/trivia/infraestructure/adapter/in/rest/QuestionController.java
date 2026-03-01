package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.question.CreateQuestionUseCase;
import levelup42.trivia.domain.port.in.question.DeleteQuestionUseCase;
import levelup42.trivia.domain.port.in.question.UpdateQuestionUseCase;
import levelup42.trivia.domain.port.in.question.GetQuestionUseCase;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.QuestionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final UpdateQuestionUseCase updateQuestionUseCase;
    private final DeleteQuestionUseCase deleteQuestionUseCase;
    private final GetQuestionUseCase getQuestionUseCase;

    public QuestionController(CreateQuestionUseCase createQuestionUseCase,
                              UpdateQuestionUseCase updateQuestionUseCase,
                              DeleteQuestionUseCase deleteQuestionUseCase,
                              GetQuestionUseCase getQuestionUseCase) {
        this.createQuestionUseCase = createQuestionUseCase;
        this.updateQuestionUseCase = updateQuestionUseCase;
        this.deleteQuestionUseCase = deleteQuestionUseCase;
        this.getQuestionUseCase = getQuestionUseCase;
    }

    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody QuestionRequest request) {
        Question questionToCreate = new Question(
                null,
                request.getStatement(),
                request.getOptionA(),
                request.getOptionB(),
                request.getOptionC(),
                request.getOptionD(),
                request.getCorrectOption(),
                request.getExplanation(),
                request.getSubject(),
                request.getTopic(),
                request.getDifficulty(),
                request.isActive()
        );
        Question created = createQuestionUseCase.save(questionToCreate);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody QuestionRequest request) {
        Question questionToUpdate = new Question(
                id,
                request.getStatement(),
                request.getOptionA(),
                request.getOptionB(),
                request.getOptionC(),
                request.getOptionD(),
                request.getCorrectOption(),
                request.getExplanation(),
                request.getSubject(),
                request.getTopic(),
                request.getDifficulty(),
                request.isActive()
        );
        Question updated = updateQuestionUseCase.updateQuestion(id, questionToUpdate);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        deleteQuestionUseCase.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        return ResponseEntity.ok(getQuestionUseCase.getAllQuestions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return getQuestionUseCase.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
