package com.kimjunhong.seoulculture.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.CultureEventAdapter;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventBookmark;
import com.kimjunhong.seoulculture.model.CultureEventData;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureEventBookmarkFragment extends Fragment {
    @BindView(R.id.gridRecyclerView_cultureEvent_bookmark) RecyclerView recyclerView;
    @BindView(R.id.defaultLayout_cultureEvent_bookmark) FrameLayout defaultLayout;

    private ArrayList<CultureEventItem> eventItems = new ArrayList<>();
    private CultureEventAdapter mAdapter;

    private Realm realm;

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
                    final int bookmarkSize = bookmarks.size();
                    final int eventId[] = new int[bookmarkSize];
                    final CultureEventItem[] eventItem = new CultureEventItem[bookmarkSize];

                    for(int i = 0; i < bookmarkSize; i++) {
                        eventId[i] = bookmarks.get(i).getEventId();
                    }
                    // 북마크 항목 동기처리
                    new AsyncTask<Void, Integer, ArrayList<CultureEventItem>>() {
                        @Override
                        protected ArrayList<CultureEventItem> doInBackground(Void... voids) {
                            for(int i = 0; i < bookmarkSize; i++) {
                                final int pos = i;

                                CultureEventService eventService = CultureEventService.retrofit.create(CultureEventService.class);
                                Call<CultureEventData> eventCall = eventService.getCultureEvent(1, 1, eventId[pos]);
                                try {
                                    CultureEvent cultureEvent = eventCall.execute().body().getSearchConcertDetailService().getRow().get(0);
                                    eventItem[pos] = new CultureEventItem(cultureEvent.getCULTCODE(),
                                                                     cultureEvent.getMAIN_IMG(),
                                                                     cultureEvent.getIS_FREE(),
                                                                     cultureEvent.getCODENAME(),
                                                                     cultureEvent.getTITLE(),
                                                                     cultureEvent.getGCODE(),
                                                                     cultureEvent.getPLACE(),
                                                                     cultureEvent.getSTRTDATE(),
                                                                     cultureEvent.getEND_DATE(),
                                                                     true);
                                    eventItems.add(eventItem[pos]);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return eventItems;
                        }

                        @Override
                        protected void onPostExecute(ArrayList<CultureEventItem> eventItems) {
                            super.onPostExecute(eventItems);
                            initGridRecyclerView(eventItems);
                        }
                    }.execute();
                }
            });
        } finally {
            realm.close();
        }
    }

    private void initGridRecyclerView(ArrayList<CultureEventItem> eventItems) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new CultureEventAdapter(getActivity(), eventItems);
        recyclerView.setAdapter(mAdapter);
    }
}
