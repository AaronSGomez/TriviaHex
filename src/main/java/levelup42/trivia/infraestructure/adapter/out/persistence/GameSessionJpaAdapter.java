package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class GameSessionJpaAdapter implements GameSessionRepositoryPort {

    private final Map<UUID, GameSession> storage = new HashMap<>();

    @Override
    public void save(GameSession session) {

    }

    @Override
    public Optional<GameSession> findById(UUID id) {
        return Optional.empty();
    }
}
