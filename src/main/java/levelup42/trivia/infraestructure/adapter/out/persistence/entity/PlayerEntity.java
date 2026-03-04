package levelup42.trivia.infraestructure.adapter.out.persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import levelup42.trivia.domain.model.Player.Role;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "player")
public class PlayerEntity {

    @Id
    private UUID id;
    private String name;
    private String mail;
    
    @Column(nullable = false)
    private String password;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    
    private Instant createdAt;

    // JPA requiere constructor por defecto
    public PlayerEntity() {
    }

    public PlayerEntity(UUID id, String name, String mail, String password, Role role, Instant createdAt) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.role = role;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
