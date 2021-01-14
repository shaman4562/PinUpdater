package com.delta22.pinup.request;

import com.delta22.pinup.entry.Domain;
import io.reactivex.Single;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PostRequest {

    @POST(".")
    @FormUrlEncoded
    Single<Domain> requestDomenSingle(@Field("app") String appName,
                                      @Field("domain") String domainName);
}
