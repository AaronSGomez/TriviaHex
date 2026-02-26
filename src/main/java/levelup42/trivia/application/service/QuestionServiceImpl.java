package levelup42.trivia.application.service;


import levelup42.trivia.domain.model.Question;
import levelup42.trivia.domain.port.in.CreateQuestionUseCase;
import levelup42.trivia.infraestructure.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuestionServiceImpl implements CreateQuestionUseCase {

    private final QuestionRepository repo;

    public QuestionServiceImpl(QuestionRepository repo) {
        this.repo = repo;
    }


    @Override
    public List<Question> findAll() {
        return repo.findAll();
    }

    @Override
    public Optional<Question> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public Question save(Question q) {
        return repo.save(q);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
