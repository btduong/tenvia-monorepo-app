package com.tenvia;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.common.dto.QuestionOptionDTO;
import com.tenvia.multiplayer.model.Lobby;

import java.util.List;

public class TestJackson {
    public static void main(String[] args) throws Exception {
        Lobby lobby = new Lobby("TEST", 1L, 10);
        QuestionDTO question = QuestionDTO.builder()
            .id(1L)
            .questionText("What is 2+2?")
            .options(List.of(new QuestionOptionDTO(1L, "4", "A", true)))
            .correctOptionId(1L)
            .build();
        lobby.setCurrentQuestion(question);
        
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(lobby);
        System.out.println("JSON OUTPUT: " + json);
    }
}
