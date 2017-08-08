package com.kimjunhong.seoulculture;

import com.kimjunhong.seoulculture.model.CultureEventGenreData;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by INMA on 2017. 8. 6..
 */

public interface CultureEventGenreService {
    @GET("{START_INDEX}/{END_INDEX}/{SUBJCODE21}")
    Call<CultureEventGenreData> getCultureEventsWithGenre(@Path("START_INDEX") int start, @Path("END_INDEX") int end, @Path("SUBJCODE21") int code);

    Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://openAPI.seoul.go.kr:8088/75535366756a686b33374e615a437a/json/SearchPerformanceBySubjectService/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
}
