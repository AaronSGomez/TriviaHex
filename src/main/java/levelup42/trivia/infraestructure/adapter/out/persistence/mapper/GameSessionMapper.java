package levelup42.trivia.infraestructure.adapter.out.persistence.mapper;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import org.springframework.stereotype.Component;

@Component
public class GameSessionMapper {

    public GameSessionEntity toEntity(GameSession domain) {
        if (domain == null) return null;
        
        return new GameSessionEntity(
                domain.getId(),
                domain.getPlayerId(),
                domain.getSubjet(),
                domain.getTotalQuestions(),
                domain.getCorrectAnswers(),
                domain.getScore(),
                domain.getStartedAt(),
                domain.getFinishedAt(),
                domain.getStatus()
        );
    }

    public GameSession toDomain(GameSessionEntity entity) {
        if (entity == null) return null;
        
        return new GameSession(
                entity.getId(),
                entity.getPlayerId(),
                entity.getSubjet(),
                entity.getTotalQuestions(),
                entity.getCorrectAnswers(),
                entity.getScore(),
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getStatus()
        );
    }
}
