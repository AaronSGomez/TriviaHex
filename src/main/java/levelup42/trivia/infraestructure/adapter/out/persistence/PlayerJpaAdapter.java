package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.mapper.PlayerMapper;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.SpringDataPlayerRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class PlayerJpaAdapter implements PlayerRepositoryPort {

    private final SpringDataPlayerRepository jpaRepository;
    private final PlayerMapper mapper;

    public PlayerJpaAdapter(SpringDataPlayerRepository jpaRepository, PlayerMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Player save(Player player) {
        PlayerEntity entityToSave = mapper.toEntity(player);
        PlayerEntity savedEntity = jpaRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Player> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }
}
