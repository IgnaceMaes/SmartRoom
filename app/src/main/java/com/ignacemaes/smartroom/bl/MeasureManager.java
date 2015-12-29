package com.ignacemaes.smartroom.bl;

import com.ignacemaes.smartroom.dal.MeasureService;
import com.ignacemaes.smartroom.model.PowerReply;
import com.ignacemaes.smartroom.model.RoomInfo;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Ignace on 8/12/2015.
 */
public class MeasureManager
{
    private MeasureService service;

    public MeasureManager()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.170")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(MeasureService.class);
    }

    public Call<RoomInfo> getRoomInfo()
    {
        return service.getRoomInfo();
    }

    public Call<PowerReply> power(int powerswitchIndex, boolean on)
    {
        String action = on ? "on" : "off";
        return service.power(String.valueOf(powerswitchIndex), action);
    }
}
