package com.kimjunhong.seoulculture.model;

import java.util.ArrayList;

/**
 * Created by INMA on 2017. 8. 6..
 */

public class CultureEventGenreData {
    private SearchPerformanceBySubjectService SearchPerformanceBySubjectService;

    public CultureEventGenreData.SearchPerformanceBySubjectService getSearchPerformanceBySubjectService() {
        return SearchPerformanceBySubjectService;
    }

    public class SearchPerformanceBySubjectService {
        int list_total_count;
        Result RESULT;
        ArrayList<CultureEventGenre> row;

        public int getList_total_count() {
            return list_total_count;
        }

        public Result getRESULT() {
            return RESULT;
        }

        public ArrayList<CultureEventGenre> getRow() {
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
