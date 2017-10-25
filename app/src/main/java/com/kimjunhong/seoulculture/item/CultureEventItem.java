package com.kimjunhong.seoulculture.item;

/**
 * Created by INMA on 2017. 7. 16..
 */

public class CultureEventItem {
    int cultCode;
    String mainImage;
    String isFree;
    String codeName;
    String title;
    String gCode;
    String place;
    String strtDate;
    String endDate;
    boolean bookmark;

    public CultureEventItem(int cultCode, String mainImage, String isFree, String codeName, String title, String gCode, String place, String strtDate, String endDate, boolean bookmark) {
        this.cultCode = cultCode;
        this.mainImage = mainImage;
        this.isFree = isFree;
        this.codeName = codeName;
        this.title = title;
        this.gCode = gCode;
        this.place = place;
        this.strtDate = strtDate;
        this.endDate = endDate;
        this.bookmark = bookmark;
    }

    public int getCultCode() {
        return cultCode;
    }

    public String getMainImage() {
        return mainImage;
    }

    public String getIsFree() {
        return isFree;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getTitle() {
        return title;
    }

    public String getgCode() {
        return gCode;
    }

    public String getPlace() {
        return place;
    }

    public String getStrtDate() {
        return strtDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public boolean isBookmark() {
        return bookmark;
    }
}
