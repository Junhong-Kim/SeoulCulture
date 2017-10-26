package com.kimjunhong.seoulculture.fragment;

import android.os.AsyncTask;
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
import android.widget.FrameLayout;

import com.kimjunhong.seoulculture.CultureEventGenreListService;
import com.kimjunhong.seoulculture.CultureEventGenreService;
import com.kimjunhong.seoulculture.CultureEventService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.CultureEventAdapter;
import com.kimjunhong.seoulculture.adapter.CultureEventGenreAdapter;
import com.kimjunhong.seoulculture.item.CultureEventGenreItem;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEvent;
import com.kimjunhong.seoulculture.model.CultureEventBookmark;
import com.kimjunhong.seoulculture.model.CultureEventData;
import com.kimjunhong.seoulculture.model.CultureEventGenre;
import com.kimjunhong.seoulculture.model.CultureEventGenreData;
import com.kimjunhong.seoulculture.model.CultureEventGenreList;
import com.kimjunhong.seoulculture.model.CultureEventGenreListData;

import java.io.IOException;
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

public class CultureEventWithGenreFragment extends Fragment {
    @BindView(R.id.recyclerView_cultureEvent_with_genre) RecyclerView recyclerViewGenre;
    @BindView(R.id.gridRecyclerView_cultureEvent_with_genre) RecyclerView recyclerView;
    @BindView(R.id.defaultLayout_cultureEvent_with_genre) FrameLayout defaultLayout;
    @BindView(R.id.event_loading_indicator_view_layout) FrameLayout loadingLayout;

    private ArrayList<CultureEventGenreItem> genreItems = new ArrayList<>();
    public static ArrayList<CultureEventItem> eventItems = new ArrayList<>();
    private CultureEventAdapter mAdapter;

    private boolean isBookmark = false;
    private Realm realm;

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
        recyclerViewGenre.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewGenre.setAdapter(new CultureEventGenreAdapter(getActivity(), items));
    }

    // 행사 데이터
    public void getCultureEvents(final int startIndex, final int endIndex, final int genre) {
        /* 장르별 행사 개수 보다 많이 호출시 Error */
        defaultLayout.setVisibility(View.INVISIBLE);

        CultureEventGenreService genreService = CultureEventGenreService.retrofit.create(CultureEventGenreService.class);
        // 장르별 모든 행사 가져오기
        Call<CultureEventGenreData> genreCall = genreService.getCultureEventsWithGenre(startIndex, endIndex, genre);
        genreCall.enqueue(new Callback<CultureEventGenreData>() {
            @Override
            public void onResponse(Call<CultureEventGenreData> call, final Response<CultureEventGenreData> response) {
                // endIndex = 장르별 행사 개수, genreRow = 장르별 행사 정보
                final ArrayList<CultureEventGenre> genreRow = response.body().getSearchPerformanceBySubjectService().getRow();
                // 문화행사 상세 정보 가져오기 위한 작업
                final CultureEventService eventService = CultureEventService.retrofit.create(CultureEventService.class);
                // 장르별 행사 개수 만큼 반복하여 상세 정보 가져오기 (동기 처리)
                new AsyncTask<Void, Integer, ArrayList<CultureEventItem>>() {
                    @Override
                    protected ArrayList<CultureEventItem> doInBackground(Void... voids) {
                        CultureEventItem[] eventItem = new CultureEventItem[endIndex];
                        for (int i = 0; i < endIndex; i++) {
                            final int pos = i;
                            // Log.v("log", "Loop : " + pos);
                            try {
                                // 북마크 표시
                                realm = Realm.getDefaultInstance();
                                realm.executeTransaction(new Realm.Transaction() {
                                    @Override
                                    public void execute(Realm realm) {
                                        CultureEventBookmark eventBookmark = CultureEventBookmark.findOne(realm, genreRow.get(pos).getCULTCODE());
                                        isBookmark = true;
                                        Log.v("log", "Event bookmark : " + eventBookmark.getEventId());
                                    }
                                });
                            } catch (Exception e) {
                                // 북마크가 되어있지 않은 경우
                                isBookmark = false;
                            } finally {
                                // 문화행사 초기화
                                try {
                                    // 장르별 행사 정보의 코드로 상세 정보 가져오기
                                    Call<CultureEventData> eventCall = eventService.getCultureEvent(1, 1, genreRow.get(pos).getCULTCODE());
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
                                                                          isBookmark);
                                    // Log.v("log", "Sync : " + pos);
                                    eventItems.add(eventItem[pos]);
                                    // 동기처리 진행 상황 전달
                                    publishProgress((int)(((i + 1.0) / endIndex) * 100));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    realm.close();
                                }
                            }
                        }
                        return eventItems;
                    }

                    @Override
                    protected void onProgressUpdate(Integer... integers) {
                        super.onProgressUpdate(integers);
                        loadingLayout.setVisibility(View.VISIBLE);
                        Log.v("log", "Progress : " + integers[0] + "%");
                    }

                    @Override
                    protected void onPostExecute(ArrayList<CultureEventItem> eventItems) {
                        super.onPostExecute(eventItems);
                        initGridRecyclerView(eventItems);
                        loadingLayout.setVisibility(View.INVISIBLE);
                    }
                }.execute();
            }

            @Override
            public void onFailure(Call<CultureEventGenreData> call, Throwable t) {

            }
        });
    }

    private void initGridRecyclerView(ArrayList<CultureEventItem> eventItems) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new CultureEventAdapter(getActivity(), eventItems);
        recyclerView.setAdapter(mAdapter);
    }
}
