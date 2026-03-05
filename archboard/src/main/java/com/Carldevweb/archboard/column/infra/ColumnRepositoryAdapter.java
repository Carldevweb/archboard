package com.Carldevweb.archboard.column.infra;

import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ColumnRepositoryAdapter implements ColumnRepository {

    private final SpringDataColumnRepository repository;

    public ColumnRepositoryAdapter(SpringDataColumnRepository repository) {
        this.repository = repository;
    }

    @Override
    public Column save(Column column) {
        ColumnEntity entity = repository.save(ColumnEntity.fromDomain(column));
        return entity.toDomain();
    }

    @Override
    public Optional<Column> findById(Long id) {
        return repository.findById(id)
                .map(ColumnEntity::toDomain);
    }

    @Override
    public List<Column> findByBoardId(Long boardId) {
        return repository.findByBoardIdOrderByPositionAsc(boardId)
                .stream()
                .map(ColumnEntity::toDomain)
                .toList();
    }

    @Override
    public void delete(Column column) {
        repository.deleteById(column.getId());
    }

    @Override
    public int findMaxPosition(Long boardId) {
        return repository.findMaxPosition(boardId);
    }
}