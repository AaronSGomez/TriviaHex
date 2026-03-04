package levelup42.trivia.infraestructure.adapter.in.rest;

import levelup42.trivia.domain.model.Player;
import levelup42.trivia.domain.port.in.player.GetPlayerUseCase;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

/**
 * Tests for PlayerController.
 * Note: Player registration (POST) is now exclusively handled by AuthController (/api/auth/register).
 * PlayerController only exposes read-only (GET) endpoints.
 */
@ExtendWith(MockitoExtension.class)
public class PlayerControllerTest {

    private MockMvc mockMvc;

    @Mock
    private GetPlayerUseCase getPlayerUseCase;

    @InjectMocks
    private PlayerController playerController;

    private Player mockPlayer;
    private UUID playerUuid;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(playerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        String playerid = "12345678-aaaa-bbbb-cccc-000000000000";
        playerUuid = UUID.fromString(playerid);

        mockPlayer = new Player(playerUuid, "AaronSGomez", "nomeacuerdobien@gmail.com", "password", Player.Role.USER);
    }

    @Test
    void getAllPlayers_ReturnsListOfPlayers() throws Exception {
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

}
