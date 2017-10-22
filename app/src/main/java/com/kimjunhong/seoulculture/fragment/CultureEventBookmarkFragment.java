package com.kimjunhong.seoulculture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.GridViewAdapter;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventBookmark;
import com.kimjunhong.seoulculture.model.CultureEventData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureEventBookmarkFragment extends Fragment {
    @BindView(R.id.gridView_cultureEvent_bookmark) GridView gridView;
    @BindView(R.id.defaultLayout_cultureEvent_bookmark) FrameLayout defaultLayout;

    ArrayList<CultureEventItem> items = new ArrayList<>();
    Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture_event_bookmark, container, false);
        ButterKnife.bind(this, view);

        initView();
        getCultureEvents();
        return view;
    }

    private void initView() {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<CultureEventBookmark> bookmarks = CultureEventBookmark.findAll(realm);
                    if(bookmarks.size() == 0) {
                        defaultLayout.setVisibility(View.VISIBLE);
                    } else {
                        defaultLayout.setVisibility(View.INVISIBLE);
                    }
                }
            });
        } finally {
            realm.close();
        }
    }

    private void getCultureEvents() {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(final Realm realm) {
                    final RealmResults<CultureEventBookmark> bookmarks = CultureEventBookmark.findAll(realm);
                    final CultureEventItem[] item = new CultureEventItem[bookmarks.size()];

                    for(int i = 0; i < bookmarks.size(); i++) {
                        final int count = i;
                        final CultureEventBookmark bookmark = bookmarks.get(i);

                        CultureEventService service = CultureEventService.retrofit.create(CultureEventService.class);
                        Call<CultureEventData> call = service.getCultureEvent(1, 1, bookmark.getEventId());
                        call.enqueue(new retrofit2.Callback<CultureEventData>() {
                            @Override
                            public void onResponse(Call<CultureEventData> call, Response<CultureEventData> response) {
                                // 행사 불러오기
                                CultureEvent cultureEvent = response.body().getSearchConcertDetailService().getRow().get(0);
                                item[count] = new CultureEventItem(cultureEvent.getCULTCODE(),
                                                                   cultureEvent.getMAIN_IMG(),
                                                                   cultureEvent.getIS_FREE(),
                                                                   cultureEvent.getCODENAME(),
                                                                   cultureEvent.getTITLE(),
                                                                   cultureEvent.getGCODE(),
                                                                   cultureEvent.getPLACE(),
                                                                   cultureEvent.getSTRTDATE(),
                                                                   cultureEvent.getEND_DATE());
                                items.add(item[count]);
                                initGridView(items);
                            }

                            @Override
                            public void onFailure(Call<CultureEventData> call, Throwable t) {

                            }
                        });
                    }
                }
            });
        } finally {
            realm.close();
        }

    }

    private void initGridView(ArrayList<CultureEventItem> items) {
        gridView.setAdapter(new GridViewAdapter(getActivity(), items));
    }
}
