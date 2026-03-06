package com.Carldevweb.archboard.activity.listener;

import com.Carldevweb.archboard.activity.app.CreateActivityUseCase;
import com.Carldevweb.archboard.card.events.CardMovedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CardActivityListener {

    private final CreateActivityUseCase createActivityUseCase;

    public CardActivityListener(CreateActivityUseCase createActivityUseCase) {
        this.createActivityUseCase = createActivityUseCase;
    }

    @EventListener
    public void onCardMoved(CardMovedEvent event) {

        createActivityUseCase.log(
                event.boardId(),
                "CARD_MOVED",
                "CARD",
                event.cardId(),
                "Card moved from column "
                        + event.fromColumnId()
                        + " to column "
                        + event.toColumnId()
        );
    }
}