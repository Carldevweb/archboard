package com.Carldevweb.archboard.column.app;

import com.Carldevweb.archboard.column.domain.Column;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MoveColumnUseCase {

    private final UpdateColumnUseCase updateColumnUseCase;

    public MoveColumnUseCase(UpdateColumnUseCase updateColumnUseCase) {
        this.updateColumnUseCase = updateColumnUseCase;
    }

    @Transactional
    public Column execute(Long userId, Long columnId, int position) {
        // Réutilise EXACTEMENT la logique existante (accès + reorder + persist)
        return updateColumnUseCase.execute(userId, columnId, null, position);
    }
}