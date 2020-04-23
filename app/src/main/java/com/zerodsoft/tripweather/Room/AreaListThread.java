package com.zerodsoft.tripweather.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import com.zerodsoft.tripweather.AreaList.AreaListAdapter;

import com.zerodsoft.tripweather.AreaSelectionActivity;
import com.zerodsoft.tripweather.NewScheduleActivity;
import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.WeatherData.Phase1Tuple;

import java.util.List;

public class AreaListThread extends Thread
{
    private AreaDb areaDb;
    private Activity activity;
    private Context context;
    private String phase1 = null;
    private String phase2 = null;
    private String phase3 = null;
    private List<Phase1Tuple> phase1List = null;
    private List<Area> areaList = null;
    private Area area = null;

    public AreaListThread(Activity activity, Context context)
    {
        this.activity = activity;
        this.areaDb = AreaDb.getInstance(context);
        this.context = context;
    }

    public AreaListThread(Activity activity, Context context, String phase1)
    {
        this.activity = activity;
        this.areaDb = AreaDb.getInstance(context);
        this.context = context;
        this.phase1 = phase1;
    }

    public AreaListThread(Activity activity, Context context, String phase1, String phase2)
    {
        this.activity = activity;
        this.areaDb = AreaDb.getInstance(context);
        this.context = context;
        this.phase1 = phase1;
        this.phase2 = phase2;
    }

    public AreaListThread(Activity activity, Context context, String phase1, String phase2, String phase3)
    {
        this.activity = activity;
        this.areaDb = AreaDb.getInstance(context);
        this.context = context;
        this.phase1 = phase1;
        this.phase2 = phase2;
        this.phase3 = phase3;
    }

    @Override
    public void run()
    {
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        if (phase1 == null)
        {
            phase1List = areaDb.areaDao().getPhase1();
        } else if (phase2 == null)
        {
            areaList = areaDb.areaDao().getPhase2(phase1);
        } else if (phase3 == null)
        {
            areaList = areaDb.areaDao().getPhase3(phase1, phase2);
        } else
        {
            area = areaDb.areaDao().getXy(phase1, phase2, phase3);
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (area == null)
                {
                    AreaListAdapter adapter = new AreaListAdapter(phase1List, areaList, areaDb, activity, context);
                    AreaSelectionActivity.recyclerViewArea.setAdapter(adapter);
                } else
                {
                    Intent intent = new Intent(activity, NewScheduleActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("area", area);
                    intent.putExtras(bundle);

                    AreaListAdapter.phase1 = null;
                    AreaListAdapter.phase2 = null;
                    AreaListAdapter.phase3 = null;
                    AreaListAdapter.currentPhase = 1;
                    AreaSelectionActivity.textViewPhase1 = null;
                    AreaSelectionActivity.textViewPhase2 = null;

                    activity.setResult(Activity.RESULT_OK, intent);
                    activity.finish();
                }
            }
        });
    }

}