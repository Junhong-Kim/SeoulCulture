package com.kimjunhong.seoulculture.activity;

import android.app.FragmentManager;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kimjunhong.seoulculture.CultureSpaceService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.model.CultureSpace;
import com.kimjunhong.seoulculture.model.CultureSpaceData;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by INMA on 2017. 8. 8..
 */

public class CultureSpaceActivity extends AppCompatActivity implements OnMapReadyCallback{
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.cultureSpace_main_img) ImageView mainImg;
    @BindView(R.id.cultureSpace_is_free) TextView isFree;
    @BindView(R.id.cultureSpace_code_name) TextView codeName;
    @BindView(R.id.cultureSpace_bookmark) ImageView bookmark;
    @BindView(R.id.cultureSpace_fac_name) TextView facName;
    @BindView(R.id.cultureSpace_addr) TextView addr;
    @BindView(R.id.cultureSpace_phne) LinearLayout phne;
    @BindView(R.id.cultureSpace_addr_copy) LinearLayout addrCopy;
    @BindView(R.id.cultureSpace_homepage) LinearLayout homepage;
    @BindView(R.id.cultureSpace_contents) TextView contents;
    @BindView(R.id.cultureSpace_map_icon) ImageView mapIcon;
    @BindView(R.id.cultureSpace_map_addr) TextView mapAddr;
    @BindView(R.id.cultureSpace_map_layout) FrameLayout mapLayout;

    private int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_culture_space);
        ButterKnife.bind(this);

        initToolbar();
        initView();

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.cultureSpace_map);
        mapFragment.getMapAsync(this);
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

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        CultureSpaceService service = CultureSpaceService.retrofit.create(CultureSpaceService.class);
        Call<CultureSpaceData> call = service.getCultureSpace(1, 1, id);
        call.enqueue(new Callback<CultureSpaceData>() {
            @Override
            public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                CultureSpace cultureSpace = response.body().getSearchCulturalFacilitiesDetailService().getRow().get(0);

                LatLng address = new LatLng(cultureSpace.getX_COORD(), cultureSpace.getY_COORD());
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(address);
                // markerOptions.title(cultureSpace.getFAC_NAME());
                // markerOptions.snippet(cultureSpace.getADDR());
                // markerOptions.icon(BitmapDescriptorFactory.defaultMarker());

                googleMap.addMarker(markerOptions);
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(address));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                googleMap.getUiSettings().setScrollGesturesEnabled(false);
                googleMap.getUiSettings().setTiltGesturesEnabled(true);
            }

            @Override
            public void onFailure(Call<CultureSpaceData> call, Throwable t) {

            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        id = Integer.parseInt(getIntent().getStringExtra("id"));

        CultureSpaceService service = CultureSpaceService.retrofit.create(CultureSpaceService.class);
        Call<CultureSpaceData> call = service.getCultureSpace(1, 1, id);
        call.enqueue(new Callback<CultureSpaceData>() {
            @Override
            public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                final CultureSpace cultureSpace = response.body().getSearchCulturalFacilitiesDetailService().getRow().get(0);

                // 대표 이미지
                Glide.with(getApplicationContext())
                     .load(cultureSpace.getMAIN_IMG())
                     .asBitmap()
                     .placeholder(R.drawable.ic_seoul_symbol)
                     .into(mainImg);
                // 무료 구분
                if(cultureSpace.getENTR_FEE().equals("1")) {
                    isFree.setText("무료");
                    isFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.positive));
                } else {
                    isFree.setText("유료");
                    isFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.negative));
                }
                // 장르명
                codeName.setText(cultureSpace.getCODENAME());
                // 북마크
                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Bookmark", Toast.LENGTH_SHORT).show();
                    }
                });
                // 문화공간명
                facName.setText(cultureSpace.getFAC_NAME());
                // 주소
                addr.setText(cultureSpace.getADDR());
                // 문의하기
                phne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel: " + cultureSpace.getPHNE()));
                        startActivity(intent);
                    }
                });
                // 주소 복사
                addrCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                        ClipData clipData = ClipData.newPlainText("address", cultureSpace.getADDR());
                        clipboardManager.setPrimaryClip(clipData);

                        Toast.makeText(getApplicationContext(), "주소를 복사했습니다", Toast.LENGTH_SHORT).show();
                    }
                });
                // 홈페이지 링크
                homepage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(cultureSpace.getHOMEPAGE()));
                        startActivity(intent);
                    }
                });
                // 내용
                if(cultureSpace.getFAC_DESC().equals("")) {
                    contents.setVisibility(View.INVISIBLE);
                } else {
                    contents.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        contents.setText(Html.fromHtml(cultureSpace.getFAC_DESC(), Html.FROM_HTML_MODE_LEGACY).toString());
                    } else {
                        contents.setText(Html.fromHtml(cultureSpace.getFAC_DESC()).toString());
                    }
                }
                // 지도 아이콘
                mapIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                });
                // 지도 주소
                mapAddr.setText(cultureSpace.getADDR());
                // 지도 레이아웃
                mapLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getApplicationContext(), MapActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    }
                });
            }

            @Override
            public void onFailure(Call<CultureSpaceData> call, Throwable t) {

            }
        });
    }
}
