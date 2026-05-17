package com.tenvia.components;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.config.QuestionServiceConfig;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Component
public class QuestionProvider {

    private final WebClient webClient;

    public QuestionProvider(WebClient.Builder webClientBuilder, QuestionServiceConfig config) {
        this.webClient = webClientBuilder
                .baseUrl(config.getUrl())
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

    public QuestionDTO swapRandomQuestion(List<Long> excludedIds) {
        return webClient.post().uri(uriBuilder -> uriBuilder.path("/questions/swap").build())
                .bodyValue(excludedIds)
                .retrieve()
                .bodyToMono(QuestionDTO.class)
                .block();
    }
}
