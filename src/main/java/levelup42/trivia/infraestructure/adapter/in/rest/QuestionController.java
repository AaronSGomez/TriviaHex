package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.CreateQuestionUseCase;
import levelup42.trivia.domain.port.in.DeleteQuestionUseCase;
import levelup42.trivia.domain.port.in.UpdateQuestionUseCase;
import levelup42.trivia.domain.port.in.GetQuestionUseCase;
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
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question created = createQuestionUseCase.save(question);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        Question updated = updateQuestionUseCase.updateQuestion(id, question);
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
