package com.kimjunhong.seoulculture.model;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureSpaceBookmark extends RealmObject {
    @PrimaryKey
    private int spaceId;

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
    }

    // 문화공간 북마크 추가
    public static void create(Realm realm, CultureSpaceBookmark bookmark) {
        CultureSpaceBookmarkList bookmarkList = realm.where(CultureSpaceBookmarkList.class).findFirst();
        RealmList<CultureSpaceBookmark> bookmarks = bookmarkList.getSpaceBookmarkList();

        CultureSpaceBookmark newBookmark = realm.createObject(CultureSpaceBookmark.class, bookmark.getSpaceId());
        bookmarks.add(newBookmark);
    }

    // 문화공간 북마크 삭제
    public static void delete(Realm realm, int spaceId) {
        CultureSpaceBookmark bookmark = findOne(realm, spaceId);
        bookmark.deleteFromRealm();
    }

    // 문화공간 북마크 가져오기
    public static CultureSpaceBookmark findOne(Realm realm, int spaceId) {
        return realm.where(CultureSpaceBookmark.class).equalTo("spaceId", spaceId).findFirst();
    }

    // 문화공간 북마크 리스트 가져오기
    public static RealmResults<CultureSpaceBookmark> findAll(Realm realm) {
        return realm.where(CultureSpaceBookmark.class).findAll();
    }
}
