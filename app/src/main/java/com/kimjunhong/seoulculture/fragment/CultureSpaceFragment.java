package com.kimjunhong.seoulculture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimjunhong.seoulculture.CultureSpaceService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.CultureSpaceAdapter;
import com.kimjunhong.seoulculture.item.CultureSpaceItem;
import com.kimjunhong.seoulculture.model.CultureSpace;
import com.kimjunhong.seoulculture.model.CultureSpaceBookmark;
import com.kimjunhong.seoulculture.model.CultureSpaceData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 7. 24..
 */

public class CultureSpaceFragment extends Fragment {
    @BindView(R.id.recyclerView_cultureSpace) RecyclerView recyclerView;

    private ArrayList<CultureSpaceItem> spaceItems = new ArrayList<>();
    private CultureSpaceAdapter mAdapter;

    private int startIndex = 1;
    private int endIndex = 6;
    private boolean isBookmark = false;
    private Realm realm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture_space, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        getCultureSpaces(startIndex, endIndex);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_culture_space, menu);
    }

    private void getCultureSpaces(final int startIndex, int endIndex) {
        CultureSpaceService service = CultureSpaceService.retrofit.create(CultureSpaceService.class);
        Call<CultureSpaceData> call = service.getCultureSpaces(startIndex, endIndex);
        call.enqueue(new Callback<CultureSpaceData>() {
            @Override
            public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                ArrayList<CultureSpaceItem> items = new ArrayList<>();
                int itemSize = response.body().getSearchCulturalFacilitiesDetailService().getRow().size();
                final ArrayList<CultureSpace> row = response.body().getSearchCulturalFacilitiesDetailService().getRow();

                final CultureSpaceItem[] item = new CultureSpaceItem[itemSize];

                for(int i = 0; i < itemSize; i++) {
                    try {
                        // 북마크 표시
                        final int pos = i;
                        realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                CultureSpaceBookmark spaceBookmark = CultureSpaceBookmark.findOne(realm, Integer.parseInt(row.get(pos).getFAC_CODE()));
                                isBookmark = true;
                                Log.v("log", "Space bookmark : " + spaceBookmark.getSpaceId());
                            }
                        });
                    } catch (Exception e) {
                        isBookmark = false;
                    } finally {
                        item[i] = new CultureSpaceItem(row.get(i).getFAC_CODE(),
                                                       row.get(i).getCODENAME(),
                                                       row.get(i).getFAC_NAME(),
                                                       row.get(i).getMAIN_IMG(),
                                                       row.get(i).getADDR(),
                                                       row.get(i).getENTRFREE(),
                                                       row.get(i).getETC_DESC(),
                                                       isBookmark);
                        items.add(item[i]);
                    }
                }
                spaceItems.addAll(items);

                if(startIndex == 1) {
                    initRecyclerView(spaceItems);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CultureSpaceData> call, Throwable t) {

            }
        });
    }

    private void initRecyclerView(ArrayList<CultureSpaceItem> items) {
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(lm);
        mAdapter = new CultureSpaceAdapter(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalItemCount = recyclerView.getAdapter().getItemCount() - 1;

                if(lastPosition == totalItemCount) {
                    startIndex = startIndex + 6;
                    endIndex = endIndex + 6;
                    getCultureSpaces(startIndex, endIndex);

//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
            }
        });
    }
}
