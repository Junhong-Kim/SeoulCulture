package com.kimjunhong.seoulculture;

import com.kimjunhong.seoulculture.model.Data;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by INMA on 2017. 7. 16..
 */

public interface CultureService {
    @GET("{START_INDEX}/{END_INDEX}/")
    Call<Data> getCultures(@Path("START_INDEX") int start, @Path("END_INDEX") int end);

    Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://openAPI.seoul.go.kr:8088/534b785a656a686b36316d74485745/json/SearchConcertDetailService/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
}
