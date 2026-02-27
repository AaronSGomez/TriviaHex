package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.mapper.GameSessionMapper;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.DataGameSessionRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

public class GameSessionJpaAdapter implements GameSessionRepositoryPort {

    private final DataGameSessionRepository repository;
    private final GameSessionMapper mapper;

    public GameSessionJpaAdapter(DataGameSessionRepository repository, GameSessionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public void save(GameSession session) {
        GameSessionEntity entity = mapper.toEntity(session);
        repository.save(entity);
    }

    @Override
    public Optional<GameSession> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
