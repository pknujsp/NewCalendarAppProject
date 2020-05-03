package com.zerodsoft.tripweather.AreaList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.AreaSelectionActivity;
import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.AppDb;
import com.zerodsoft.tripweather.Room.AreaListThread;
import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.WeatherData.Phase1Tuple;

import java.util.List;

public class AreaListAdapter extends RecyclerView.Adapter<AreaListAdapter.AreaViewHolder>
{
    private List<Area> areaList;
    private List<Phase1Tuple> phase1Tuples;
    public static String phase1 = null;
    public static String phase2 = null;
    public static String phase3 = null;
    public static int currentPhase = 1;
    private AppDb appDb;
    private Context context;
    private Activity activity;


    public AreaListAdapter(List<Phase1Tuple> phase1Tuples, List<Area> areaList, AppDb appDb, Activity activity, Context context)
    {
        this.phase1Tuples = phase1Tuples;
        this.areaList = areaList;
        this.appDb = appDb;
        this.activity = activity;
        this.context = context;
    }

    public class AreaViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewAreaName;
        Context context;
        Activity activity;

        public AreaViewHolder(@NonNull View itemView, Context context, Activity activity)
        {
            super(itemView);

            textViewAreaName = (TextView) itemView.findViewById(R.id.textview_area_name);
            this.activity = activity;
            this.context = context;
        }
    }

    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_area_item, parent, false);

        return new AreaViewHolder(view, this.context, this.activity);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position)
    {
        String areaName = null;

        if (phase1 == null)
        {
            areaName = phase1Tuples.get(position).getPhase1();
        } else if (phase2 == null)
        {
            if (areaList.get(position).getPhase2().equals(""))
            {
                areaName = "ALL";
            } else
            {
                areaName = areaList.get(position).getPhase2();
            }
        } else if (phase3 == null)
        {
            if (areaList.get(position).getPhase3().equals(""))
            {
                areaName = "ALL";
            } else
            {
                areaName = areaList.get(position).getPhase3();
            }
        }

        holder.textViewAreaName.setText(areaName);
        holder.context = this.context;
        holder.activity = this.activity;

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                final String selectedArea = holder.textViewAreaName.getText().toString();
                AreaListThread areaListThread = null;

                if (phase1 == null)
                {
                    phase1 = selectedArea;
                    AreaSelectionActivity.textViewPhase1.setText(selectedArea);
                    currentPhase = 2;
                    areaListThread = new AreaListThread(holder.activity, holder.context, phase1);
                } else if (phase2 == null)
                {
                    phase2 = selectedArea;
                    AreaSelectionActivity.textViewPhase2.setText(selectedArea);
                    currentPhase = 3;
                    areaListThread = new AreaListThread(holder.activity, holder.context, phase1, phase2);
                } else if (phase3 == null)
                {
                    phase3 = selectedArea;
                    areaListThread = new AreaListThread(holder.activity, holder.context, phase1, phase2, phase3);
                }

                areaListThread.start();
            }
        };
        holder.textViewAreaName.setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount()
    {
        if (phase1Tuples == null)
        {
            return areaList.size();
        } else
        {
            return phase1Tuples.size();
        }
    }
}
