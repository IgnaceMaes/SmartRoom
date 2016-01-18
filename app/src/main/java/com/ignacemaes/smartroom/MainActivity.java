package com.ignacemaes.smartroom;

import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ignacemaes.smartroom.bl.MeasureManager;
import com.ignacemaes.smartroom.model.PowerReply;
import com.ignacemaes.smartroom.model.RoomInfo;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity
{
    private MeasureManager measureManager = new MeasureManager();

    @Bind(R.id.value_temperature) TextView temp;
    @Bind(R.id.value_light) TextView light;
    @Bind(R.id.value_door) TextView door;
    @Bind(R.id.value_mode) TextView mode;

    @Bind(R.id.swiperefreshlayout) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.main_cardcontainer) LinearLayout cardContainer;

    private RoomInfo roomInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        refreshRoomInfo();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {
                refreshRoomInfo();
            }
        });
    }

    private void initializeLayout()
    {
        cardContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (int i = 0; i < roomInfo.getPowerswitchStates().size(); i++)
        {
            final int finalI = i;
            final boolean currentOn = roomInfo.getPowerswitchStates().get(i);
            View powerX = inflater.inflate(R.layout.item_power, cardContainer, false);

            ((TextView) powerX.findViewById(R.id.title_power)).setText("Powerswitch " + i);
            ((TextView) powerX.findViewById(R.id.value_power)).setText("Current: " + (currentOn ? "on" : "off"));

            powerX.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    measureManager.power(finalI, !currentOn)
                            .enqueue(new Callback<PowerReply>()
                            {
                                @Override
                                public void onResponse(Response<PowerReply> response, Retrofit retrofit)
                                {
                                    Snackbar.make(temp, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                                    refreshRoomInfo();
                                }

                                @Override
                                public void onFailure(Throwable t)
                                {
                                    Snackbar.make(temp, "Failed to power switch", Snackbar.LENGTH_LONG).show();
                                }
                            });
                }
            });

            cardContainer.addView(powerX);
        }
    }

    private void refreshRoomInfo()
    {
        measureManager.getRoomInfo()
                .enqueue(new Callback<RoomInfo>()
                {
                    @Override
                    public void onResponse(Response<RoomInfo> response, Retrofit retrofit)
                    {
                        roomInfo = response.body();

                        setTempValue(roomInfo.getTemperature());
                        setLightValue(roomInfo.getLight());
                        setDoorValue(roomInfo.isDoorClosed());
                        setModeValue(roomInfo.isAutoswitch());

                        initializeLayout();

                        refreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onFailure(Throwable t)
                    {
                        Snackbar.make(temp, "Failed to load roominfo", Snackbar.LENGTH_LONG).show();

                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    @OnClick(R.id.card_mode) void onToggleMode()
    {
        measureManager.setAutoswitchMode(!roomInfo.isAutoswitch())
                .enqueue(new Callback<PowerReply>()
                {
                    @Override
                    public void onResponse(Response<PowerReply> response, Retrofit retrofit)
                    {
                        Snackbar.make(temp, response.body().getMessage(), Snackbar.LENGTH_LONG).show();
                        refreshRoomInfo();
                    }

                    @Override
                    public void onFailure(Throwable t)
                    {
                        Snackbar.make(temp, "Failed to toggle mode", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void setTempValue(double value)
    {
        temp.setText(String.format("Current: %.0f °C", value));
    }

    private void setLightValue(int value)
    {
        light.setText(String.format("Current: %d lux", value));
    }

    private void setDoorValue(boolean closed)
    {
        door.setText(String.format("Current: %s", closed ? "closed" : "open"));
    }

    private void setModeValue(boolean isInAutoswitchMode)
    {
        mode.setText(String.format("Current: %s", isInAutoswitchMode ? "on" : "off"));
    }
}
