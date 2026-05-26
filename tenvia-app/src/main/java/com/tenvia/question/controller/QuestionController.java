package com.tenvia.question.controller;

import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/{id}")
    public ResponseEntity<ClientQuestionDTO> getQuestion(@PathVariable Long id) {
        QuestionDTO questionDTO = questionService.getQuestionById(id);
        return ResponseEntity.ok(ClientQuestionDTO.from(questionDTO));
    }

    @PostMapping("/swap")
    public ResponseEntity<ClientQuestionDTO> swapQuestion(@RequestBody List<Long> excludedIds) {
        QuestionDTO questionDTO = questionService.swapQuestion(excludedIds);
        return ResponseEntity.ok(ClientQuestionDTO.from(questionDTO));
    }

}
