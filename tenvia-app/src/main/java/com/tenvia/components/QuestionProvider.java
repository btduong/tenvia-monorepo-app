package com.tenvia.components;

import com.tenvia.dto.QuestionDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class QuestionProvider {

    private final WebClient webClient;

    public QuestionProvider(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public QuestionDTO fetchQuestionById(Long questionId) {
        return this.webClient.get()
                .uri("/questions/{id}", questionId)
                .retrieve()
                .bodyToMono(QuestionDTO.class)
                .block(); // Standard synchronous wait
    }

    public List<QuestionDTO> fetchRandomQuestions(int limit) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/questions/random")
                        .queryParam("limit", limit).build())
                .retrieve()
                .bodyToFlux(QuestionDTO.class)
                .collectList()
                .block();// flux web client is async, block -> syn request
    }
}
