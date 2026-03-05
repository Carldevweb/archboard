package com.Carldevweb.archboard.column.domain;

import java.util.List;
import java.util.Optional;

public interface ColumnRepository {

    Column save(Column column);

    Optional<Column> findById(Long id);

    List<Column> findByBoardId(Long boardId);

    void delete(Column column);

    int findMaxPosition(Long boardId);
}