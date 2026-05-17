package com.tenvia.dto;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class GameSessionDTO {

    public static GameSessionDTO from(GameSessionEntity entity, List<QuestionDTO> questions) {
        UserEntity user = entity.getUser();
        return GameSessionDTO.builder()
                .id(entity.getId())
                .score(entity.getScore())
                .questions(questions)
                .currentQuestionIndex(entity.getCurrentQuestionIndex())
                .user(new UserDTO(user.getId(), user.getUsername(), user.getCreatedAt(), user.getBalance(), new HashMap<>()))
                .duration(Duration.between(LocalDateTime.now(), entity.getEndTime()).getSeconds())
                .endTime(entity.getEndTime().toString())
                .build();
    }

    private List<QuestionDTO> questions;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private UUID id;
    private UserDTO user;
    private long duration;
    private String endTime;
}
