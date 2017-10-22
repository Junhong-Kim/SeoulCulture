package com.kimjunhong.seoulculture.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureEventBookmarkList extends RealmObject {
    RealmList<CultureEventBookmark> eventBookmarkList;

    public RealmList<CultureEventBookmark> getEventBookmarkList() {
        return eventBookmarkList;
    }
}
