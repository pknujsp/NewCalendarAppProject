package com.zerodsoft.tripweather.ScheduleList;

import android.content.Context;
import android.content.Intent;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.DTO.Schedule;

import java.util.List;

public class ScheduleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
{
    private List<Schedule> travelSchedules = null;
    private SparseIntArray viewTypeArr;
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    public class HeaderViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewDate;

        HeaderViewHolder(View itemView)
        {
            super(itemView);

            textViewDate = (TextView) itemView.findViewById(R.id.textview_schedule_header_date);
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewArea;
        ImageView morningSky, daySky, eveningSky;
        LinearLayout linearLayout;

        ChildViewHolder(View itemView)
        {
            super(itemView);

            textViewArea = (TextView) itemView.findViewById(R.id.text_view_travel_area);
            morningSky = (ImageView) itemView.findViewById(R.id.image_view_morning_sky);
            daySky = (ImageView) itemView.findViewById(R.id.image_view_day_sky);
            eveningSky = (ImageView) itemView.findViewById(R.id.image_view_evening_sky);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_schedule_item);
        }
    }


    public ScheduleListAdapter(List<Schedule> travelSchedules)
    {
        this.travelSchedules = travelSchedules;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_schedule_item, parent, false);
        //  ScheduleListAdapter.ViewHolder viewHolder = new ViewHolder(view);

        return null;
    }


    @Override
    public int getItemViewType(int position)
    {
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        String destination = travelSchedules.get(position).getAreaName();
        String date = travelSchedules.get(position).getDate();

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        };


    }

    @Override
    public int getItemCount()
    {
        this.viewTypeArr = new SparseIntArray();

        int count = 0;

        if (!travelSchedules.isEmpty())
        {
            viewTypeArr.clear();

            for (int i = 0; i < travelSchedules.size(); ++i)
            {
                viewTypeArr.put(count, HEADER);


            }
        }
        return count;
    }
}
