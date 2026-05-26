package com.tenvia.question.controller;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.dto.ClientQuestionDTO;
import com.tenvia.question.service.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class QuestionControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private QuestionService questionService;

    @Test
    void canFetchQuestions() throws Exception {
        QuestionDTO questionDTO = QuestionDTO.builder()
                .id(1L)
                .questionText("who are you")
                .options(null)
                .powerUpDisabled(false)
                .correctOptionId(1L)
                .expiresInSeconds(15)
                .explanation("explained")
                .correctLetter("A")
                .build();
        List<QuestionDTO> questionDTOList = List.of(questionDTO);

        when(questionService.fetchRandomQuestion(10)).thenReturn(questionDTOList);

        String responseData = mockMvc.perform(get("/questions/random")
                        .param("limit", "10"))
                .andExpect(status().is2xxSuccessful())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ClientQuestionDTO> questionDTOS = objectMapper.readValue(responseData, new TypeReference<>() {});

        assertThat(questionDTOS.size()).isEqualTo(1);
        ClientQuestionDTO clientQuestion = questionDTOS.get(0);
        assertThat(clientQuestion.id()).isEqualTo(1L);
        assertThat(clientQuestion.powerUpDisabled()).isFalse();
        assertThat(clientQuestion.expiresInSecond()).isEqualTo(15);
        assertThat(clientQuestion.questionText()).isEqualTo("who are you");
    }
}