package com.kimjunhong.seoulculture.item;

/**
 * Created by INMA on 2017. 7. 25..
 */

public class CultureSpaceItem {
    String facCode;
    String codeName;
    String facName;
    String mainImg;
    String addr;
    String entrFree;
    String etcDesc;

    public CultureSpaceItem(String facCode, String codeName, String facName, String mainImg, String addr, String entrFree, String etcDesc) {
        this.facCode = facCode;
        this.codeName = codeName;
        this.facName = facName;
        this.mainImg = mainImg;
        this.addr = addr;
        this.entrFree = entrFree;
        this.etcDesc = etcDesc;
    }

    public String getFacCode() {
        return facCode;
    }

    public String getCodeName() {
        return codeName;
    }

    public String getFacName() {
        return facName;
    }

    public String getMainImg() {
        return mainImg;
    }

    public String getAddr() {
        return addr;
    }

    public String getEntrFree() {
        return entrFree;
    }

    public String getEtcDesc() {
        return etcDesc;
    }
}
