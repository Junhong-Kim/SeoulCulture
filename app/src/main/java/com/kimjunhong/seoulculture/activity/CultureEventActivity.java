package com.kimjunhong.seoulculture.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kimjunhong.seoulculture.CultureService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.model.Culture;
import com.kimjunhong.seoulculture.model.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 7. 19..
 */

public class CultureEventActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.culture_event_main_img) ImageView mainImage;
    @BindView(R.id.culture_event_code_name) TextView codeName;
    @BindView(R.id.culture_event_title) TextView title;
    @BindView(R.id.culture_event_start_date) TextView startDate;
    @BindView(R.id.culture_event_end_date) TextView endDate;
    @BindView(R.id.culture_event_remain_date) TextView remainDate;
    @BindView(R.id.culture_event_place) TextView place;
    @BindView(R.id.culture_event_place_copy) LinearLayout copy;
    @BindView(R.id.culture_event_org_link) LinearLayout orgLink;
    @BindView(R.id.culture_event_inquiry) LinearLayout inquiry;
    @BindView(R.id.culture_event_is_free) TextView isFree;
    @BindView(R.id.culture_event_contents) TextView contents;
    @BindView(R.id.culture_event_gcode) TextView gCode;
    @BindView(R.id.culture_event_bookmark) ImageView bookmark;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_event);
        ButterKnife.bind(this);

        initToolbar();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        int id = getIntent().getIntExtra("id", 0);

        CultureService service = CultureService.retrofit.create(CultureService.class);
        Call<Data> call = service.getCulture(1, 1, id);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                final Culture culture = response.body().getSearchConcertDetailService().getRow().get(0);

                // 대표 이미지
                Glide.with(getApplicationContext())
                     .load(culture.getMAIN_IMG().toLowerCase())
                     .asBitmap()
                     .placeholder(R.drawable.ic_seoul_symbol)
                     .into(mainImage);
                // 제목
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    title.setText(Html.fromHtml(culture.getTITLE(), Html.FROM_HTML_MODE_LEGACY).toString());
                } else {
                    title.setText(Html.fromHtml(culture.getTITLE()).toString());
                }
                // 무료구분
                if(culture.getIS_FREE().equals("1")) {
                    isFree.setText("무료");
                    isFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.positive));
                } else {
                    isFree.setText("유료");
                    isFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.negative));
                }
                // 내용
                if(culture.getCONTENTS().equals("")) {
                    contents.setVisibility(View.INVISIBLE);
                } else {
                    contents.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        contents.setText(Html.fromHtml(culture.getCONTENTS(), Html.FROM_HTML_MODE_LEGACY).toString());
                    } else {
                        contents.setText(Html.fromHtml(culture.getCONTENTS()).toString());
                    }
                }
                // 문의
                inquiry.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + culture.getINQUIRY()));
                        startActivity(intent);
                    }
                });
                // 주소 복사
                copy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("address", culture.getPLACE());
                        clipboardManager.setPrimaryClip(clipData);

                        Toast.makeText(getApplicationContext(), "주소를 복사했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                // 원문 링크
                orgLink.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(culture.getORG_LINK()));
                        startActivity(intent);
                    }
                });
                // 날짜
                try {
                    Date today = new Date();
                    Date end = dateFormat.parse(culture.getEND_DATE());

                    long diff = end.getTime() - today.getTime();
                    long diffDays = diff / (24 * 60 * 60 * 1000);

                    if(diff < 0) {
                        remainDate.setText("오늘 종료");
                        Log.v("log", "날짜 차이 : " + diffDays + ", " + diff);
                    } else {
                        remainDate.setText((diffDays + 1) + "일 남음");
                        Log.v("log", "날짜 차이 : " + (diffDays + 1) + ", " + diff);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                // 북마크
                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                codeName.setText(culture.getCODENAME());
                startDate.setText(culture.getSTRTDATE());
                endDate.setText(culture.getEND_DATE());
                place.setText(culture.getPLACE());
                gCode.setText(culture.getGCODE());
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {

            }
        });
    }
}
