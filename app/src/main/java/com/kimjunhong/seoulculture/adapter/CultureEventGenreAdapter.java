package com.kimjunhong.seoulculture.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kimjunhong.seoulculture.CultureEventGenreService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.activity.MainActivity;
import com.kimjunhong.seoulculture.fragment.CultureEventWithGenreFragment;
import com.kimjunhong.seoulculture.item.CultureEventGenreItem;
import com.kimjunhong.seoulculture.model.CultureEventGenreData;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 8. 3..
 */

public class CultureEventGenreAdapter extends RecyclerView.Adapter<CultureEventGenreAdapter.ViewHolder> {
    private Context mContext;
    private List<CultureEventGenreItem> items;
    private int selectedPosition = -1;

    public CultureEventGenreAdapter(Context mContext, List<CultureEventGenreItem> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public CultureEventGenreAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_culture_event_genre, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CultureEventGenreAdapter.ViewHolder holder, final int position) {
        final CultureEventGenreItem item = items.get(position);

        // 선택 항목 강조
        if(selectedPosition == position) {
            holder.genre.setBackgroundResource(R.drawable.bg_primary_corner);
            holder.genre.setTextColor(ContextCompat.getColor(mContext, R.color.white));
        } else {
            holder.genre.setBackgroundResource(R.drawable.bg_white_corner);
            holder.genre.setTextColor(ContextCompat.getColor(mContext, R.color.black));
        }

        holder.genre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 선택 항목 강조
                selectedPosition = position;
                notifyDataSetChanged();

                // 이전 행사 목록 초기화
                CultureEventWithGenreFragment.eventItems.clear();

                switch (item.getGenre()) {
                    case "콘서트":
                        searchEventWithGenre(1);
                        break;
                    case "클래식":
                        searchEventWithGenre(2);
                        break;
                    case "뮤지컬/오페라":
                        searchEventWithGenre(3);
                        break;
                    case "연극":
                        searchEventWithGenre(5);
                        break;
                    case "무용":
                        searchEventWithGenre(6);
                        break;
                    case "전시/미술":
                        searchEventWithGenre(7);
                        break;
                    case "기타":
                        searchEventWithGenre(10);
                        break;
                    case "국악":
                        searchEventWithGenre(11);
                        break;
                    case "축제":
                        searchEventWithGenre(12);
                        break;
                    case "독주/독창회":
                        searchEventWithGenre(17);
                        break;
                    case "영화":
                        searchEventWithGenre(18);
                        break;
                    case "문화교양/강좌":
                        searchEventWithGenre(19);
                        break;
                }
            }
        });

        holder.genre.setText(item.getGenre());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    // 장르별 행사 가져오기
    private void searchEventWithGenre(final int genre) {
        CultureEventGenreService service = CultureEventGenreService.retrofit.create(CultureEventGenreService.class);
        Call<CultureEventGenreData> call = service.getCultureEventsWithGenre(1, 1, genre);
        call.enqueue(new Callback<CultureEventGenreData>() {
            @Override
            public void onResponse(Call<CultureEventGenreData> call, Response<CultureEventGenreData> response) {
                CultureEventWithGenreFragment genreFragment = (CultureEventWithGenreFragment) ((MainActivity)MainActivity.mContext).getSupportFragmentManager().findFragmentById(R.id.cultureEvent_container);
                genreFragment.initEvents(1, response.body().getSearchPerformanceBySubjectService().getList_total_count(), genre);
            }

            @Override
            public void onFailure(Call<CultureEventGenreData> call, Throwable t) {

            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cultureEvent_genre_item) TextView genre;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
