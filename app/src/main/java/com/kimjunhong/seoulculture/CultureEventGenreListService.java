package com.kimjunhong.seoulculture;

import com.kimjunhong.seoulculture.model.CultureEventGenreListData;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by INMA on 2017. 8. 3..
 */

public interface CultureEventGenreListService {
    @GET("{START_INDEX}/{END_INDEX}")
    Call<CultureEventGenreListData> getCultureEventGenreList(@Path("START_INDEX") int start, @Path("END_INDEX") int end);

    Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://openAPI.seoul.go.kr:8088/65586e73786a686b3637436b4a6747/json/SearchConcertSubjectCatalogService/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
}
