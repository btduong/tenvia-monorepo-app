package com.tenvia.question.service;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.exceptions.QuestionNotFoundException;
import com.tenvia.question.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<QuestionDTO> fetchRandomQuestion(int limit) {
        List<QuestionEntity> randomQuestions = fetchInitialQuestions(limit);
        return QuestionMapper.from(randomQuestions);
    }

    public QuestionDTO getQuestionById(Long id) {
        QuestionEntity questionEntity = questionRepository.findById(id).orElseThrow(() -> new QuestionNotFoundException(id));
        return QuestionMapper.from(questionEntity);
    }

    /**
     * Retrieve a new question with its id NOT in the list of excluded ids.
     * If the excludedIds list is empty then retrieve a random question from all the questions.
     * @param excludedIds - a list of ids to exclude
     * @return a {@link QuestionDTO}
     */
    public QuestionDTO swapQuestion(List<Long> excludedIds) {
        List<Long> availableIds;
        if (excludedIds == null || excludedIds.isEmpty()) {
            availableIds = questionRepository.findAllIds();
        } else {
            availableIds = questionRepository.findIdsExcluding(excludedIds);
        }

        if (availableIds.isEmpty()) {
            throw new IllegalStateException("No additional questions available to swap");
        }

        List<Long> selectedIds = pickRandomIds(availableIds, 1);
        QuestionEntity question = questionRepository.findById(selectedIds.get(0))
                .orElseThrow(() -> new IllegalStateException("No questions available"));
        return QuestionMapper.from(question);
    }

    private List<QuestionEntity> fetchInitialQuestions(int limit) {
        List<Long> allIds = questionRepository.findAllIds();

        if (allIds.isEmpty()) {
            throw new IllegalStateException("No questions available");
        }

        List<Long> selectedIds = pickRandomIds(allIds, limit);
        List<QuestionEntity> questionEntities = questionRepository.findAllById(selectedIds);
        Collections.shuffle(questionEntities);
        return questionEntities;
    }

    private List<Long> pickRandomIds(List<Long> ids, int limit) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> shuffled = new ArrayList<>(ids);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, limit);
    }
}
