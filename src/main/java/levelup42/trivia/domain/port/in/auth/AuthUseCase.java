package levelup42.trivia.domain.port.in.auth;

import levelup42.trivia.domain.model.Player;

public interface AuthUseCase {
    Player register(String email, String password, Player.Role role);
    String login(String email, String password);
}
