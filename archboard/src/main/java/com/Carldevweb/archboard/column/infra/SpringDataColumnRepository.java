package com.Carldevweb.archboard.column.infra;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SpringDataColumnRepository extends JpaRepository<ColumnEntity, Long> {

    List<ColumnEntity> findByBoardIdOrderByPositionAsc(Long boardId);

    @Query("select coalesce(max(c.position), -1) from ColumnEntity c where c.boardId = :boardId")
    int findMaxPosition(Long boardId);
}