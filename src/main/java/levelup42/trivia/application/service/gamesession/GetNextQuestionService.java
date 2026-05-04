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

        // 3. Buscar una pregunta según tipo de sesión
        Optional<Question> nextQuestion = Optional.empty();

        // Prepare exclusion list starting with already asked questions
        java.util.List<Long> excludedIds = new java.util.ArrayList<>(askedQuestionIds);

        if (session.getSessionType() == levelup42.trivia.domain.model.SessionType.REVIEW) {
            // Preferir preguntas falladas por el usuario en esta asignatura (más recientes primero)
            java.util.List<Long> failedIds = sessionRepository.findFailedQuestionIdsByPlayerAndSubject(session.getPlayerId(), session.getSubject());
            // pick first failed not yet asked
            for (Long qid : failedIds) {
                if (!excludedIds.contains(qid)) {
                    nextQuestion = questionRepository.findById(qid);
                    if (nextQuestion.isPresent()) break;
                }
            }
        } else {
            // NORMAL session: use dynamic 96h window strategy based on pool size
            // Get total questions available in this subject
            long totalQuestionsInSubject = questionRepository.countBySubject(session.getSubject());
            
            // Calculate how many questions have been asked in the last 96 hours
            java.time.Instant since = java.time.Instant.now().minus(java.time.Duration.ofHours(96));
            long recentAskedCount = sessionRepository.countAskedByPlayerAndSubjectSince(
                session.getPlayerId(), 
                session.getSubject(), 
                since
            );
            
            // Dynamic threshold: if 80% or more of pool is excluded, disable 96h window
            // to allow continuing play without running out of questions
            double exclusionRatio = totalQuestionsInSubject > 0 ? (double) recentAskedCount / totalQuestionsInSubject : 0;
            
            if (exclusionRatio < 0.80) {
                // Apply 96h window - we have enough diversity
                java.util.List<Long> recentAsked = sessionRepository.findAskedQuestionIdsByPlayerAndSubjectSince(
                    session.getPlayerId(), 
                    session.getSubject(), 
                    since
                );
                if (recentAsked != null && !recentAsked.isEmpty()) {
                    for (Long id : recentAsked) if (!excludedIds.contains(id)) excludedIds.add(id);
                }
            }
            // If exclusionRatio >= 0.80: skip adding recent to excludedIds (allow repetition)
        }

        // If still no nextQuestion (either NORMAL fallback or REVIEW had none), pick random excluding excludedIds
        if (nextQuestion.isEmpty()) {
            if (excludedIds.isEmpty()) {
                nextQuestion = questionRepository.findRandomBySubject(session.getSubject());
            } else {
                nextQuestion = questionRepository.findRandomUnansweredBySubject(session.getSubject(), excludedIds);
            }
        }

        // 4. Si encontramos una pregunta, la registramos como preguntada en esta sesión para que no se repita
        nextQuestion.ifPresent(question -> {
            sessionRepository.registerAskedQuestion(sessionId, question.getId());
        });

        return nextQuestion;
    }
}
