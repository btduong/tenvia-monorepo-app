package com.tenvia.question_service_ms.service;

import com.tenvia.dto.QuestionDTO;
import com.tenvia.question_service_ms.QuestionMapper;
import com.tenvia.question_service_ms.entities.QuestionEntity;
import com.tenvia.question_service_ms.repositories.QuestionRepository;
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
