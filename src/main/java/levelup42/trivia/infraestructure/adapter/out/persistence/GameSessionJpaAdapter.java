package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.mapper.GameSessionMapper;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.DataGameSessionRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class GameSessionJpaAdapter implements GameSessionRepositoryPort {

    private final DataGameSessionRepository repository;
    private final GameSessionMapper mapper;

    public GameSessionJpaAdapter(DataGameSessionRepository repository, GameSessionMapper mapper) {
        this.repository = repository;
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
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<GameSession> findAllFinishedOrderedByScoreDesc() {
        return repository.findAllByStatusOrderByScoreDesc("FINISHED").stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
