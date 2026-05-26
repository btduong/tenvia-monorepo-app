package com.tenvia.question.repository;

import com.tenvia.TenviaApplication;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.repositories.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ContextConfiguration(classes = TenviaApplication.class)
@DataJpaTest
class QuestionRepositoryIntegrationTest {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void canFindRandomQuestions() {
        List<QuestionEntity> questionEntityList = questionRepository.findRandomQuestions(10);
        assertEquals(10, questionEntityList.size());

    }

    @Test
    void canFindRandomQuestionExcluding() {
        List<QuestionEntity> questionEntityList = questionRepository.findRandomQuestions(10);
        assertEquals(10, questionEntityList.size());

        List<Long> excludedIds = questionEntityList.stream().map(QuestionEntity::getId).toList();
        QuestionEntity randomQuestion = questionRepository.findRandomQuestionExcluding(excludedIds);
        assertThat(questionEntityList).doesNotContain(randomQuestion);
    }
}