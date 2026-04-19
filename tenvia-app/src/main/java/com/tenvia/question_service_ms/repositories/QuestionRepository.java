package com.tenvia.question_service_ms.repositories;

import com.tenvia.question_service_ms.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<QuestionEntity> findRandomQuestions();

}
