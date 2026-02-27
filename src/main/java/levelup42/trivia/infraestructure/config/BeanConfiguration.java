package levelup42.trivia.infraestructure.config;

import levelup42.trivia.application.service.CreateGameSessionService;
import levelup42.trivia.domain.port.in.CreateGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.GameSessionJpaAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public GameSessionRepositoryPort sessionRepositoryPort() {
        return new GameSessionJpaAdapter();
    }

    @Bean
    public CreateGameSessionUseCase createSessionUseCase(GameSessionRepositoryPort repository) {
        return new CreateGameSessionService(repository);
    }

}
