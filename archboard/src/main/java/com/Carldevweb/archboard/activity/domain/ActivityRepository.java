package com.Carldevweb.archboard.activity.domain;

import java.util.List;

public interface ActivityRepository {

    Activity save(Activity activity);

    List<Activity> findByBoardId(Long boardId);
}