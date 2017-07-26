package com.kimjunhong.seoulculture.model;

import java.util.ArrayList;

/**
 * Created by INMA on 2017. 7. 25..
 */

public class CultureSpaceData {
    SearchCulturalFacilitiesDetailService SearchCulturalFacilitiesDetailService;

    public SearchCulturalFacilitiesDetailService getSearchCulturalFacilitiesDetailService() {
        return SearchCulturalFacilitiesDetailService;
    }

    public class SearchCulturalFacilitiesDetailService {
        private int list_total_count;
        private Result RESULT;
        private ArrayList<CultureSpace> row;

        public int getList_total_count() {
            return list_total_count;
        }

        public Result getRESULT() {
            return RESULT;
        }

        public ArrayList<CultureSpace> getRow() {
            return row;
        }

        public class Result {
            String CODE;
            String MESSAGE;

            public String getCODE() {
                return CODE;
            }

            public String getMESSAGE() {
                return MESSAGE;
            }
        }
    }
}
