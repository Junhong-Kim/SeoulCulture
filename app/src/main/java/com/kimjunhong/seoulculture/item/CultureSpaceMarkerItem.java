package com.kimjunhong.seoulculture.item;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by INMA on 2017. 8. 10..
 */

public class CultureSpaceMarkerItem implements ClusterItem {
    private String facCode;
    private String facName;
    private LatLng latLng;

    public CultureSpaceMarkerItem(String facCode, String facName, LatLng latLng) {
        this.facCode = facCode;
        this.facName = facName;
        this.latLng = latLng;
    }

    public String getFacCode() {
        return facCode;
    }

    public void setFacCode(String facCode) {
        this.facCode = facCode;
    }

    public String getFacName() {
        return facName;
    }

    public void setFacName(String facName) {
        this.facName = facName;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public LatLng getPosition() {
        return latLng;
    }
}
