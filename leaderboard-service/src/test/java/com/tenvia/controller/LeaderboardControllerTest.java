package com.tenvia.controller;

import com.tenvia.leaderboard.controller.LeaderboardController;
import com.tenvia.leaderboard.dto.LeaderboardDTO;
import com.tenvia.leaderboard.service.LeaderboardService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LeaderboardService leaderboardService;

    @Test
    void getTopScores_expect200() throws Exception {
        LeaderboardDTO player1 = new LeaderboardDTO("Alice", 15);
        LeaderboardDTO player2 = new LeaderboardDTO("Bob", 10);
        List<LeaderboardDTO> mockScores = List.of(player1, player2);

        when(leaderboardService.getTopScores()).thenReturn(mockScores);

        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userName").value("Alice"))
                .andExpect(jsonPath("$[0].score").value(15))
                .andExpect(jsonPath("$[1].userName").value("Bob"))
                .andExpect(jsonPath("$[1].score").value(10));;
    }

    @Test
    void getTopScores_expectedEmptyList_whenNoScoresExist() throws Exception {

        when(leaderboardService.getTopScores()).thenReturn(Lists.newArrayList());

        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(0));
    }
}