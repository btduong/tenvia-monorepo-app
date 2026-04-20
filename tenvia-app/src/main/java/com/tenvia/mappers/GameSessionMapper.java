package com.tenvia.mappers;

import com.tenvia.dto.GameSessionDTO;
import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.dto.UserDTO;
import com.tenvia.entities.GameSessionEntity;
import com.tenvia.entities.UserEntity;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Component
public class GameSessionMapper {

    public GameSessionDTO toDTO(GameSessionEntity entity, List<QuestionDTO> questions) {
        UserEntity user = entity.getUser();

        return GameSessionDTO.builder()
                .id(entity.getId())
                .score(entity.getScore())
                .fiftyFiftyUsed(entity.isFiftyFiftyUsed())
                .questions(questions)
                .currentQuestionIndex(entity.getCurrentQuestionIndex())
                .user(new UserDTO(user.getId(), user.getUsername(), user.getCreatedAt(), user.getBalance(), new HashMap<>()))
                .duration(Duration.between(LocalDateTime.now(), entity.getEndTime()).getSeconds())
                .endTime(entity.getEndTime().toString())
                .build();
    }
}
