package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.gamesession.GetNextQuestionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class GetNextQuestionService implements GetNextQuestionUseCase {

    private final GameSessionRepositoryPort sessionRepository;
    private final QuestionRepositoryPort questionRepository;

    public GetNextQuestionService(GameSessionRepositoryPort sessionRepository, QuestionRepositoryPort questionRepository) {
        this.sessionRepository = sessionRepository;
        this.questionRepository = questionRepository;
    }

    @Override
    public Optional<Question> getNextQuestion(UUID sessionId) {
        // 1. Obtener la sesión
        GameSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (session.isFinished()) {
            throw new IllegalStateException("Game Session is already finished");
        }

        if (session.getAnsweredQuestions() >= session.getTotalQuestions()) {
             // Por seguridad, si ya llegó al límite pero por algún motivo no se marcó como fin, cerrarla.
             session.finish();
             sessionRepository.save(session);
             return Optional.empty();
        }

        // 2. Obtener las IDs de preguntas ya respondidas en esta sesión
        List<Long> askedQuestionIds = sessionRepository.findAskedQuestionIdsBySessionId(sessionId);

        // 3. Buscar una pregunta aleatoria de la categoría que NO esté en la lista de respondidas
        Optional<Question> nextQuestion;
        if (askedQuestionIds.isEmpty()) {
            // Si es la primera pregunta de la sesión, no pasamos la lista vacía al IN de SQL (puede dar error de sintaxis en algunos DB engines)
             nextQuestion = questionRepository.findRandomBySubject(session.getSubjet());
        } else {
             nextQuestion = questionRepository.findRandomUnansweredBySubject(session.getSubjet(), askedQuestionIds);
        }

        // 4. Si encontramos una pregunta, la registramos como preguntada en esta sesión para que no se repita
        nextQuestion.ifPresent(question -> {
            sessionRepository.registerAskedQuestion(sessionId, question.getId());
        });

        return nextQuestion;
    }
}
