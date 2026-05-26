package com.tenvia.question.controller;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/questions")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @GetMapping("/random")
    public ResponseEntity<List<ClientQuestionDTO>> fetchQuestions(@RequestParam int limit) {
        List<QuestionDTO> questionEntities = questionService.fetchRandomQuestion(limit);
        List<ClientQuestionDTO> clientQuestions = questionEntities.stream()
                .map(ClientQuestionDTO::from)
                .toList();
        return ResponseEntity.ok(clientQuestions);
    }

}
