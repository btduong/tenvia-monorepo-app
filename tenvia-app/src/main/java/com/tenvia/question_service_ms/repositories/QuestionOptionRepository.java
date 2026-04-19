package com.tenvia.question_service_ms.repositories;

import com.tenvia.question_service_ms.entities.QuestionOptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOptionEntity, Integer> {

}
