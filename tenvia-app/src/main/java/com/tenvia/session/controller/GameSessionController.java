package com.tenvia.session.controller;

import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.session.dto.AnswerRequestDTO;
import com.tenvia.session.dto.AnswerResponseDTO;
import com.tenvia.session.dto.GameSessionDTO;
import com.tenvia.session.services.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/sessions")
public class GameSessionController {

    @Autowired
    private GameSessionService gameSessionService;



    @PostMapping("/start")
    public GameSessionDTO startNewSession(@RequestParam Long id, @RequestParam int limit) {
        return gameSessionService.createNewSession(id, limit);
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<AnswerResponseDTO> verify(@PathVariable UUID sessionId, @RequestBody AnswerRequestDTO request) {
        AnswerResponseDTO answerResponseDTO = gameSessionService.validateAnswer(sessionId, request.getSelectedOptionId());
        return ResponseEntity.ok(answerResponseDTO);
    }

    @GetMapping("/{sessionId}/questions/next")
    public ClientQuestionDTO getNextQuestion(@PathVariable UUID sessionId) {
        return gameSessionService.getNextQuestion(sessionId);
    }

    @PostMapping("/{sessionId}/abandon")
    public ResponseEntity<Void> abandonSession(@PathVariable UUID sessionId) {
        gameSessionService.abandonSession(sessionId);
        return ResponseEntity.ok().build();
    }

}
