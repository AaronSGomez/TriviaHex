package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.out.PlayerRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.mapper.PlayerMapper;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.DataPlayerRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PlayerJpaAdapter implements PlayerRepositoryPort {

    private final DataPlayerRepository jpaRepository;
    private final PlayerMapper mapper;

    public PlayerJpaAdapter(DataPlayerRepository jpaRepository, PlayerMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Player savePlayer(Player player) {
        PlayerEntity entityToSave = mapper.toEntity(player);
        PlayerEntity savedEntity = jpaRepository.save(entityToSave);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Player> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public void deletePlayer(UUID id) {
        jpaRepository.deleteById(id);
    }

    public List<Player> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Player> findByMail(String mail) {
        return jpaRepository.findByMail(mail).map(mapper::toDomain);
    }
}
