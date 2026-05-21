package com.tenvia.question;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.entities.QuestionOptionEntity;
import com.tenvia.question.service.QuestionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class QuestionMapperTest {

    @Test
    void canMapFrom_singleQuestionEntity() {
        QuestionOptionEntity opt1 = new QuestionOptionEntity("A", "opt_content");
        QuestionEntity questionEntity = new QuestionEntity("who are you", "A", "explained");
        questionEntity.addOption(opt1);

        ReflectionTestUtils.setField(opt1, "id", 1L);

        QuestionDTO questionDTO = QuestionMapper.from(questionEntity);
        assertNotNull(questionDTO);
        assertEquals("A", questionDTO.correctLetter());
        assertEquals("who are you", questionDTO.questionText());
        assertEquals("explained", questionDTO.explanation());
    }

    @Test
    void canMapFrom_listOfQuestionEntity() {
        QuestionOptionEntity opt1 = new QuestionOptionEntity("A", "opt_1_content");
        QuestionOptionEntity opt2 = new QuestionOptionEntity("B", "opt_2_content");
        QuestionEntity questionEntity = new QuestionEntity("who are you", "A", "question_1_explained");
        questionEntity.addOption(opt1);
        QuestionEntity questionEntity2 = new QuestionEntity("who am I", "B", "question_2_explained");
        questionEntity2.addOption(opt2);

        ReflectionTestUtils.setField(opt1, "id", 1L);
        ReflectionTestUtils.setField(opt2, "id", 2L);

        List<QuestionDTO> result = QuestionMapper.from(List.of(questionEntity, questionEntity2));

        assertEquals(2, result.size());

        QuestionDTO questionDTO1 = result.get(0);
        assertNotNull(questionDTO1);
        assertEquals("A", questionDTO1.correctLetter());
        assertEquals("who are you", questionDTO1.questionText());
        assertEquals("question_1_explained", questionDTO1.explanation());

        QuestionDTO questionDTO2 = result.get(1);
        assertNotNull(questionDTO2);
        assertEquals("B", questionDTO2.correctLetter());
        assertEquals("who am I", questionDTO2.questionText());
        assertEquals("question_2_explained", questionDTO2.explanation());
    }
}