package com.kimjunhong.seoulculture.model;

/**
 * Created by INMA on 2017. 8. 6..
 */

public class CultureEventGenre {
    int CULTCODE;
    int SUBJCODE;
    String CODENAME;
    String TITLE;
    String STRTDATE;
    String END_DATE;
    String PLACE;
    String MAIN_IMG;

    public int getCULTCODE() {
        return CULTCODE;
    }

    public int getSUBJCODE() {
        return SUBJCODE;
    }

    public String getCODENAME() {
        return CODENAME;
    }

    public String getTITLE() {
        return TITLE;
    }

    public String getSTRTDATE() {
        return STRTDATE;
    }

    public String getEND_DATE() {
        return END_DATE;
    }

    public String getPLACE() {
        return PLACE;
    }

    public String getMAIN_IMG() {
        return MAIN_IMG;
    }
}
