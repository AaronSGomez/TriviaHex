package levelup42.trivia.domain.port.in.auth;

import levelup42.trivia.domain.model.Player;

public record AuthenticatedPlayer(String token, Player player) {
}
