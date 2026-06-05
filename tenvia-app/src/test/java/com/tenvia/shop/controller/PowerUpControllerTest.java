package com.tenvia.shop.controller;

import com.tenvia.common.UserRole;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.security.JwtUtil;
import com.tenvia.session.dto.AppliedEffectResult;
import com.tenvia.session.exceptions.InvalidSessionOwnerException;
import com.tenvia.shop.PowerUpType;
import com.tenvia.shop.dto.PowerUpResponseDTO;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.shop.services.PowerUpService;
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
    private JwtUtil jwtUtil;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private PowerUpService powerUpService;


    private final Long userId = 1L;
    private final UUID sessionId = UUID.randomUUID();

    @Test
    void canUsePowerUp() throws Exception {
        String token = jwtUtil.generateToken(1L, UserRole.ROLE_USER);
        PowerUpType powerToUse = PowerUpType.HAMMER;
        LocalDateTime createdAt = LocalDateTime.now();
        UserDTO userDTO = new UserDTO(1L, "alice", createdAt, 0, new HashMap<>());
        QuestionDTO questionDTO = new QuestionDTO(1L, "who are you", new ArrayList<>(), false, "A", "xplained", 1L, 15);
        ClientQuestionDTO questionResponse = ClientQuestionDTO.from(questionDTO, 0, 15);
        AppliedEffectResult effectResult = new AppliedEffectResult(true, powerToUse, questionResponse);
        PowerUpResponseDTO response = new PowerUpResponseDTO(userDTO, effectResult);

        when(powerUpService.applyPowerUp(userId, sessionId, powerToUse)).thenReturn(response);

        String responseJson = mockMvc.perform(post("/api/powerups/use")
                        .header("Authorization", "Bearer " + token)
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

    @Test
    void expect403_whenUserDoesNotOwnTheSession() throws Exception {
        PowerUpType powerUpToUse = PowerUpType.HAMMER;
        when(powerUpService.applyPowerUp(userId, sessionId, powerUpToUse)).thenThrow(new InvalidSessionOwnerException(sessionId, userId));

        mockMvc.perform(post("/api/powerups/use")
                        .param("type", powerUpToUse.toString())
                        .param("userId", userId.toString())
                        .param("sessionId", sessionId.toString()))
                .andExpect(status().isForbidden());
    }

}