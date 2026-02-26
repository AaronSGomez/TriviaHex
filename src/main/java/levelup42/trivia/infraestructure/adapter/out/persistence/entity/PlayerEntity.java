package levelup42.trivia.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "players")
public class PlayerEntity {

    @Id
    private UUID id;
    private String name;
    private String mail;
    private Instant createdAt;

    // JPA requiere constructor por defecto
    public PlayerEntity() {
    }

    public PlayerEntity(UUID id, String name, String mail, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
