package com.Carldevweb.archboard.card.api;

import com.Carldevweb.archboard.card.api.dto.*;
import com.Carldevweb.archboard.card.app.*;
import com.Carldevweb.archboard.security.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CardController {

    private final CurrentUser currentUser;
    private final CreateCardUseCase createCardUseCase;
    private final ListCardsUseCase listCardsUseCase;
    private final UpdateCardUseCase updateCardUseCase;
    private final MoveCardUseCase moveCardUseCase;
    private final DeleteCardUseCase deleteCardUseCase;

    public CardController(
            CurrentUser currentUser,
            CreateCardUseCase createCardUseCase,
            ListCardsUseCase listCardsUseCase,
            UpdateCardUseCase updateCardUseCase,
            MoveCardUseCase moveCardUseCase,
            DeleteCardUseCase deleteCardUseCase
    ) {
        this.currentUser = currentUser;
        this.createCardUseCase = createCardUseCase;
        this.listCardsUseCase = listCardsUseCase;
        this.updateCardUseCase = updateCardUseCase;
        this.moveCardUseCase = moveCardUseCase;
        this.deleteCardUseCase = deleteCardUseCase;
    }

    @PostMapping("/columns/{columnId}/cards")
    @ResponseStatus(HttpStatus.CREATED)
    public CardResponse create(@PathVariable Long columnId, @RequestBody @Valid CreateCardRequest request) {
        Long userId = currentUser.id();
        var created = createCardUseCase.execute(userId, columnId, request.title(), request.description());
        return CardResponse.from(created);
    }

    @GetMapping("/columns/{columnId}/cards")
    public List<CardResponse> list(@PathVariable Long columnId) {
        Long userId = currentUser.id();
        return listCardsUseCase.execute(userId, columnId).stream().map(CardResponse::from).toList();
    }

    @PatchMapping("/cards/{cardId}")
    public CardResponse update(@PathVariable Long cardId, @RequestBody @Valid UpdateCardRequest request) {
        Long userId = currentUser.id();
        var updated = updateCardUseCase.execute(userId, cardId, request.title(), request.description());
        return CardResponse.from(updated);
    }

    @PatchMapping("/cards/{cardId}/move")
    public CardResponse move(@PathVariable Long cardId, @RequestBody @Valid MoveCardRequest request) {
        Long userId = currentUser.id();
        var moved = moveCardUseCase.execute(userId, cardId, request.toColumnId(), request.position());
        return CardResponse.from(moved);
    }

    @DeleteMapping("/cards/{cardId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long cardId) {
        Long userId = currentUser.id();
        deleteCardUseCase.execute(userId, cardId);
    }
}