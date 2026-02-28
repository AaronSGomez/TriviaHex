package levelup42.trivia.domain.port.in.player;

import java.util.UUID;

public interface DeletePlayerUseCase {
    void deletePlayer(UUID id);
}
