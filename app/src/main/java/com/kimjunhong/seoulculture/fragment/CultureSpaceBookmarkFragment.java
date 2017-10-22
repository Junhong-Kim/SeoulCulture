package com.kimjunhong.seoulculture.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kimjunhong.seoulculture.CultureSpaceService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.RecyclerViewAdapter;
import com.kimjunhong.seoulculture.item.CultureSpaceItem;
import com.kimjunhong.seoulculture.model.CultureSpace;
import com.kimjunhong.seoulculture.model.CultureSpaceBookmark;
import com.kimjunhong.seoulculture.model.CultureSpaceData;
import com.kimjunhong.seoulculture.util.RecyclerViewDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 10. 8..
 */

public class CultureSpaceBookmarkFragment extends Fragment {
    @BindView(R.id.recyclerView_cultureSpace_bookmark) RecyclerView recyclerView;
    @BindView(R.id.defaultLayout_cultureSpace_bookmark) FrameLayout defaultLayout;

    ArrayList<CultureSpaceItem> items = new ArrayList<>();
    Realm realm;

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
        // TODO: RecyclerView item sequence
        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(final Realm realm) {
                    RealmResults<CultureSpaceBookmark> bookmarks = CultureSpaceBookmark.findAll(realm);
                    final CultureSpaceItem[] item = new CultureSpaceItem[bookmarks.size()];

                    for(int i = 0; i < bookmarks.size(); i++) {
                        final int count = i;

                        CultureSpaceService service = CultureSpaceService.retrofit.create(CultureSpaceService.class);
                        Call<CultureSpaceData> call = service.getCultureSpace(1, 1, bookmarks.get(i).getSpaceId());
                        call.enqueue(new retrofit2.Callback<CultureSpaceData>() {
                            @Override
                            public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                                CultureSpace cultureSpace = response.body().getSearchCulturalFacilitiesDetailService().getRow().get(0);
                                item[count] = new CultureSpaceItem(cultureSpace.getFAC_CODE(),
                                                                   cultureSpace.getCODENAME(),
                                                                   cultureSpace.getFAC_NAME(),
                                                                   cultureSpace.getMAIN_IMG(),
                                                                   cultureSpace.getADDR(),
                                                                   cultureSpace.getENTRFREE(),
                                                                   cultureSpace.getETC_DESC());

                                items.add(item[count]);
                                initRecyclerView(items);
                            }

                            @Override
                            public void onFailure(Call<CultureSpaceData> call, Throwable t) {

                            }
                        });
                        Log.v("log", "Space bookmark ID: " + bookmarks.get(i).getSpaceId());
                    }
                }
            });
        } finally {
            realm.close();
        }
    }

    private void initRecyclerView(List<CultureSpaceItem> items) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new RecyclerViewAdapter(getActivity(), items));
        recyclerView.addItemDecoration(new RecyclerViewDecoration(15));
    }
}
