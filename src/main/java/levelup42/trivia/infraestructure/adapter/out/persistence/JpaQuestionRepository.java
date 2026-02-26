package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.QuestionEntity;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class JpaQuestionRepository implements QuestionRepositoryPort {

    private final Map<Long, QuestionEntity> repository;

    @Override
    public Question saveQuestion(Question question) {
        return question;
    }

    @Override
    public void deleteQuestion(Long id) {
        repository.remove(id);
    }

    @Override
    public List<Question> findAll() {
        return Collections.emptyList();
    }

    @Override
    public Optional<Question> findById(Long id) {
        return Optional.empty();
    }
}
