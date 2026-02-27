package levelup42.trivia.infraestructure.adapter.out.persistence.mapper;

import levelup42.trivia.domain.model.Question;
import levelup42.trivia.infraestructure.adapter.out.persistence.entity.QuestionEntity;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public QuestionEntity toEntity(Question domain) {
        if (domain == null) return null;
        QuestionEntity entity = new QuestionEntity();
        entity.setId(domain.getId());
        entity.setStatement(domain.getStatement());
        entity.setOptionA(domain.getOptionA());
        entity.setOptionB(domain.getOptionB());
        entity.setOptionC(domain.getOptionC());
        entity.setOptionD(domain.getOptionD());
        entity.setCorrectOption(domain.getCorrectOption());
        entity.setExplanation(domain.getExplanation());
        entity.setSubject(domain.getSubject());
        entity.setTopic(domain.getTopic());
        entity.setDifficulty(domain.getDifficulty());
        entity.setActive(domain.isActive());
        return entity;
    }

    public Question toDomain(QuestionEntity entity) {
        if (entity == null) return null;
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
                entity.isActive()
        );
    }
}
