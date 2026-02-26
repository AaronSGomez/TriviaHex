package levelup42.trivia.infraestructure.config;

import levelup42.trivia.application.service.CreateSessionService;
import levelup42.trivia.domain.port.in.CreateSessionUseCase;
import levelup42.trivia.domain.port.out.SessionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.InMemorySessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public SessionRepositoryPort sessionRepositoryPort() {
        return new InMemorySessionRepository();
    }

    @Bean
    public CreateSessionUseCase createSessionUseCase(SessionRepositoryPort repository) {
        return new CreateSessionService(repository);
    }

}
