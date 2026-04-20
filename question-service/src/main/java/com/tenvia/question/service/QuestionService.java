package com.tenvia.question.service;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.QuestionMapper;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.repositories.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionMapper questionMapper;

    public List<QuestionDTO> fetchRandomQuestion(int limit) {
        List<QuestionEntity> randomQuestions = fetchInitialQuestions();
        return questionMapper.toQuestionDTO(randomQuestions);
    }

    public QuestionDTO getQuestionById(Long id) {
        QuestionEntity questionEntity = questionRepository.findById(id).orElseThrow(() -> new RuntimeException("No question with id"));
        return questionMapper.toQuestionDTO(questionEntity);
    }

    private List<QuestionEntity> fetchInitialQuestions() {
        List<QuestionEntity> randomQuestions = questionRepository.findRandomQuestions();
        if (randomQuestions.isEmpty()) {
            throw new IllegalStateException("No questions available");
        }
        return randomQuestions;
    }
}
