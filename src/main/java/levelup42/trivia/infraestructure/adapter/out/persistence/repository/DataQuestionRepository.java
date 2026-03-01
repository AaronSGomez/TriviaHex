package levelup42.trivia.infraestructure.adapter.out.persistence.repository;

import levelup42.trivia.infraestructure.adapter.out.persistence.entity.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DataQuestionRepository extends JpaRepository<QuestionEntity, Long> {
    List<QuestionEntity> findBySubjectAndActiveTrue(String subject);

    @Query(value = "SELECT * FROM question q WHERE q.subject = :subject AND q.active = true AND q.id NOT IN :askedIds ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<QuestionEntity> findRandomUnansweredBySubject(@Param("subject") String subject, @Param("askedIds") List<Long> askedIds);

    @Query(value = "SELECT * FROM question q WHERE q.subject = :subject AND q.active = true ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<QuestionEntity> findRandomBySubject(@Param("subject") String subject);
}