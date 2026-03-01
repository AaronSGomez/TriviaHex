package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.CreatePlayerUseCase;
import levelup42.trivia.domain.port.in.player.DeletePlayerUseCase;
import levelup42.trivia.domain.port.in.player.GetPlayerUseCase;
import levelup42.trivia.domain.port.in.player.UpdatePlayerUseCase;
import levelup42.trivia.infraestructure.config.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class PlayerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CreatePlayerUseCase createPlayerUseCase;

    @Mock
    private UpdatePlayerUseCase updatePlayerUseCase;

    @Mock
    private DeletePlayerUseCase deletePlayerUseCase;

    @Mock
    private GetPlayerUseCase getPlayerUseCase;

    @InjectMocks
    private PlayerController playerController;

    private Player mockPlayer;

    private UUID playerUuid;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(playerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String playerid = "12345678-aaaa-bbbb-cccc-000000000000"; // cadena de UUID
        playerUuid = UUID.fromString(playerid); // conversion  UUID

        mockPlayer= new Player(playerUuid, "AaronSGomez", "nomeacuerdobien@gmail.com");
    }

    @Test
    void createPlayer__WithValidData_ReturnsCreatedPlayer() throws Exception {
        when(createPlayerUseCase.createPlayer(any(Player.class))).thenReturn(mockPlayer);

        String requestJson =  """
                {
                    "name": "AaronSGomez",
                    "mail": "nomeacuerdobien@gmail.com"
                }
                """;

        mockMvc.perform(post("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerUuid.toString()))
                .andExpect(jsonPath("$.name").value("AaronSGomez"));
    }

    @Test
    void getAllPlayers_ReturnsListOfplayers() throws Exception {
        when(getPlayerUseCase.getAllPlayers()).thenReturn(List.of(mockPlayer));

        mockMvc.perform(get("/api/v1/players")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(playerUuid.toString()))
                .andExpect(jsonPath("$[0].name").value("AaronSGomez"));
    }

    @Test
    void getPlayerById_WhenFound_ReturnsPlayer() throws Exception {
        when(getPlayerUseCase.getPlayerById(playerUuid)).thenReturn(mockPlayer);

        mockMvc.perform(get("/api/v1/players/" + playerUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerUuid.toString()))
                .andExpect(jsonPath("$.name").value("AaronSGomez"));
    }

    @Test
    void getPlayerById_WhenNotFound_ReturnsNotFound() throws Exception {
        when(getPlayerUseCase.getPlayerById(playerUuid)).thenReturn(null);

        mockMvc.perform(get("/api/v1/players/" + playerUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void updatePlayer_WithValidData_ReturnsUpdatedPlayer() throws Exception {
        Player updatedPlayer = new Player(playerUuid, "AaronSGomez", "nomeacuerdobien@gmail.com");
        when(updatePlayerUseCase.updatePlayer(any(UUID.class), any(Player.class))).thenReturn(updatedPlayer);

        String requestJson =  """
                {
                    "name": "AaronSGomez",
                    "mail": "nomeacuerdobien@gmail.com"
                }
                """;

        mockMvc.perform(post("/api/v1/players/" + playerUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(playerUuid.toString()))
                .andExpect(jsonPath("$.name").value("AaronSGomez"));
    }

    @Test
    void deletePlayer_ReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/api/v1/players/" + playerUuid.toString())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }



}
