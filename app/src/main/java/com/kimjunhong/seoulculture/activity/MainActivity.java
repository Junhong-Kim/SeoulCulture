package com.kimjunhong.seoulculture.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.adapter.ViewPagerAdapter;
import com.kimjunhong.seoulculture.fragment.CultureEventFragment;
import com.kimjunhong.seoulculture.fragment.CultureSpaceFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigationView;
    @BindView(R.id.menu_culture_bookmark) ImageView bookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initToolbar();
        initViewPager();
        initBottomNavigationView();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                Toast.makeText(getApplicationContext(), "Search", Toast.LENGTH_SHORT).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
    }

    private void initViewPager() {
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new CultureEventFragment());
        fragments.add(new CultureSpaceFragment());

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch(position) {
                    case 0:
                        bottomNavigationView.setSelectedItemId(R.id.menu_culture_event);
                        viewPager.setCurrentItem(position);
                        break;
                    case 1:
                        bottomNavigationView.setSelectedItemId(R.id.menu_culture_space);
                        viewPager.setCurrentItem(position);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_culture_event:
                        viewPager.setCurrentItem(0);
                        return true;

                    case R.id.menu_culture_space:
                        viewPager.setCurrentItem(1);
                        return true;

                    default:
                        return false;
                }
            }
        });
    }

    private void initView() {
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Bookmark", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
