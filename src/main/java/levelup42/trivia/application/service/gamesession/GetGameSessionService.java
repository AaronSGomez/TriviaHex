package levelup42.trivia.application.service.gamesession;

import levelup42.trivia.domain.model.GameSession;
import levelup42.trivia.domain.port.in.gamesession.GetGameSessionUseCase;
import levelup42.trivia.domain.port.out.GameSessionRepositoryPort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
public class GetGameSessionService implements GetGameSessionUseCase {

    private final GameSessionRepositoryPort sessionRepository;

    public GetGameSessionService(GameSessionRepositoryPort sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Override
    public GameSession getSessionById(UUID id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Game Session not found"));
    }

    @Override
    public List<GameSession> getPlayerHistory(UUID playerId) {
        return sessionRepository.findByPlayerId(playerId);
    }

    @Override
    public List<GameSession> getLeaderboard() {
        return sessionRepository.findAllFinishedOrderedByScoreDesc();
    }

    @Override
    public List<GameSession> getWeeklyLeaderboard() {
        // Zona horaria Madrid (Europa/Madrid = UTC+1 en invierno, UTC+2 en verano)
        ZoneId madridZone = ZoneId.of("Europe/Madrid");
        LocalDate today = LocalDate.now(madridZone);
        
        // Calcular el lunes de esta semana (DayOfWeek.MONDAY = 1)
        LocalDate monday = today.minusDays(today.getDayOfWeek().getValue() - 1);
        LocalDate sunday = monday.plusDays(6);
        
        // Convertir a Instant (inicio del lunes y fin del domingo)
        Instant weekStart = monday.atStartOfDay(madridZone).toInstant();
        Instant weekEnd = sunday.plusDays(1).atStartOfDay(madridZone).toInstant();
        
        return sessionRepository.findWeeklyLeaderboard(weekStart, weekEnd);
    }
}
