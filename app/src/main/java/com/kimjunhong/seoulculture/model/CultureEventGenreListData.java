package com.kimjunhong.seoulculture.model;

import java.util.ArrayList;

/**
 * Created by INMA on 2017. 8. 3..
 */

public class CultureEventGenreListData {
    SearchConcertSubjectCatalogService SearchConcertSubjectCatalogService;

    public CultureEventGenreListData.SearchConcertSubjectCatalogService getSearchConcertSubjectCatalogService() {
        return SearchConcertSubjectCatalogService;
    }

    public class SearchConcertSubjectCatalogService {
        int list_total_count;
        Result RESULT;
        ArrayList<CultureEventGenreList> row;

        public int getList_total_count() {
            return list_total_count;
        }

        public Result getRESULT() {
            return RESULT;
        }

        public ArrayList<CultureEventGenreList> getRow() {
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
