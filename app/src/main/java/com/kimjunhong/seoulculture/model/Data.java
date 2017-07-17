package com.kimjunhong.seoulculture.model;

import java.util.ArrayList;

/**
 * Created by INMA on 2017. 7. 16..
 */

public class Data {
    private SearchConcertDetailService SearchConcertDetailService;

    public Data.SearchConcertDetailService getSearchConcertDetailService() {
        return SearchConcertDetailService;
    }

    public class SearchConcertDetailService {
        private int list_total_count;
        private Result RESULT;
        private ArrayList<Culture> row;

        public int getList_total_count() {
            return list_total_count;
        }

        public Result getRESULT() {
            return RESULT;
        }

        public ArrayList<Culture> getRow() {
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
