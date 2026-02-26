package levelup42.trivia.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Player {

    private final UUID id;
    private final String name;
    private final String mail;
    private final Instant createdAt;

    public Player(UUID id, String name, String mail) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.createdAt = Instant.now();
    }

    // getters
    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getMail() {return mail;}
    public Instant getCreatedAt() {return createdAt;}

}
