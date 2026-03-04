package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.question.CreateQuestionUseCase;
import levelup42.trivia.domain.port.in.question.GetQuestionUseCase;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.QuestionRequest;
import levelup42.trivia.infraestructure.adapter.in.rest.dto.QuestionResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/v1/questions")
public class QuestionController {

    private final CreateQuestionUseCase createQuestionUseCase;
    private final GetQuestionUseCase getQuestionUseCase;

    public QuestionController(CreateQuestionUseCase createQuestionUseCase,
                              GetQuestionUseCase getQuestionUseCase) {
        this.createQuestionUseCase = createQuestionUseCase;
        this.getQuestionUseCase = getQuestionUseCase;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<QuestionResponse> createQuestion(@Valid @RequestBody QuestionRequest request) {
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
        return ResponseEntity.ok(QuestionResponse.fromDomain(created));
    }

    @GetMapping
    public ResponseEntity<List<QuestionResponse>> getAllQuestions() {
        return ResponseEntity.ok(getQuestionUseCase.getAllQuestions().stream().map(QuestionResponse::fromDomain).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestionById(@PathVariable Long id) {
        return getQuestionUseCase.getQuestionById(id)
                .map(question -> ResponseEntity.ok(QuestionResponse.fromDomain(question)))
                .orElse(ResponseEntity.notFound().build());
    }
}
