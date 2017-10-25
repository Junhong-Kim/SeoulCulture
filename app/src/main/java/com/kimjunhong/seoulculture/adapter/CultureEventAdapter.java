package com.kimjunhong.seoulculture.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.activity.CultureEventActivity;
import com.kimjunhong.seoulculture.item.CultureEventItem;
import com.kimjunhong.seoulculture.model.CultureEventBookmark;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by INMA on 2017. 10. 23..
 */

public class CultureEventAdapter extends RecyclerView.Adapter<CultureEventAdapter.ViewHolder> {
    private Context mContext;
    private List<CultureEventItem> eventItems;
    private Realm realm;

    public CultureEventAdapter(Context mContext, List<CultureEventItem> eventItems) {
        this.mContext = mContext;
        this.eventItems = eventItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_culture_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CultureEventItem eventItem = eventItems.get(position);

        // 레이아웃
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CultureEventActivity.class);
                intent.putExtra("id", eventItem.getCultCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        // 대표 이미지
        Glide.with(mContext)
                .load(eventItem.getMainImage().toLowerCase())
                .asBitmap()
                .placeholder(R.drawable.ic_seoul_symbol)
                .into(holder.mainImage);
        // 제목
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.title.setText(Html.fromHtml(eventItem.getTitle(), Html.FROM_HTML_MODE_LEGACY).toString());
        } else {
            holder.title.setText(Html.fromHtml(eventItem.getTitle()).toString());
        }
        // 무료 구분
        if(eventItem.getIsFree().equals("1")) {
            holder.isFree.setText("무료");
            holder.isFree.setBackgroundColor(ContextCompat.getColor(mContext, R.color.positive));
        } else {
            holder.isFree.setText("유료");
            holder.isFree.setBackgroundColor(ContextCompat.getColor(mContext, R.color.negative));
        }
        // 북마크
        if(eventItem.isBookmark()) {
            holder.bookmark.setImageResource(R.drawable.ic_heart_filled);
        } else {
            holder.bookmark.setImageResource(R.drawable.ic_heart);
        }
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realm = Realm.getDefaultInstance();
                try {
                    // 북마크 추가
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            CultureEventBookmark bookmark = new CultureEventBookmark();
                            bookmark.setEventId(eventItem.getCultCode());

                            CultureEventBookmark.create(realm, bookmark);
                            holder.bookmark.setImageResource(R.drawable.ic_heart_filled);

                            Toast.makeText(mContext, "북마크에 추가되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    // 북마크 삭제
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            CultureEventBookmark.delete(realm, eventItem.getCultCode());
                            holder.bookmark.setImageResource(R.drawable.ic_heart);

                            Toast.makeText(mContext, "북마크가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        });
        // 장르 이름
        holder.codeName.setText(eventItem.getCodeName());
        // 자치구
        holder.gCode.setText(eventItem.getgCode());
        // 주소
        holder.place.setText(eventItem.getPlace());
        // 시작일
        holder.strtDate.setText(eventItem.getStrtDate());
        // 종료일
        holder.endDate.setText(eventItem.getEndDate());
    }

    @Override
    public int getItemCount() {
        return eventItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cultureEvent_item_layout) LinearLayout layout;
        @BindView(R.id.cultureEvent_item_mainImage) ImageView mainImage;
        @BindView(R.id.cultureEvent_item_isFree)TextView isFree;
        @BindView(R.id.cultureEvent_item_codeName)TextView codeName;
        @BindView(R.id.cultureEvent_item_title)TextView title;
        @BindView(R.id.cultureEvent_item_gCode)TextView gCode;
        @BindView(R.id.cultureEvent_item_place)TextView place;
        @BindView(R.id.cultureEvent_item_strtDate)TextView strtDate;
        @BindView(R.id.cultureEvent_item_endDate)TextView endDate;
        @BindView(R.id.cultureEvent_item_bookmark)ImageView bookmark;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
