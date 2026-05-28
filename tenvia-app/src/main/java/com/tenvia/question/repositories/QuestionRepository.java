package com.tenvia.question.repositories;

import com.tenvia.question.entities.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {

    /**
     * Retrieve all questions ids using the primary key as they are indexed.
     * @return a list of question ids
     */
    @Query("SELECT q.id FROM QuestionEntity q")
    List<Long> findAllIds();

    /**
     * Retrieve all question ids excluding ids in the list of excluded ids.
     * @param excludedIds a list of excluded ids
     * @return a list of question ids not in the excluded ids list
     */
    @Query("SELECT q.id FROM QuestionEntity q WHERE q.id NOT IN :excludedIds")
    List<Long> findIdsExcluding(@Param("excludedIds") List<Long> excludedIds);

}
