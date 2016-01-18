package com.ignacemaes.smartroom.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Ignace on 29/12/2015.
 */
public class PowerReply
{
    @SerializedName("m")
    private String message;

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }
}
