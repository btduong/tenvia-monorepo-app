package com.tenvia.question.controller;

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
    public ResponseEntity<List<QuestionDTO>> fetchQuestions(@RequestParam int limit) {
        List<QuestionDTO> questionEntities = questionService.fetchRandomQuestion(limit);
        return ResponseEntity.ok(questionEntities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestion(@PathVariable Long id) {
        QuestionDTO questionEntities = questionService.getQuestionById(id);
        return ResponseEntity.ok(questionEntities);
    }

    @PostMapping("/swap")
    public ResponseEntity<QuestionDTO> swapQuestion(@RequestBody List<Long> excludedIds) {
        QuestionDTO question = questionService.swapQuestion(excludedIds);
        return ResponseEntity.ok(question);
    }

}
