package com.zerodsoft.scheduleweather.Room;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;

import com.zerodsoft.scheduleweather.AreaList.AreaListAdapter;

import com.zerodsoft.scheduleweather.AreaSelectionActivity;
import com.zerodsoft.scheduleweather.Room.DTO.Area;
import com.zerodsoft.scheduleweather.WeatherData.Phase1Tuple;

import java.util.List;

public class AreaListThread extends Thread
{
    private AppDb appDb;
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
        this.appDb = AppDb.getInstance(context);
        this.context = context;
    }

    public AreaListThread(Activity activity, Context context, String phase1)
    {
        this.activity = activity;
        this.appDb = AppDb.getInstance(context);
        this.context = context;
        this.phase1 = phase1;
    }

    public AreaListThread(Activity activity, Context context, String phase1, String phase2)
    {
        this.activity = activity;
        this.appDb = AppDb.getInstance(context);
        this.context = context;
        this.phase1 = phase1;
        this.phase2 = phase2;
    }

    public AreaListThread(Activity activity, Context context, String phase1, String phase2, String phase3)
    {
        this.activity = activity;
        this.appDb = AppDb.getInstance(context);
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
            phase1List = appDb.areaDao().getPhase1();
        } else if (phase2 == null)
        {
            areaList = appDb.areaDao().getPhase2(phase1);
        } else if (phase3 == null)
        {
            if (phase2.equals("ALL"))
            {
                area = appDb.areaDao().getPhase2Xy(phase1, new String(""));
            } else
            {
                areaList = appDb.areaDao().getPhase3(phase1, phase2);
            }
        } else
        {
            if (phase3.equals("ALL"))
            {
                area = appDb.areaDao().getPhase3Xy(phase1, phase2, new String(""));
            } else
            {
                area = appDb.areaDao().getXy(phase1, phase2, phase3);
            }
        }

        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if (area == null)
                {
                    AreaListAdapter adapter = new AreaListAdapter(phase1List, areaList, appDb, activity, context);
                    AreaSelectionActivity.recyclerViewArea.setAdapter(adapter);
                } else
                {
                    Intent intent = activity.getIntent();
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