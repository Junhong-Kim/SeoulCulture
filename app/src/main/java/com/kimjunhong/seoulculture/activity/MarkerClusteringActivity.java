package com.kimjunhong.seoulculture.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kimjunhong.seoulculture.CultureSpaceService;
import com.kimjunhong.seoulculture.R;
import com.kimjunhong.seoulculture.item.CultureSpaceMarkerItem;
import com.kimjunhong.seoulculture.model.CultureSpace;
import com.kimjunhong.seoulculture.model.CultureSpaceData;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.kimjunhong.seoulculture.R.id.marker_title;

/**
 * Created by INMA on 2017. 8. 10..
 */

public class MarkerClusteringActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {
    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.marker_fac_info_layout) FrameLayout facInfoLayout;
    @BindView(R.id.marker_cultureSpace_item_mainImage) ImageView facMainImg;
    @BindView(R.id.marker_cultureSpace_item_isFree) TextView facIsFree;
    @BindView(R.id.marker_cultureSpace_item_codeName) TextView facCodeName;
    @BindView(R.id.marker_cultureSpace_item_bookmark) ImageView facBookmark;
    @BindView(R.id.marker_cultureSpace_item_facName) TextView facName;
    @BindView(R.id.marker_cultureSpace_item_addr) TextView facAddr;
    @BindView(R.id.marker_cultureSpace_item_etcDesc) TextView facEtcDesc;

    // ClusterManager<CultureSpaceMarkerItem> clusterManager;
    View customMarkerView;
    Marker selectedMarker;
    TextView markerTitle;
    String facCode;

    GoogleMap mMap;
    LatLng SEOUL = new LatLng(37.56, 126.97);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_clustering);
        ButterKnife.bind(this);

        initToolbar();

        FragmentManager fragmentManager = getFragmentManager();
        MapFragment mapFragment = (MapFragment)fragmentManager.findFragmentById(R.id.marker_clustering_map);
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
        // Set Google Map location
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15));
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);

        // clusterManager = new ClusterManager<>(this, mMap);
        // mMap.setOnCameraChangeListener(clusterManager);

        setCustomMarkerView();
        getMarkerItems();
    }

    private void setCustomMarkerView() {
        customMarkerView = LayoutInflater.from(this).inflate(R.layout.view_marker, null);
        markerTitle = (TextView) customMarkerView.findViewById(marker_title);
    }

    private void getMarkerItems() {
        final CultureSpaceService service = CultureSpaceService.retrofit.create(CultureSpaceService.class);
        Call<CultureSpaceData> call = service.getCultureSpaces(1, 1);
        call.enqueue(new Callback<CultureSpaceData>() {
            @Override
            public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                final int size = response.body().getSearchCulturalFacilitiesDetailService().getList_total_count();
                call = service.getCultureSpaces(1, size);
                call.enqueue(new Callback<CultureSpaceData>() {
                    @Override
                    public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                        ArrayList<CultureSpace> row = response.body().getSearchCulturalFacilitiesDetailService().getRow();

                        final ArrayList<CultureSpaceMarkerItem> markerList = new ArrayList<>();
                        for(int i = 0; i < size; i ++) {
                            // 문화공간 좌표
                            double lat = row.get(i).getX_COORD();
                            double lng = row.get(i).getY_COORD();

                            // clusterManager.addItem(new CultureSpaceMarkerItem(row.get(i).getFAC_NAME(),
                            //                                                  new LatLng(lat, lng)));

                            markerList.add(new CultureSpaceMarkerItem(row.get(i).getFAC_CODE(),
                                                                      row.get(i).getFAC_NAME(),
                                                                      new LatLng(lat, lng)));
                        }

                        for(CultureSpaceMarkerItem markerItem : markerList) {
                            addMarker(markerItem, false);
                        }

                        // Refresh cluster
                        // clusterManager.cluster();
                    }

                    @Override
                    public void onFailure(Call<CultureSpaceData> call, Throwable t) {

                    }
                });
            }

            @Override
            public void onFailure(Call<CultureSpaceData> call, Throwable t) {

            }
        });
    }

    private Marker addMarker(CultureSpaceMarkerItem markerItem, boolean isSelectedMarker) {
        LatLng position = markerItem.getLatLng();
        String facCode = markerItem.getFacCode();
        String facName = markerItem.getFacName();

        markerTitle.setText(facName);

        if (isSelectedMarker) {
            markerTitle.setBackgroundResource(R.drawable.ic_marker_box_fill);
            markerTitle.setTextColor(Color.WHITE);
        } else {
            markerTitle.setBackgroundResource(R.drawable.ic_marker_box);
            markerTitle.setTextColor(Color.BLACK);
        }

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(facName);
        markerOptions.snippet(facCode);
        markerOptions.position(position);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(this, customMarkerView)));

        return mMap.addMarker(markerOptions);
    }

    private Marker addMarker(Marker marker, boolean isSelectedMarker) {
        double lat = marker.getPosition().latitude;
        double lng = marker.getPosition().longitude;
        String title = marker.getTitle();

        CultureSpaceMarkerItem temp = new CultureSpaceMarkerItem(facCode, title, new LatLng(lat, lng));
        return addMarker(temp, isSelectedMarker);
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        mMap.animateCamera(center);
        changeSelectedMarker(marker);

        // marker snippet 를 통해 문화 공간 code 가져오기
        Log.v("log", "FAC Name: " + marker.getTitle() + ", " + "FAC Code: " + marker.getSnippet());
        facCode = marker.getSnippet();

        /* marker 정보 setting */
        facInfoLayout.setVisibility(View.VISIBLE);
        facInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CultureSpaceActivity.class);
                intent.putExtra("id", facCode);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        CultureSpaceService service = CultureSpaceService.retrofit.create(CultureSpaceService.class);
        Call<CultureSpaceData> call = service.getCultureSpace(1, 1, Integer.parseInt(facCode));
        call.enqueue(new Callback<CultureSpaceData>() {
            @Override
            public void onResponse(Call<CultureSpaceData> call, Response<CultureSpaceData> response) {
                CultureSpace cultureSpace = response.body().getSearchCulturalFacilitiesDetailService().getRow().get(0);
                // 대표 이미지
                Glide.with(getApplicationContext())
                        .load(cultureSpace.getMAIN_IMG())
                        .asBitmap()
                        .placeholder(R.drawable.ic_seoul_symbol)
                        .into(facMainImg);
                // 무료 구분
                if(cultureSpace.getENTRFREE().equals("1")) {
                    facIsFree.setText("유료");
                    facIsFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.negative));
                } else {
                    facIsFree.setText("무료");
                    facIsFree.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.positive));
                }
                // 장르명
                facCodeName.setText(cultureSpace.getCODENAME());
                // 북마크
                facBookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getApplicationContext(), "Bookmark", Toast.LENGTH_SHORT).show();
                    }
                });
                // 공간명
                facName.setText(cultureSpace.getFAC_NAME());
                // 주소
                facAddr.setText(cultureSpace.getADDR());
                // 내용
                if(cultureSpace.getFAC_DESC().equals("")) {
                    facEtcDesc.setVisibility(View.INVISIBLE);
                } else {
                    facEtcDesc.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        facEtcDesc.setText(Html.fromHtml(cultureSpace.getFAC_DESC(), Html.FROM_HTML_MODE_LEGACY).toString());
                    } else {
                        facEtcDesc.setText(Html.fromHtml(cultureSpace.getFAC_DESC()).toString());
                    }
                }
            }

            @Override
            public void onFailure(Call<CultureSpaceData> call, Throwable t) {

            }
        });

        return true;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // 맵을 클릭했을 때 선택된 marker 제거 & marker 정보 레이아웃 숨기기
        changeSelectedMarker(null);
        facInfoLayout.setVisibility(View.INVISIBLE);
    }

    private void changeSelectedMarker(Marker marker) {
        if(selectedMarker != null) {
            addMarker(selectedMarker, false);
            selectedMarker.remove();
        }

        if(marker != null) {
            selectedMarker = addMarker(marker, true);
            marker.remove();
        }
    }
}
