package com.ignacemaes.smartroom.dal;

import com.ignacemaes.smartroom.model.PowerReply;
import com.ignacemaes.smartroom.model.RoomInfo;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by Ignace on 8/12/2015.
 */
public interface MeasureService
{
    @GET("/")
    Call<RoomInfo> getRoomInfo();

    @GET("/power")
    Call<PowerReply> power(@Query("id") String id, @Query("action") String action);
}
