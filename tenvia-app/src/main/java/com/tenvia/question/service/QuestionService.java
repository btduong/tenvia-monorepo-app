package com.tenvia.question.service;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    public List<QuestionDTO> fetchRandomQuestion(int limit) {
        List<QuestionEntity> randomQuestions = fetchInitialQuestions(limit);
        return QuestionMapper.from(randomQuestions);
    }

    public QuestionDTO getQuestionById(Long id) {
        QuestionEntity questionEntity = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("No question with id"));
        return QuestionMapper.from(questionEntity);
    }

    public QuestionDTO swapQuestion(List<Long> excludedIds) {
        QuestionEntity question;
        if (excludedIds == null || excludedIds.isEmpty()) {
            question = questionRepository.findRandomQuestions(1).stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("No questions available"));
        } else {
            question = questionRepository.findRandomQuestionExcluding(excludedIds);
            if (question == null) {
                throw new IllegalStateException("No additional questions available to swap");
            }
        }
        return QuestionMapper.from(question);
    }

    private List<QuestionEntity> fetchInitialQuestions(int limit) {
        List<QuestionEntity> randomQuestions = questionRepository.findRandomQuestions(limit);
        if (randomQuestions.isEmpty()) {
            throw new IllegalStateException("No questions available");
        }
        return randomQuestions;
    }
}
