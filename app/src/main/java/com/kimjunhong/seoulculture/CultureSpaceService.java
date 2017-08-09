package com.kimjunhong.seoulculture;

import com.kimjunhong.seoulculture.model.CultureSpaceData;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by INMA on 2017. 7. 25..
 */

public interface CultureSpaceService {
    @GET("{START_INDEX}/{END_INDEX}/")
    Call<CultureSpaceData> getCultureSpaces(@Path("START_INDEX") int start, @Path("END_INDEX") int end);

    @GET("{START_INDEX}/{END_INDEX}/{FAC_CODE}")
    Call<CultureSpaceData> getCultureSpace(@Path("START_INDEX") int start, @Path("END_INDEX") int end, @Path("FAC_CODE") int code);

    Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://openAPI.seoul.go.kr:8088/455a726a676a686b33396b4c6b6c56/json/SearchCulturalFacilitiesDetailService/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
}
