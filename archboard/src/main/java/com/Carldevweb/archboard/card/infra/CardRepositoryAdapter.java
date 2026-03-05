package com.Carldevweb.archboard.card.infra;

import com.Carldevweb.archboard.card.domain.Card;
import com.Carldevweb.archboard.card.domain.CardRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CardRepositoryAdapter implements CardRepository {

    private final SpringDataCardRepository repository;

    public CardRepositoryAdapter(SpringDataCardRepository repository) {
        this.repository = repository;
    }

    @Override
    public Card save(Card card) {
        CardEntity entity = repository.save(CardEntity.fromDomain(card));
        return entity.toDomain();
    }

    @Override
    public Optional<Card> findById(Long id) {
        return repository.findById(id).map(CardEntity::toDomain);
    }

    @Override
    public List<Card> findByColumnId(Long columnId) {
        return repository.findByColumnIdOrderByPositionAsc(columnId)
                .stream()
                .map(CardEntity::toDomain)
                .toList();
    }

    @Override
    public void delete(Card card) {
        repository.deleteById(card.getId());
    }

    @Override
    public int findMaxPosition(Long columnId) {
        return repository.findMaxPosition(columnId);
    }
}