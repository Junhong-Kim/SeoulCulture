package com.kimjunhong.seoulculture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.CultureEventAdapter;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventBookmark;
import com.kimjunhong.seoulculture.model.CultureEventData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 8. 8..
 */

public class CultureEventWithDateFragment extends Fragment {
    @BindView(R.id.gridRecyclerView_cultureEvent_with_date) RecyclerView recyclerView;

    private ArrayList<CultureEventItem> eventItems = new ArrayList<>();
    private CultureEventAdapter mAdapter;

    private int startIndex = 1;
    private int endIndex = 6;
    private boolean isBookmark = false;
    private Realm realm;

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
                final ArrayList<CultureEvent> row = response.body().getSearchConcertDetailService().getRow();

                CultureEventItem[] item = new CultureEventItem[itemSize];

                for(int i = 0; i < itemSize; i++) {
                    try {
                        // 북마크 표시
                        final int pos = i;
                        realm = Realm.getDefaultInstance();
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                CultureEventBookmark eventBookmark = CultureEventBookmark.findOne(realm, row.get(pos).getCULTCODE());
                                isBookmark = true;
                                Log.v("log", "Event bookmark : " + eventBookmark.getEventId());
                            }
                        });
                    } catch (Exception e) {
                        isBookmark = false;
                    } finally {
                        item[i] = new CultureEventItem(row.get(i).getCULTCODE(),
                                                       row.get(i).getMAIN_IMG(),
                                                       row.get(i).getIS_FREE(),
                                                       row.get(i).getCODENAME(),
                                                       row.get(i).getTITLE(),
                                                       row.get(i).getGCODE(),
                                                       row.get(i).getPLACE(),
                                                       row.get(i).getSTRTDATE(),
                                                       row.get(i).getEND_DATE(),
                                                       isBookmark);
                        items.add(item[i]);
                    }
                }
                eventItems.addAll(items);

                if(startIndex == 1) {
                    initGridRecyclerView(eventItems);
                } else {
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<CultureEventData> call, Throwable t) {

            }
        });
    }

    private void initGridRecyclerView(ArrayList<CultureEventItem> eventItems) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new CultureEventAdapter(getActivity(), eventItems);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalItemCount = recyclerView.getAdapter().getItemCount() - 1;

                if(lastPosition == totalItemCount) {
                    startIndex = startIndex + 6;
                    endIndex = endIndex + 6;
                    getCultureEvents(startIndex, endIndex);

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
