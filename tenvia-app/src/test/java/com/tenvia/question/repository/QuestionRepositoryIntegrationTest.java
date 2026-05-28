package com.tenvia.question.repository;

import com.tenvia.TenviaApplication;
import com.tenvia.question.repositories.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ContextConfiguration(classes = TenviaApplication.class)
@DataJpaTest
class QuestionRepositoryIntegrationTest {

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    void canFindAllIds() {
        List<Long> allIds = questionRepository.findAllIds();
        assertThat(allIds).isNotEmpty();
    }

    @Test
    void canFindExcludes() {
        List<Long> allIds = questionRepository.findAllIds();
        assertThat(allIds).isNotEmpty();

        List<Long> excludedIds = allIds.subList(0, 3);
        List<Long> remainIds = questionRepository.findIdsExcluding(excludedIds);
        assertThat(remainIds).doesNotContainAnyElementsOf(excludedIds);
    }
}