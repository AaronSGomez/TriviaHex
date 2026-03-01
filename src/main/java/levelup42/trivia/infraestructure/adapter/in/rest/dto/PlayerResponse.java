package levelup42.trivia.infraestructure.adapter.in.rest.dto;

import levelup42.trivia.domain.model.Player;

import java.time.Instant;
import java.util.UUID;

public class PlayerResponse {

    private final UUID id;
    private final String name;
    private final String mail;
    private final Instant createdAt;

    public PlayerResponse(UUID id, String name, String mail, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.createdAt = createdAt;
    }

    public static PlayerResponse fromDomain(Player player) {
        return new PlayerResponse(
                player.getId(),
                player.getName(),
                player.getMail(),
                player.getCreatedAt()
        );
    }

    // getters
    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getMail() {return mail;}
    public Instant getCreatedAt() {return createdAt;}

}
