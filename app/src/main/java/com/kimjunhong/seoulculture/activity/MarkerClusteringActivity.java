package com.kimjunhong.seoulculture.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
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

/**
 * Created by INMA on 2017. 8. 10..
 */

public class MarkerClusteringActivity extends AppCompatActivity implements OnMapReadyCallback {
    @BindView(R.id.toolbar) Toolbar toolbar;
    ClusterManager<CultureSpaceMarkerItem> clusterManager;

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
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10));

        clusterManager = new ClusterManager<>(this, googleMap);
        googleMap.setOnCameraChangeListener(clusterManager);

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

                        for(int i = 0; i < size; i++) {
                            double lat = row.get(i).getX_COORD();
                            double lng = row.get(i).getY_COORD();

                            clusterManager.addItem(new CultureSpaceMarkerItem(row.get(i).getFAC_NAME(),
                                                                              row.get(i).getADDR(),
                                                                              new LatLng(lat, lng)));
                        }
                        // Refresh cluster
                        clusterManager.cluster();
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

    private void initToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
