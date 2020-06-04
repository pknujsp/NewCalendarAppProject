package com.zerodsoft.scheduleweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.AreaList.AreaListAdapter;
import com.zerodsoft.scheduleweather.Room.AreaListThread;
import com.zerodsoft.scheduleweather.R;

public class AreaSelectionActivity extends AppCompatActivity
{
    public static RecyclerView recyclerViewArea = null;
    public static TextView textViewPhase1;
    public static TextView textViewPhase2;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_selection);

        recyclerViewArea = (RecyclerView) findViewById(R.id.recycler_view_arealist);
        textViewPhase1 = (TextView) findViewById(R.id.textview_phase1);
        textViewPhase2 = (TextView) findViewById(R.id.textview_phase2);

        recyclerViewArea.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerViewArea.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayoutManager.VERTICAL));

        AreaListThread areaListThread = new AreaListThread(AreaSelectionActivity.this, getApplicationContext());
        areaListThread.start();

    }


    @Override
    public void onBackPressed()
    {
        int currentPhase = AreaListAdapter.currentPhase;
        AreaListThread areaListThread = null;

        if (currentPhase == 1)
        {
            super.onBackPressed();
        } else if (currentPhase == 2)
        {
            AreaListAdapter.phase1 = null;
            AreaListAdapter.currentPhase = 1;
            AreaSelectionActivity.textViewPhase1.setText("");
            areaListThread = new AreaListThread(AreaSelectionActivity.this, getApplicationContext(), AreaListAdapter.phase1);

            areaListThread.start();
        } else if (currentPhase == 3)
        {
            AreaListAdapter.phase2 = null;
            AreaListAdapter.currentPhase = 2;
            AreaSelectionActivity.textViewPhase2.setText("");
            areaListThread = new AreaListThread(AreaSelectionActivity.this, getApplicationContext(), AreaListAdapter.phase1, AreaListAdapter.phase2);

            areaListThread.start();
        }
    }
}
