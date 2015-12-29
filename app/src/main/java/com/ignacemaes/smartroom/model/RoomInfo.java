package com.ignacemaes.smartroom.model;

import java.util.List;

/**
 * Created by Ignace on 8/12/2015.
 */
public class RoomInfo
{
    private double temperature;
    private int light;
    private boolean doorClosed;
    private List<Boolean> powerswitchStates;

    public double getTemperature()
    {
        return temperature;
    }

    public void setTemperature(double temperature)
    {
        this.temperature = temperature;
    }

    public int getLight()
    {
        return light;
    }

    public void setLight(int light)
    {
        this.light = light;
    }

    public boolean isDoorClosed()
    {
        return doorClosed;
    }

    public void setDoorClosed(boolean doorClosed)
    {
        this.doorClosed = doorClosed;
    }

    public List<Boolean> getPowerswitchStates()
    {
        return powerswitchStates;
    }

    public void setPowerswitchStates(List<Boolean> powerswitchStates)
    {
        this.powerswitchStates = powerswitchStates;
    }
}
