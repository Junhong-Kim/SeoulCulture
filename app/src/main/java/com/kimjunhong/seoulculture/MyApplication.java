package com.kimjunhong.seoulculture;

import android.app.Application;

import com.kimjunhong.seoulculture.model.CultureEventBookmarkList;
import com.kimjunhong.seoulculture.model.CultureSpaceBookmarkList;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by INMA on 2017. 10. 9..
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Realm.init(this);
        RealmConfiguration realmConfig = new RealmConfiguration.Builder()
                .initialData(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.createObject(CultureEventBookmarkList.class);
                        realm.createObject(CultureSpaceBookmarkList.class);
                    }
                })
                .build();
        Realm.setDefaultConfiguration(realmConfig);
    }
}
