package com.kimjunhong.seoulculture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;

import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.GridViewAdapter;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 8. 8..
 */

public class CultureEventWithDateFragment extends Fragment {
    @BindView(R.id.gridView_cultureEvent_with_date)
    GridView gridView;

    ArrayList<CultureEventItem> allItems = new ArrayList<>();
    private GridViewAdapter mAdapter;
    private boolean flag = false;
    private int startIndex = 1;
    private int endIndex = 6;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture_event_with_date, container, false);
        ButterKnife.bind(this, view);

        getCultureEvents(startIndex, endIndex);
        return view;
    }

    private void getCultureEvents(final int startIndex, int endIndex) {
        CultureEventService service = CultureEventService.retrofit.create(CultureEventService.class);
        Call<CultureEventData> call = service.getCultureEvents(startIndex, endIndex);
        call.enqueue(new Callback<CultureEventData>() {
            @Override
            public void onResponse(Call<CultureEventData> call, Response<CultureEventData> response) {
                ArrayList<CultureEventItem> items = new ArrayList<>();
                int itemSize = response.body().getSearchConcertDetailService().getRow().size();
                ArrayList<CultureEvent> row = response.body().getSearchConcertDetailService().getRow();

                CultureEventItem[] item = new CultureEventItem[itemSize];

                for(int i = 0; i < itemSize; i++) {
                    item[i] = new CultureEventItem(row.get(i).getCULTCODE(),
                                                   row.get(i).getMAIN_IMG(),
                                                   row.get(i).getIS_FREE(),
                                                   row.get(i).getCODENAME(),
                                                   row.get(i).getTITLE(),
                                                   row.get(i).getGCODE(),
                                                   row.get(i).getPLACE(),
                                                   row.get(i).getSTRTDATE(),
                                                   row.get(i).getEND_DATE());
                    items.add(item[i]);
                }
                allItems.addAll(items);

                if(startIndex == 1) {
                    initGridView(allItems);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CultureEventData> call, Throwable t) {

            }
        });
    }

    private void initGridView(ArrayList<CultureEventItem> items) {
        mAdapter = new GridViewAdapter(getActivity(), items);
        gridView.setAdapter(mAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && flag) {
                    startIndex = startIndex + 6;
                    endIndex = endIndex + 6;
                    getCultureEvents(startIndex, endIndex);

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                flag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount) >= totalItemCount;
            }
        });
    }
}
