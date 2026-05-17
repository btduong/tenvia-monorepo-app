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

@Builder
public record GameSessionDTO(List<QuestionDTO> questions,
                             int currentQuestionIndex,
                             int score,
                             UUID id,
                             UserDTO user,
                             long duration,
                             String endTime) {

    public static GameSessionDTO from(GameSessionEntity entity, List<QuestionDTO> questions, long remainingDurationInSeconds) {
        UserEntity user = entity.getUser();
        return GameSessionDTO.builder()
                .id(entity.getId())
                .score(entity.getScore())
                .questions(questions)
                .currentQuestionIndex(entity.getCurrentQuestionIndex())
                .user(new UserDTO(user.getId(), user.getUsername(), user.getCreatedAt(), user.getBalance(), new HashMap<>()))
                .duration(remainingDurationInSeconds)
                .endTime(entity.getEndTime().toString())
                .build();
    }

}
