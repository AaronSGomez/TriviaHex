package levelup42.trivia.domain.port.in;

import java.util.UUID;

public interface DeletePlayerUseCase {
    void deletePlayer(UUID id);
}
