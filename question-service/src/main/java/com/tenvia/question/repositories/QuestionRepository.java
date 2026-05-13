package com.tenvia.question.repositories;

import com.tenvia.question.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    @Query(value = "SELECT * FROM questions ORDER BY RAND() LIMIT 10", nativeQuery = true)
    List<QuestionEntity> findRandomQuestions();

    @Query(value = "SELECT * from questions where id NOT IN :excludedIds ORDER BY RAND() LIMIT 1", nativeQuery = true)
    QuestionEntity findRandomQuestionExcluding(@Param("excludedIds") List<Long> excludedIds);

}
