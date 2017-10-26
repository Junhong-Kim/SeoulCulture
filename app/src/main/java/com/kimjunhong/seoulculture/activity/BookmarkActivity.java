package com.kimjunhong.seoulculture.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.fragment.CultureEventBookmarkFragment;
import com.kimjunhong.seoulculture.fragment.CultureSpaceBookmarkFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by INMA on 2017. 10. 7..
 */

public class BookmarkActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.toolbar_title) TextView toolbarTitle;
    @BindView(R.id.btn_cultureEvent_bookmark) TextView cultureEventBookmark;
    @BindView(R.id.btn_cultureSpace_bookmark) TextView cultureSpaceBookmark;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
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

        toolbarTitle.setText("북마크");
    }

    private void initView() {
        cultureEventBookmark.setBackgroundResource(R.drawable.bg_accent_top_right_corner);
        cultureEventBookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        cultureSpaceBookmark.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.sub));
        cultureSpaceBookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

        getSupportFragmentManager().beginTransaction().add(R.id.bookmark_container, new CultureEventBookmarkFragment()).commit();

        cultureEventBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cultureEventBookmark.setBackgroundResource(R.drawable.bg_accent_top_right_corner);
                cultureEventBookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                cultureSpaceBookmark.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.sub));
                cultureSpaceBookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.bookmark_container, new CultureEventBookmarkFragment());
                fragmentTransaction.commit();
            }
        });

        cultureSpaceBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cultureEventBookmark.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                cultureEventBookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                cultureSpaceBookmark.setBackgroundResource(R.drawable.bg_accent_top_left_corner);
                cultureSpaceBookmark.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.bookmark_container, new CultureSpaceBookmarkFragment());
                fragmentTransaction.commit();
            }
        });
    }
}
