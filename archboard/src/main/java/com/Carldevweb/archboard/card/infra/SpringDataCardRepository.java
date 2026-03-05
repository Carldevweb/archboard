package com.Carldevweb.archboard.card.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataCardRepository extends JpaRepository<CardEntity, Long> {

    List<CardEntity> findByColumnIdOrderByPositionAsc(Long columnId);

    @Query("select coalesce(max(c.position), -1) from CardEntity c where c.columnId = :columnId")
    int findMaxPosition(Long columnId);
}