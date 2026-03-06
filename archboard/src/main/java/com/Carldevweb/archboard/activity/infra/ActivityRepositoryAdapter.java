package com.Carldevweb.archboard.activity.infra;

import com.Carldevweb.archboard.activity.domain.Activity;
import com.Carldevweb.archboard.activity.domain.ActivityRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ActivityRepositoryAdapter implements ActivityRepository {

    private final SpringDataActivityRepository repository;

    public ActivityRepositoryAdapter(SpringDataActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    public Activity save(Activity activity) {
        ActivityEntity entity = repository.save(ActivityEntity.fromDomain(activity));
        return entity.toDomain();
    }

    @Override
    public List<Activity> findByBoardId(Long boardId) {
        return repository.findByBoardIdOrderByCreatedAtDesc(boardId)
                .stream()
                .map(ActivityEntity::toDomain)
                .toList();
    }
}