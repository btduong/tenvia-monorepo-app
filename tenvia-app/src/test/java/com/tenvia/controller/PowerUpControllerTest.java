package com.tenvia.controller;

import com.tenvia.PowerUpType;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.dto.AppliedEffectResult;
import com.tenvia.dto.PowerUpResponseDTO;
import com.tenvia.dto.QuestionResponse;
import com.tenvia.dto.UserDTO;
import com.tenvia.services.PowerUpService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class PowerUpControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PowerUpService powerUpService;


    private final Long userId = 1L;
    private final UUID sessionId = UUID.randomUUID();

    @Test
    void canUsePowerUp() throws Exception {
        PowerUpType powerToUse = PowerUpType.HAMMER;
        LocalDateTime createdAt = LocalDateTime.now();
        UserDTO userDTO = new UserDTO(1L, "alice", createdAt, 0, new HashMap<>());
        QuestionDTO questionDTO = new QuestionDTO(1L, "who are you", new ArrayList<>(), false, "A", "xplained", 1L, 15);
        QuestionResponse questionResponse = QuestionResponse.from(questionDTO, 0, 15);
        AppliedEffectResult effectResult = new AppliedEffectResult(true, powerToUse, questionResponse);
        PowerUpResponseDTO response = new PowerUpResponseDTO(userDTO, effectResult);

        when(powerUpService.applyPowerUp(userId, sessionId, powerToUse)).thenReturn(response);

        String responseJson = mockMvc.perform(post("/api/powerups/use")
                        .param("type", powerToUse.toString())
                        .param("userId", userId.toString())
                        .param("sessionId", sessionId.toString()))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        PowerUpResponseDTO responseData = objectMapper.readValue(responseJson, PowerUpResponseDTO.class);
        assertThat(responseData.updatedUser().username()).isEqualTo("alice");
        assertThat(responseData.updatedUser().balance()).isEqualTo(0);
        assertThat(responseData.updatedUser().createdAt()).isEqualTo(createdAt);
        assertThat(responseData.effectResult().canUsePowerUps()).isTrue();

    }

    @Test
    void expect400_whenParamTypesAreInvalid() throws Exception {

        mockMvc.perform(post("/api/powerups/use")
                        .param("type", "invalidType")
                        .param("userId", userId.toString())
                        .param("sessionId", sessionId.toString()))
                .andExpect(status().is4xxClientError());
    }

}