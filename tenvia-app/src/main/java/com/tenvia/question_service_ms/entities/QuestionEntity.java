package com.tenvia.question_service_ms.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Entity represent a question with 4 choices and one correct answer linked by the choices index
 */
@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The question content.
     */
    @Column(name = "question_text", length = 1000)
    private String questionText;

    @OneToMany(mappedBy = "questionEntity")
    private List<QuestionOptionEntity> options = new ArrayList<>();

    private String correctLetter;

    /**
     * The explanation for the correct answer.
     */
    @Column(columnDefinition = "NVARCHAR(MAX)") // for SQL Server/H2
    // @Column(columnDefinition = "TEXT") // for unlimited text
    private String explanation;

}
