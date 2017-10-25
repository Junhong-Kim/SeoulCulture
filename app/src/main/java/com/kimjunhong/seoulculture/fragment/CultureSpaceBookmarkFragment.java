package com.kimjunhong.seoulculture.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kimjunhong.seoulculture.CultureSpaceService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.CultureSpaceAdapter;
import com.kimjunhong.seoulculture.item.CultureSpaceItem;
import com.kimjunhong.seoulculture.model.CultureSpace;
import com.kimjunhong.seoulculture.model.CultureSpaceBookmark;
import com.kimjunhong.seoulculture.model.CultureSpaceData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureSpaceBookmarkFragment extends Fragment {
    @BindView(R.id.recyclerView_cultureSpace_bookmark) RecyclerView recyclerView;
    @BindView(R.id.defaultLayout_cultureSpace_bookmark) FrameLayout defaultLayout;

    private ArrayList<CultureSpaceItem> spaceItems = new ArrayList<>();
    private Realm realm;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture_space_bookmark, container, false);
        ButterKnife.bind(this, view);

        initView();
        getCultureSpaces();
        return view;
    }

    private void initView() {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    RealmResults<CultureSpaceBookmark> bookmarks = CultureSpaceBookmark.findAll(realm);
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

    private void getCultureSpaces() {
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(final Realm realm) {
                    final RealmResults<CultureSpaceBookmark> bookmarks = CultureSpaceBookmark.findAll(realm);
                    final int bookmarkSize = bookmarks.size();
                    final int spaceId[] = new int[bookmarkSize];
                    final CultureSpaceItem[] spaceItem = new CultureSpaceItem[bookmarkSize];

                    for(int i = 0; i < bookmarkSize; i++) {
                        spaceId[i] = bookmarks.get(i).getSpaceId();
                    }
                    // 북마크 항목 동기처리
                    new AsyncTask<Void, Integer, ArrayList<CultureSpaceItem>>() {
                        @Override
                        protected ArrayList<CultureSpaceItem> doInBackground(Void... voids) {
                            for(int i = 0; i < bookmarkSize; i++) {
                                final int pos = i;

                                CultureSpaceService spaceService = CultureSpaceService.retrofit.create(CultureSpaceService.class);
                                Call<CultureSpaceData> spaceCall = spaceService.getCultureSpace(1, 1, spaceId[pos]);
                                try {
                                    CultureSpace cultureSpace = spaceCall.execute().body().getSearchCulturalFacilitiesDetailService().getRow().get(0);
                                    spaceItem[pos] = new CultureSpaceItem(cultureSpace.getFAC_CODE(),
                                                                          cultureSpace.getCODENAME(),
                                                                          cultureSpace.getFAC_NAME(),
                                                                          cultureSpace.getMAIN_IMG(),
                                                                          cultureSpace.getADDR(),
                                                                          cultureSpace.getENTRFREE(),
                                                                          cultureSpace.getETC_DESC(),
                                                                          true);

                                    spaceItems.add(spaceItem[pos]);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return spaceItems;
                        }

                        @Override
                        protected void onPostExecute(ArrayList<CultureSpaceItem> spaceItems) {
                            super.onPostExecute(spaceItems);
                            initRecyclerView(spaceItems);
                        }
                    }.execute();
                }
            });
        } finally {
            realm.close();
        }
    }

    private void initRecyclerView(List<CultureSpaceItem> items) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new CultureSpaceAdapter(getActivity(), items));
    }
}
