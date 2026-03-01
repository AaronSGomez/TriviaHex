package levelup42.trivia.infraestructure.adapter.out.persistence.repository;

import levelup42.trivia.infraestructure.adapter.out.persistence.entity.GameSessionQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DataGameSessionQuestionRepository extends JpaRepository<GameSessionQuestionEntity, Long> {

    @Query("SELECT gsq.questionId FROM GameSessionQuestionEntity gsq WHERE gsq.sessionId = :sessionId")
    List<Long> findAskedQuestionIdsBySessionId(@Param("sessionId") UUID sessionId);
}
