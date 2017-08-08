package com.kimjunhong.seoulculture.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridView;

import com.kimjunhong.seoulculture.CultureEventGenreListService;
import com.kimjunhong.seoulculture.CultureEventGenreService;
import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.CultureEventGenreAdapter;
import com.kimjunhong.seoulculture.adapter.GridViewAdapter;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.item.CultureEventGenreItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventData;
import com.kimjunhong.seoulculture.model.CultureEventGenre;
import com.kimjunhong.seoulculture.model.CultureEventGenreData;
import com.kimjunhong.seoulculture.model.CultureEventGenreList;
import com.kimjunhong.seoulculture.model.CultureEventGenreListData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 8. 8..
 */

public class CultureEventWithGenreFragment extends Fragment {
    @BindView(R.id.recyclerView_cultureEvent_with_genre) RecyclerView recyclerViewGenre;
    @BindView(R.id.gridView_cultureEvent_with_genre) GridView gridViewResult;
    @BindView(R.id.defaultLayout_cultureEvent_with_genre) FrameLayout defaultLayout;

    private ArrayList<CultureEventGenreItem> genreItems = new ArrayList<>();
    public static ArrayList<CultureEventItem> eventItems = new ArrayList<>();
    private GridViewAdapter eventAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_culture_event_with_genre, container, false);
        ButterKnife.bind(this, view);

        initGenres();
        return view;
    }

    // 장르 데이터
    private void initGenres() {
        CultureEventGenreListService service = CultureEventGenreListService.retrofit.create(CultureEventGenreListService.class);
        Call<CultureEventGenreListData> call = service.getCultureEventGenreList(1, 20);
        call.enqueue(new Callback<CultureEventGenreListData>() {
            @Override
            public void onResponse(Call<CultureEventGenreListData> call, Response<CultureEventGenreListData> response) {
                int size = response.body().getSearchConcertSubjectCatalogService().getList_total_count();
                CultureEventGenreItem[] item = new CultureEventGenreItem[size];

                // 장르 Row
                ArrayList<CultureEventGenreList> row = response.body().getSearchConcertSubjectCatalogService().getRow();

                for(int i = 0; i < size; i++) {
                    // 장르 Name
                    item[i] = new CultureEventGenreItem(row.get(i).getCODENAME());
                    genreItems.add(item[i]);
                }
                initRecyclerViewGenre(genreItems);
            }

            @Override
            public void onFailure(Call<CultureEventGenreListData> call, Throwable t) {

            }
        });
    }

    // 장르 RecyclerView 초기화
    private void initRecyclerViewGenre(ArrayList<CultureEventGenreItem> items) {
        recyclerViewGenre.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerViewGenre.setLayoutManager(lm);
        CultureEventGenreAdapter genreAdapter = new CultureEventGenreAdapter(getActivity(), items);
        recyclerViewGenre.setAdapter(genreAdapter);
    }

    // 행사 데이터
    public void initEvents(final int startIndex, final int endIndex, int genre) {
        /* 장르별 행사 개수 보다 많이 호출시 Error */
        defaultLayout.setVisibility(View.INVISIBLE);

        // 특정 장르의 행사 목록 가져오기
        CultureEventGenreService service = CultureEventGenreService.retrofit.create(CultureEventGenreService.class);
        Call<CultureEventGenreData> genreCall = service.getCultureEventsWithGenre(startIndex, endIndex, genre);
        genreCall.enqueue(new Callback<CultureEventGenreData>() {
            @Override
            public void onResponse(Call<CultureEventGenreData> call, Response<CultureEventGenreData> response) {
                // 특정 장르의 행사 목록 크기
                final int size = response.body().getSearchPerformanceBySubjectService().getRow().size();
                ArrayList<CultureEventGenre> row = response.body().getSearchPerformanceBySubjectService().getRow();
                CultureEventService cultureEventService = CultureEventService.retrofit.create(CultureEventService.class);

                // 특정 장르의 행사 목록 크기 만큼 루프를 반복하여(size), 해당 행사 정보 가져오기(getCultureEvent)
                for(int i = 0; i < size; i++) {
                    final int count = i;
                    final CultureEventItem[] item = new CultureEventItem[size];

                    Call<CultureEventData> eventCall = cultureEventService.getCultureEvent(1, 1, row.get(i).getCULTCODE());
                    eventCall.enqueue(new Callback<CultureEventData>() {
                        @Override
                        public void onResponse(Call<CultureEventData> call, Response<CultureEventData> response) {
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
                            eventItems.add(item[count]);
                            initGridViewEvents(eventItems);
                        }

                        @Override
                        public void onFailure(Call<CultureEventData> call, Throwable t) {

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CultureEventGenreData> call, Throwable t) {

            }
        });
    }

    // 행사 GridView 초기화
    public void initGridViewEvents(ArrayList<CultureEventItem> items) {
        eventAdapter = new GridViewAdapter(getActivity(), items);
        gridViewResult.setAdapter(eventAdapter);
    }
}
