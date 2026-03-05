package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import com.Carldevweb.archboard.column.domain.ColumnRepository;
import com.Carldevweb.archboard.common.access.AccessService;
import com.Carldevweb.archboard.common.api.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UpdateColumnUseCase {

    private final ColumnRepository columnRepository;
    private final AccessService accessService;

    public UpdateColumnUseCase(ColumnRepository columnRepository, AccessService accessService) {
        this.columnRepository = columnRepository;
        this.accessService = accessService;
    }

    @Transactional
    public Column execute(Long userId, Long columnId, String newName, Integer newPosition) {
        Column column = columnRepository.findById(columnId)
                .orElseThrow(() -> new NotFoundException("Column not found"));

        // Vérification d'accès via board owner
        accessService.requireBoardOwner(userId, column.getBoardId());

        boolean changed = false;

        // rename
        if (newName != null && !newName.isBlank() && !newName.equals(column.getName())) {
            column.rename(newName);
            changed = true;
        }

        // move (reorder)
        if (newPosition != null) {
            Column moved = reorder(column, newPosition);
            // reorder() persiste déjà ce qu'il faut, mais on renvoie l'objet final
            return moved;
        }

        if (changed) {
            return columnRepository.save(column);
        }

        return column;
    }

    /**
     * Réordonnancement robuste sans "bulk update":
     * - charge la liste ordonnée
     * - retire la colonne
     * - réinsère à l'index demandé (clamp)
     * - réécrit les positions 0..n-1 en persistant uniquement les changements
     */
    private Column reorder(Column target, int requestedPosition) {
        Long boardId = target.getBoardId();

        List<Column> columns = new ArrayList<>(columnRepository.findByBoardId(boardId));

        int currentIndex = indexOf(columns, target.getId());
        if (currentIndex == -1) {
            // Cas théorique (incohérence DB)
            throw new NotFoundException("Column not found in board");
        }

        Column removed = columns.remove(currentIndex);

        int clamped = clamp(requestedPosition, 0, Math.max(0, columns.size()));
        columns.add(clamped, removed);

        Column updatedTarget = null;

        for (int i = 0; i < columns.size(); i++) {
            Column c = columns.get(i);

            if (c.getPosition() != i) {
                c.move(i);
                Column saved = columnRepository.save(c);
                if (saved.getId().equals(target.getId())) {
                    updatedTarget = saved;
                }
            } else {
                if (c.getId().equals(target.getId())) {
                    updatedTarget = c;
                }
            }
        }

        // Si la colonne n'a pas changé de position (ex: même index), on la renvoie telle quelle
        return updatedTarget != null ? updatedTarget : target;
    }

    private int indexOf(List<Column> columns, Long columnId) {
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getId().equals(columnId)) return i;
        }
        return -1;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}