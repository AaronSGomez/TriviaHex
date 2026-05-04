package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.model.SessionStatus;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.mapper.GameSessionMapper;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionQuestionEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.DataGameSessionRepository;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.DataGameSessionQuestionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GameSessionJpaAdapter implements GameSessionRepositoryPort {

    private final DataGameSessionRepository repository;
    private final DataGameSessionQuestionRepository questionSessionRepository;
    private final GameSessionMapper mapper;

    public GameSessionJpaAdapter(DataGameSessionRepository repository, DataGameSessionQuestionRepository questionSessionRepository, GameSessionMapper mapper) {
        this.repository = repository;
        this.questionSessionRepository = questionSessionRepository;
        this.mapper = mapper;
    }

    @Override
    public GameSession save(GameSession session) {
        GameSessionEntity entity = mapper.toEntity(session);
        GameSessionEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<GameSession> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<GameSession> findByPlayerId(UUID playerId) {
        return repository.findByPlayerId(playerId).stream()
            .map(this::toDomainFromLegacyRow)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameSession> findAllFinishedOrderedByScoreDesc() {
        return repository.findAllByStatusOrderByScoreDesc(SessionStatus.FINISHED.ordinal()).stream()
            .map(this::toDomainFromLegacyRow)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameSession> findWeeklyLeaderboard(java.time.Instant weekStart, java.time.Instant weekEnd) {
        return repository.findWeeklyLeaderboard(SessionStatus.FINISHED.ordinal(), weekStart, weekEnd).stream()
            .map(this::toDomainFromLegacyRow)
                .collect(Collectors.toList());
    }

        private GameSession toDomainFromLegacyRow(DataGameSessionRepository.LegacyGameSessionRow row) {
        GameSession session = new GameSession(
            row.getId(),
            row.getPlayerId(),
            row.getSubject(),
            row.getTotalQuestions() != null ? row.getTotalQuestions() : 0,
            row.getAnsweredQuestions() != null ? row.getAnsweredQuestions() : 0,
            row.getCorrectAnswers() != null ? row.getCorrectAnswers() : 0,
            row.getSkippedAnswers() != null ? row.getSkippedAnswers() : 0,
            row.getScore() != null ? row.getScore() : 0,
            row.getStartedAt(),
            row.getFinishedAt(),
                row.getStatus() != null && row.getStatus() == SessionStatus.FINISHED.ordinal()
                    ? SessionStatus.FINISHED
                    : SessionStatus.IN_PROGRESS
        );
        return session;
        }

    @Override
    public List<Long> findAskedQuestionIdsBySessionId(UUID sessionId) {
        return questionSessionRepository.findAskedQuestionIdsBySessionId(sessionId);
    }

    @Override
    public void registerAskedQuestion(UUID sessionId, Long questionId) {
        GameSessionQuestionEntity entity = new GameSessionQuestionEntity(sessionId, questionId);
        questionSessionRepository.save(entity);
    }

    @Override
    public List<Long> findFailedQuestionIdsByPlayerAndSubject(UUID playerId, String subject) {
        return questionSessionRepository.findFailedQuestionIdsByPlayerAndSubject(playerId, subject);
    }

    @Override
    public List<Long> findCorrectQuestionIdsByPlayerAndSubjectSince(UUID playerId, String subject, java.time.Instant since) {
        return questionSessionRepository.findCorrectQuestionIdsByPlayerAndSubjectSince(playerId, subject, since);
    }

    @Override
    public List<Long> findAskedQuestionIdsByPlayerAndSubjectSince(UUID playerId, String subject, java.time.Instant since) {
        return questionSessionRepository.findAskedQuestionIdsByPlayerAndSubjectSince(playerId, subject, since);
    }

    @Override
    public void registerAnswerResult(UUID sessionId, Long questionId, boolean correct, java.time.Instant answeredAt) {
        java.util.Optional<GameSessionQuestionEntity> opt = questionSessionRepository.findBySessionIdAndQuestionId(sessionId, questionId);
        if (opt.isPresent()) {
            GameSessionQuestionEntity entity = opt.get();
            entity.setCorrect(correct);
            entity.setAnsweredAt(answeredAt);
            questionSessionRepository.save(entity);
        } else {
            GameSessionQuestionEntity entity = new GameSessionQuestionEntity(sessionId, questionId);
            entity.setCorrect(correct);
            entity.setAnsweredAt(answeredAt);
            questionSessionRepository.save(entity);
        }
    }

    @Override
    public long countAskedByPlayerAndSubjectSince(UUID playerId, String subject, java.time.Instant since) {
        return questionSessionRepository.countAskedByPlayerAndSubjectSince(playerId, subject, since);
    }
}
