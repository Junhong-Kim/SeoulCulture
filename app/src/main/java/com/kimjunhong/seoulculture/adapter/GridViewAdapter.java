package com.kimjunhong.seoulculture.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kimjunhong.seoulculture.activity.CultureEventActivity;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.item.GridCultureItem;

import java.util.List;

/**
 * Created by INMA on 2017. 7. 16..
 */

public class GridViewAdapter extends BaseAdapter {
    Context mContext;
    List<GridCultureItem> items;

    public GridViewAdapter(Context mContext, List<GridCultureItem> items) {
        this.mContext = mContext;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;
        final GridCultureItem item = items.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid_culture, null);

            holder = new ViewHolder();
            holder.layout = (LinearLayout) convertView.findViewById(R.id.gridView_item_layout);
            holder.mainImage = (ImageView) convertView.findViewById(R.id.gridView_item_mainImage);
            holder.isFree = (TextView) convertView.findViewById(R.id.gridView_item_isFree);
            holder.codeName = (TextView) convertView.findViewById(R.id.gridView_item_codeName);
            holder.title = (TextView) convertView.findViewById(R.id.gridView_item_title);
            holder.gCode = (TextView) convertView.findViewById(R.id.gridView_item_gCode);
            holder.place = (TextView) convertView.findViewById(R.id.gridView_item_place);
            holder.strtDate = (TextView) convertView.findViewById(R.id.gridView_item_strtDate);
            holder.endDate = (TextView) convertView.findViewById(R.id.gridView_item_endDate);
            holder.bookmark = (ImageView) convertView.findViewById(R.id.gridView_item_bookmark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CultureEventActivity.class);
                intent.putExtra("id", item.getCultCode());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        // 대표이미지
        Glide.with(mContext)
             .load(item.getMainImage().toLowerCase())
             .asBitmap()
             .placeholder(R.drawable.ic_seoul_symbol)
             .into(holder.mainImage);
        // 제목
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.title.setText(Html.fromHtml(item.getTitle(), Html.FROM_HTML_MODE_LEGACY).toString());
        } else {
            holder.title.setText(Html.fromHtml(item.getTitle()).toString());
        }
        // 무료구분
        if(item.getIsFree().equals("1")) {
            holder.isFree.setText("무료");
            holder.isFree.setBackgroundColor(ContextCompat.getColor(mContext, R.color.positive));
        } else {
            holder.isFree.setText("유료");
            holder.isFree.setBackgroundColor(ContextCompat.getColor(mContext, R.color.negative));
        }
        // 북마크
        holder.bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        holder.codeName.setText(item.getCodeName());
        holder.gCode.setText(item.getgCode());
        holder.place.setText(item.getPlace());
        holder.strtDate.setText(item.getStrtDate());
        holder.endDate.setText(item.getEndDate());

        return convertView;
    }

    public class ViewHolder {
        LinearLayout layout;
        ImageView mainImage;
        TextView isFree;
        TextView codeName;
        TextView title;
        TextView gCode;
        TextView place;
        TextView strtDate;
        TextView endDate;
        ImageView bookmark;
    }
}
