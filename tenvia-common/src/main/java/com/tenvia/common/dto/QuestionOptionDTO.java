package com.tenvia.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionDTO {
    /**
     * The unique id.
     */
    private Integer id;

    /**
     * The content of the option ie "The Vietnam war ended in 1975"
     */
    private String content;

    /**
     * The letter of the option ie "A"
     */
    private String letter;

    /**
     * Indicate whether this option is available for selecting.
     */
    private Boolean isAvailable;

    @JsonProperty("isAvailable")
    public boolean isAvailable() {
        return isAvailable == null || isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}
