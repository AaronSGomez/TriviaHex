package levelup42.trivia.domain.port.in.auth;

import levelup42.trivia.domain.model.Player;

public interface AuthUseCase {
    Player register(String mail, String name, String password, Player.Role role);
    String login(String mail, String password);
    AuthenticatedPlayer loginWithGoogle(String idToken);
}
