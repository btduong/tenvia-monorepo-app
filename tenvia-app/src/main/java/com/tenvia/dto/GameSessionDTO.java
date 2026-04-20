package com.tenvia.dto;

import com.tenvia.common.dto.QuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class GameSessionDTO {

    private List<QuestionDTO> questions;
    private boolean fiftyFiftyUsed = false;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private UUID id;
    private UserDTO user;
    private long duration;
    private String endTime;
}
