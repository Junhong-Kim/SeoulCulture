package com.kimjunhong.seoulculture;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;
import android.widget.GridView;

import com.kimjunhong.seoulculture.adapter.GridViewAdapter;
import com.kimjunhong.seoulculture.item.GridCultureItem;
import com.kimjunhong.seoulculture.model.Culture;
import com.kimjunhong.seoulculture.model.Data;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.gridView) GridView gridView;
    ArrayList<GridCultureItem> allItems = new ArrayList<>();
    private GridViewAdapter mAdapter;
    private boolean flag = false;
    private int startIndex = 1;
    private int endIndex = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getCultures(startIndex, endIndex);
    }

    private void getCultures(final int startIndex, int endIndex) {
        CultureService service = CultureService.retrofit.create(CultureService.class);
        Call<Data> call = service.getCultures(startIndex, endIndex);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                ArrayList<GridCultureItem> items = new ArrayList<>();
                int itemSize = response.body().getSearchConcertDetailService().getRow().size();
                ArrayList<Culture> row = response.body().getSearchConcertDetailService().getRow();

                GridCultureItem[] item = new GridCultureItem[itemSize];

                for(int i = 0; i < itemSize; i++) {
                    item[i] = new GridCultureItem(row.get(i).getCULTCODE(),
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
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }

    private void initGridView(ArrayList<GridCultureItem> items) {
        mAdapter = new GridViewAdapter(getApplicationContext(), items);
        gridView.setAdapter(mAdapter);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && flag) {
                    startIndex = startIndex + 6;
                    endIndex = endIndex + 6;
                    getCultures(startIndex, endIndex);

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
