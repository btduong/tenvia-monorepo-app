package com.tenvia.session.controller;

import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.session.dto.AnswerRequestDTO;
import com.tenvia.session.dto.AnswerResponseDTO;
import com.tenvia.session.dto.GameSessionDTO;
import com.tenvia.session.services.GameSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public GameSessionDTO startNewSession(@AuthenticationPrincipal String userIdString, @RequestParam int limit) {

        Long userId = Long.valueOf(userIdString);

        return gameSessionService.createNewSession(userId, limit);
    }

    @PostMapping("/{sessionId}/answer")
    public ResponseEntity<AnswerResponseDTO> verify(@AuthenticationPrincipal String userIdString, @PathVariable UUID sessionId, @RequestBody AnswerRequestDTO request) {
        Long userId = Long.valueOf(userIdString);
        AnswerResponseDTO answerResponseDTO = gameSessionService.validateAnswer(sessionId, request.getSelectedOptionId(), userId);
        return ResponseEntity.ok(answerResponseDTO);
    }

    @GetMapping("/{sessionId}/questions/next")
    public ClientQuestionDTO getNextQuestion(@AuthenticationPrincipal String userIdString, @PathVariable UUID sessionId) {
        Long userId = Long.valueOf(userIdString);
        return gameSessionService.getNextQuestion(sessionId, userId);
    }

    @PostMapping("/{sessionId}/abandon")
    public ResponseEntity<Void> abandonSession(@AuthenticationPrincipal String userIdString, @PathVariable UUID sessionId) {
        Long userId = Long.valueOf(userIdString);
        gameSessionService.abandonSession(sessionId, userId);
        return ResponseEntity.ok().build();
    }

}
