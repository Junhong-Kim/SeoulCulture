package com.kimjunhong.seoulculture.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.activity.CultureSpaceActivity;
import com.kimjunhong.seoulculture.item.CultureSpaceItem;
import com.kimjunhong.seoulculture.model.CultureSpaceBookmark;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

/**
 * Created by INMA on 2017. 7. 25..
 */

public class CultureSpaceAdapter extends RecyclerView.Adapter<CultureSpaceAdapter.ViewHolder> {
    private Context mContext;
    private List<CultureSpaceItem> items;
    private Realm realm;

    public CultureSpaceAdapter(Context mContext, List<CultureSpaceItem> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_culture_space, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CultureSpaceItem item = items.get(position);

        // 레이아웃
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CultureSpaceActivity.class);
                intent.putExtra("id", item.getFacCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        // 북마크
        if(item.isBookmark()) {
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
                            CultureSpaceBookmark bookmark = new CultureSpaceBookmark();
                            bookmark.setSpaceId(Integer.parseInt(item.getFacCode()));

                            CultureSpaceBookmark.create(realm, bookmark);
                            holder.bookmark.setImageResource(R.drawable.ic_heart_filled);

                            Toast.makeText(mContext, "북마크에 추가되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    // 북마크 삭제
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            CultureSpaceBookmark.delete(realm, Integer.parseInt(item.getFacCode()));
                            holder.bookmark.setImageResource(R.drawable.ic_heart);

                            Toast.makeText(mContext, "북마크가 삭제되었습니다", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    realm.close();
                }
            }
        });
        // 무료 구분
        if(item.getEntrFree().equals("1")) {
            holder.isFree.setText("유료");
            holder.isFree.setBackgroundColor(ContextCompat.getColor(mContext, R.color.negative));
        } else {
            holder.isFree.setText("무료");
            holder.isFree.setBackgroundColor(ContextCompat.getColor(mContext, R.color.positive));
        }
        // 대표 이미지
        Glide.with(mContext)
                .load(item.getMainImg())
                .asBitmap()
                .placeholder(R.drawable.ic_seoul_symbol)
                .into(holder.mainImage);
        // 장르 이름
        holder.codeName.setText(item.getCodeName());
        // 공간 이름
        holder.facName.setText(item.getFacName());
        // 주소
        holder.addr.setText(item.getAddr());
        // 기타 정보
        holder.etcDesc.setText(item.getEtcDesc());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cultureSpace_item_layout) LinearLayout layout;
        @BindView(R.id.cultureSpace_item_isFree) TextView isFree;
        @BindView(R.id.cultureSpace_item_mainImage) ImageView mainImage;
        @BindView(R.id.cultureSpace_item_codeName) TextView codeName;
        @BindView(R.id.cultureSpace_item_bookmark) ImageView bookmark;
        @BindView(R.id.cultureSpace_item_facName) TextView facName;
        @BindView(R.id.cultureSpace_item_addr) TextView addr;
        @BindView(R.id.cultureSpace_item_etcDesc) TextView etcDesc;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
