package com.tenvia.session.dto;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.session.entities.GameSessionEntity;
import com.tenvia.user.dto.UserDTO;
import com.tenvia.user.entities.UserEntity;
import lombok.Builder;

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
