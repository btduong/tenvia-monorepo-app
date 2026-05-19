package com.tenvia.leaderboard.controller;

import com.tenvia.leaderboard.dto.LeaderboardDTO;
import com.tenvia.leaderboard.service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping
    public List<LeaderboardDTO> getTopScores() {
        return leaderboardService.getTopScores();
    }

}
