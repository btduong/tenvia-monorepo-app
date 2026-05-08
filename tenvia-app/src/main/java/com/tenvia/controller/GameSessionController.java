package com.tenvia.controller;

import com.tenvia.dto.AnswerRequestDTO;
import com.tenvia.dto.AnswerResponseDTO;
import com.tenvia.dto.GameSessionDTO;
import com.tenvia.dto.QuestionResponse;
import com.tenvia.dto.QuestionRewardResponse;
import com.tenvia.services.GameSessionService;
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

    @PostMapping("/{sessionId}/{id}/fifty-fifty")
    public ResponseEntity<List<Integer>> getFiftyFifty(@PathVariable UUID sessionId, @PathVariable Integer id) {
        List<Integer> fiftyFiftyIds = gameSessionService.applyFiftyFiftyOption(sessionId);
        return ResponseEntity.ok(fiftyFiftyIds);
    }

    @GetMapping("/{sessionId}/questions/next")
    public QuestionRewardResponse getNextQuestion(@PathVariable UUID sessionId) {
        return gameSessionService.getNextQuestion(sessionId);
    }
}
