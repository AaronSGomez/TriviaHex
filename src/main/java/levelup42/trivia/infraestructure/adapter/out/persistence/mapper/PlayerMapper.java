package levelup42.trivia.infraestructure.adapter.out.persistence.mapper;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.PlayerEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlayerMapper {

    public PlayerEntity toEntity(Player domain) {
        if (domain == null) return null;
        UUID entityId = domain.getId() != null ? domain.getId() : UUID.randomUUID();
        return new PlayerEntity(
                entityId,
                domain.getName(),
                domain.getMail(),
                domain.getPassword(),
                domain.getRole(),
                domain.getCreatedAt()
        );
    }

    public Player toDomain(PlayerEntity entity) {
        if (entity == null) return null;
        
        // El constructor de Player asume que se le pasa id, name, y mail,
        // pero createdAt lo establece a now().
        // Para que coincida con DB, crearemos la instancia y si es necesario 
        // tener constructor con todos los argumentos en domain.
        // Dado el actual modelo de Player:
        // public Player(UUID id, String name, String mail)
        // Setear createdAt luego requeriría un setter, o pasarlo en el constructor.
        // Se respetará el constructor actual para la carga de datos.
        
        return new Player(
                entity.getId(),
                entity.getName(),
                entity.getMail(),
                entity.getPassword(),
                entity.getRole()
        );
    }
}
