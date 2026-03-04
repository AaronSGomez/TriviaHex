package levelup42.trivia.domain.model;

import java.time.Instant;
import java.util.UUID;

public class Player {

    public enum Role {
        USER,
        ADMIN
    }

    private final UUID id;
    private final String name;
    private final String mail;
    private final String password;
    private final Role role;
    private final Instant createdAt;

    public Player(UUID id, String name, String mail, String password, Role role) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.role = role;
        this.createdAt = Instant.now();
    }

    // getters
    public UUID getId() {return id;}
    public String getName() {return name;}
    public String getMail() {return mail;}
    public String getPassword() {return password;}
    public Role getRole() {return role;}
    public Instant getCreatedAt() {return createdAt;}

}
