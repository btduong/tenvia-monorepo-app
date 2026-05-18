package com.tenvia.question.service;

import com.tenvia.common.dto.QuestionDTO;
import com.tenvia.question.entities.QuestionEntity;
import com.tenvia.question.entities.QuestionOptionEntity;
import com.tenvia.question.repositories.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.management.RuntimeErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private QuestionMapper questionMapper;
    @InjectMocks
    private QuestionService questionService;

    QuestionOptionEntity opt1;
    QuestionEntity questionEntity;

    @BeforeEach
    public void setUp() {
        opt1 = new QuestionOptionEntity("A", "opt_content");
        questionEntity = new QuestionEntity("who are you", "A", "explained");
        questionEntity.addOption(opt1);
        ReflectionTestUtils.setField(opt1, "id", 1);
    }

    @Test
    void canFetchRandomQuestions() {

        when(questionRepository.findRandomQuestions()).thenReturn(List.of(questionEntity));

        List<QuestionDTO> questionDTOS = questionService.fetchRandomQuestion(1);

        QuestionDTO questionDTO1 = questionDTOS.get(0);
        assertEquals(1, questionDTOS.size());
        assertEquals("who are you", questionDTO1.questionText());
        assertEquals("explained", questionDTO1.explanation());
        assertEquals("A", questionDTO1.correctLetter());
    }

    @Test
    void fetchRandomQuestion_expectException_whenResultIsEmpty() {
        when(questionRepository.findRandomQuestions()).thenReturn(new ArrayList<>());

        Exception exception = assertThrows(IllegalStateException.class, () -> questionService.fetchRandomQuestion(5));
        assertEquals("No questions available", exception.getMessage());
    }

    @Test
    void canFetchQuestionById() {
        when(questionRepository.findById(1L)).thenReturn(Optional.ofNullable(questionEntity));

        QuestionDTO question = questionService.getQuestionById(1L);
        assertEquals("who are you", question.questionText());
        assertEquals("explained", question.explanation());
        assertEquals("A", question.correctLetter());
    }

    @Test
    void fetchQuestionByID_expectException() {
        Exception runtimeException = assertThrows(RuntimeException.class, () -> questionService.getQuestionById(2L));
        assertEquals("No question with id",  runtimeException.getMessage());
    }

    @Test
    void canSwapQuestion() {
        List<Long> excludedIds = List.of(1L, 3L);
        when(questionRepository.findRandomQuestionExcluding(excludedIds)).thenReturn(questionEntity);

        QuestionDTO questionDTO = questionService.swapQuestion(excludedIds);
        assertEquals("who are you", questionDTO.questionText());
        assertEquals("explained", questionDTO.explanation());
        assertEquals("A", questionDTO.correctLetter());
    }


}