package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.QuestionEntity;
import levelup42.trivia.infraestructure.repository.QuestionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JpaQuestionRepository implements QuestionRepositoryPort {

    private final QuestionRepository repository;

    public JpaQuestionRepository(QuestionRepository repository) {
        this.repository = repository;
    }

    @Override
    public Question saveQuestion(Question question) {
        QuestionEntity entity = toEntity(question);
        QuestionEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void deleteQuestion(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Question> findAll() {
        return repository.findAll().stream().map(this::toDomain).collect(Collectors.toList());
    }

    @Override
    public Optional<Question> findById(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    private QuestionEntity toEntity(Question question) {
        QuestionEntity entity = new QuestionEntity();
        if (question.getId() != null) {
            entity.setId(question.getId());
        }
        entity.setStatement(question.getStatement());
        entity.setOptionA(question.getOptionA());
        entity.setOptionB(question.getOptionB());
        entity.setOptionC(question.getOptionC());
        entity.setOptionD(question.getOptionD());
        entity.setCorrectOption(question.getCorrectOption());
        entity.setExplanation(question.getExplanation());
        entity.setSubject(question.getSubject());
        entity.setTopic(question.getTopic());
        entity.setDifficulty(question.getDifficulty());
        entity.setActive(question.isActive());
        return entity;
    }

    private Question toDomain(QuestionEntity entity) {
        return new Question(
                entity.getId(),
                entity.getStatement(),
                entity.getOptionA(),
                entity.getOptionB(),
                entity.getOptionC(),
                entity.getOptionD(),
                entity.getCorrectOption(),
                entity.getExplanation(),
                entity.getSubject(),
                entity.getTopic(),
                entity.getDifficulty(),
                entity.isActive());
    }
}
