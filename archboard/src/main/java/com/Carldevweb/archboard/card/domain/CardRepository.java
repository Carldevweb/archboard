package com.Carldevweb.archboard.card.domain;

import java.util.List;
import java.util.Optional;

public interface CardRepository {

    Card save(Card card);

    Optional<Card> findById(Long id);

    List<Card> findByColumnId(Long columnId);

    void delete(Card card);

    int findMaxPosition(Long columnId);
}