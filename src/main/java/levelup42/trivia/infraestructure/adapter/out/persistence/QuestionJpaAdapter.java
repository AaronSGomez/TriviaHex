package levelup42.trivia.infraestructure.adapter.out.persistence;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.out.QuestionRepositoryPort;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.QuestionEntity;
import levelup42.trivia.infraestructure.adapter.out.persistence.mapper.QuestionMapper;
import levelup42.trivia.infraestructure.adapter.out.persistence.repository.DataQuestionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class QuestionJpaAdapter implements QuestionRepositoryPort {

    private final DataQuestionRepository repository;
    private final QuestionMapper mapper;

    public QuestionJpaAdapter(DataQuestionRepository repository, QuestionMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Question saveQuestion(Question question) {
        QuestionEntity entity = mapper.toEntity(question);
        QuestionEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Question> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Question> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteQuestion(Long id) {
        repository.deleteById(id);
    }
}
