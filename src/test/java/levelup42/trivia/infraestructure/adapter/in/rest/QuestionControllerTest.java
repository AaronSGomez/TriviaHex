package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.question.CreateQuestionUseCase;
import levelup42.trivia.domain.port.in.question.GetQuestionUseCase;
import levelup42.trivia.infraestructure.config.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class QuestionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreateQuestionUseCase createQuestionUseCase;

    @Mock
    private GetQuestionUseCase getQuestionUseCase;

    @InjectMocks
    private QuestionController questionController;

    private Question mockQuestion;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(questionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
                
        mockQuestion = new Question(
                1L, "What is 2+2?", "3", "4", "5", "6", "B", "Basic math", "Math", "Addition", "Easy", true
        );
    }

    @Test
    void createQuestion_WithValidData_ReturnsCreatedQuestion() throws Exception {
        when(createQuestionUseCase.save(any(Question.class))).thenReturn(mockQuestion);

        String requestJson = """
                {
                    "statement": "What is 2+2?",
                    "optionA": "3",
                    "optionB": "4",
                    "optionC": "5",
                    "optionD": "6",
                    "correctOption": "B",
                    "explanation": "Basic math",
                    "subject": "Math",
                    "topic": "Addition",
                    "difficulty": "Easy",
                    "active": true
                }
                """;

        mockMvc.perform(post("/api/v1/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statement").value("What is 2+2?"));
    }

    @Test
    void getAllQuestions_ReturnsListOfQuestions() throws Exception {
        when(getQuestionUseCase.getAllQuestions()).thenReturn(List.of(mockQuestion));

        mockMvc.perform(get("/api/v1/questions")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].statement").value("What is 2+2?"));
    }

    @Test
    void getQuestionById_WhenFound_ReturnsQuestion() throws Exception {
        when(getQuestionUseCase.getQuestionById(1L)).thenReturn(Optional.of(mockQuestion));

        mockMvc.perform(get("/api/v1/questions/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.statement").value("What is 2+2?"));
    }

    @Test
    void getQuestionById_WhenNotFound_ReturnsNotFound() throws Exception {
        when(getQuestionUseCase.getQuestionById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/questions/99")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
