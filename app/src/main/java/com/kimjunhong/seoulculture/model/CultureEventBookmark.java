package com.kimjunhong.seoulculture.model;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureEventBookmark extends RealmObject {
    @PrimaryKey
    private int eventId;

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    // 문화행사 북마크 생성
    public static void create(Realm realm, CultureEventBookmark bookmark) {
        CultureEventBookmarkList bookmarkList = realm.where(CultureEventBookmarkList.class).findFirst();
        RealmList<CultureEventBookmark> bookmarks = bookmarkList.getEventBookmarkList();

        CultureEventBookmark newBookmark = realm.createObject(CultureEventBookmark.class, bookmark.getEventId());
        bookmarks.add(newBookmark);
    }

    // 문화행사 북마크 삭제
    public static void delete(Realm realm, int eventId) {
        CultureEventBookmark bookmark = findOne(realm, eventId);
        bookmark.deleteFromRealm();
    }

    // 문화행사 북마크 가져오기
    public static CultureEventBookmark findOne(Realm realm, int eventId) {
        return realm.where(CultureEventBookmark.class).equalTo("eventId", eventId).findFirst();
    }

    // 문화행사 북마크 리스트 가져오기
    public static RealmResults<CultureEventBookmark> findAll(Realm realm) {
        return realm.where(CultureEventBookmark.class).findAll();
    }
}
