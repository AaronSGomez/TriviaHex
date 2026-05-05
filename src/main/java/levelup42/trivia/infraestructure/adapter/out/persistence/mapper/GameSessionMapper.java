package levelup42.trivia.infraestructure.adapter.out.persistence.mapper;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import org.springframework.stereotype.Component;

@Component
public class GameSessionMapper {

    public GameSessionEntity toEntity(GameSession domain) {
        if (domain == null) return null;
        
        GameSessionEntity entity = new GameSessionEntity(
            domain.getId(),
            domain.getPlayerId(),
            domain.getSubject(),
            domain.getTotalQuestions(),
            domain.getAnsweredQuestions(),
            domain.getCorrectAnswers(),
            domain.getSkippedAnswers(),
            domain.getScore(),
            domain.getStartedAt(),
            domain.getFinishedAt(),
            domain.getStatus()
        );
        entity.setTestCycleIndex(domain.getTestCycleIndex());
        entity.setSessionType(domain.getSessionType());
        entity.setReviewQuestionCount(domain.getReviewQuestionCount());
        return entity;
    }

    public GameSession toDomain(GameSessionEntity entity) {
        if (entity == null) return null;
        
        GameSession domain = new GameSession(
            entity.getId(),
            entity.getPlayerId(),
            entity.getSubject(),
            entity.getTotalQuestions(),
            entity.getAnsweredQuestions(),
            entity.getCorrectAnswers(),
            entity.getSkippedAnswers(),
            entity.getScore(),
            entity.getStartedAt(),
            entity.getFinishedAt(),
            entity.getStatus()
        );
        domain.setTestCycleIndex(entity.getTestCycleIndex());
        domain.setSessionType(entity.getSessionType());
        domain.setReviewQuestionCount(entity.getReviewQuestionCount());
        return domain;
    }
}
