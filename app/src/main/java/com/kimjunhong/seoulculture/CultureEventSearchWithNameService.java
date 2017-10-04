package com.kimjunhong.seoulculture;

import com.kimjunhong.seoulculture.model.CultureEventSearchWithNameData;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by INMA on 2017. 10. 4..
 */

public interface CultureEventSearchWithNameService {
    @GET("{START_INDEX}/{END_INDEX}/{S_TITLE}")
    Call<CultureEventSearchWithNameData> getCultureEvents(@Path("START_INDEX") int start, @Path("END_INDEX") int end, @Path("S_TITLE") String title);

    Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://openAPI.seoul.go.kr:8088/4a746346706a686b383573436c765a/json/SearchConcertNameService/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
}
