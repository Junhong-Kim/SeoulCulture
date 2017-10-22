package com.kimjunhong.seoulculture.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.fragment.CultureEventFragment;
import com.kimjunhong.seoulculture.fragment.CultureSpaceFragment;
import com.kimjunhong.seoulculture.fragment.MyCultureFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.fragment_container) FrameLayout container;
    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigationView;

    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = this;

        initToolbar();
        initBottomNavigationView();

        if(container != null) {
            if(savedInstanceState != null) {
                return;
            }

            CultureEventFragment fragment = new CultureEventFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_culture_event:
                startActivity(new Intent(this, CultureEventSearchActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    // 문화행사
                    case R.id.menu_culture_event:
                        fragmentTransaction.replace(R.id.fragment_container, new CultureEventFragment());
                        fragmentTransaction.commit();
                        return true;

                    // 문화공간
                    case R.id.menu_culture_space:
                        fragmentTransaction.replace(R.id.fragment_container, new CultureSpaceFragment());
                        fragmentTransaction.commit();
                        return true;

                    // MY문화
                    case R.id.menu_my_culture:
                        fragmentTransaction.replace(R.id.fragment_container, new MyCultureFragment());
                        fragmentTransaction.commit();
                        return true;

                    default:
                        return false;
                }
            }
        });
    }
}
