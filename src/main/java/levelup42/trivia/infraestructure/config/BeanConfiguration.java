package levelup42.trivia.infraestructure.config;

import levelup42.trivia.application.service.gamesession.CreateGameSessionService;
import levelup42.trivia.domain.port.in.CreateGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.GameSessionJpaAdapter;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.DataGameSessionRepository;
import levelup42.trivia.infraestructure.adapter.out.persistence.mapper.GameSessionMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public GameSessionRepositoryPort sessionRepositoryPort(DataGameSessionRepository dataRepo, GameSessionMapper mapper) {
        return new GameSessionJpaAdapter(dataRepo, mapper);
    }

    @Bean
    public CreateGameSessionUseCase createSessionUseCase(GameSessionRepositoryPort repository) {
        return new CreateGameSessionService(repository);
    }

}
