package com.kimjunhong.seoulculture.model;

import java.util.ArrayList;

/**
 * Created by INMA on 2017. 10. 4..
 */

public class CultureEventSearchWithNameData {
    private SearchConcertNameService SearchConcertNameService;

    public CultureEventSearchWithNameData.SearchConcertNameService getSearchConcertNameService() {
        return SearchConcertNameService;
    }

    public class SearchConcertNameService {
        private int list_total_count;
        private Result RESULT;
        private ArrayList<CultureEventSearchWithName> row;

        public int getList_total_count() {
            return list_total_count;
        }

        public Result getRESULT() {
            return RESULT;
        }

        public ArrayList<CultureEventSearchWithName> getRow() {
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
