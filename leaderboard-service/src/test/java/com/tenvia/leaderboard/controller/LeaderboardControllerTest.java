package com.tenvia.leaderboard.controller;

import com.tenvia.leaderboard.dto.LeaderboardDTO;
import com.tenvia.leaderboard.service.LeaderboardService;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LeaderboardController.class)
class LeaderboardControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
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

        String responseData = mockMvc.perform(get("/leaderboard"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<LeaderboardDTO> topScores = objectMapper.readValue(responseData, new TypeReference<>() {
        });
        assertThat(topScores.size()).isEqualTo(2);
        assertThat(topScores.get(0).userName()).isEqualTo("Alice");
        assertThat(topScores.get(0).score()).isEqualTo(15);
        assertThat(topScores.get(1).userName()).isEqualTo("Bob");
        assertThat(topScores.get(1).score()).isEqualTo(10);

    }

    @Test
    void getTopScores_expectedEmptyList_whenNoScoresExist() throws Exception {

        when(leaderboardService.getTopScores()).thenReturn(Lists.newArrayList());

        mockMvc.perform(get("/leaderboard"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.length()").value(0));
    }
}