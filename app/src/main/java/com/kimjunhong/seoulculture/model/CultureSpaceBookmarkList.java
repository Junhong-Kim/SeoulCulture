package com.kimjunhong.seoulculture.model;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureSpaceBookmarkList  extends RealmObject {
    RealmList<CultureSpaceBookmark> spaceBookmarkList;

    public RealmList<CultureSpaceBookmark> getSpaceBookmarkList() {
        return spaceBookmarkList;
    }
}
