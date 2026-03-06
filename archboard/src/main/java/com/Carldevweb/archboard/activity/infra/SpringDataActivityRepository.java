package com.Carldevweb.archboard.activity.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataActivityRepository extends JpaRepository<ActivityEntity, Long> {

    List<ActivityEntity> findByBoardIdOrderByCreatedAtDesc(Long boardId);
}