package com.tenvia.leaderboard_ms.service;

import com.tenvia.common.event.ScoreSubmittedEvent;
import com.tenvia.leaderboard_ms.repository.LeaderboardRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class LeaderboardServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private LeaderboardService leaderboardService;
    @MockitoSpyBean
    private LeaderboardRepository leaderboardRepository;

    @Test
    void canCacheScore() throws Exception {
        ScoreSubmittedEvent event = new ScoreSubmittedEvent("A", 1);
        leaderboardService.saveScore(event);

        mockMvc.perform(get("/leaderboard")).andExpect(status().isOk());
        mockMvc.perform(get("/leaderboard")).andExpect(status().isOk());
        mockMvc.perform(get("/leaderboard")).andExpect(status().isOk());

        verify(leaderboardRepository).findTop10ByOrderByScoreDesc();


    }
}