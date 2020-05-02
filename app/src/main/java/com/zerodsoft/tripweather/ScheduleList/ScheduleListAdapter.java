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
import androidx.room.RoomDatabase;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.WeatherData.ForecastAreaData;
import com.zerodsoft.tripweather.WeatherData.WeatherData;

import java.io.Serializable;
import java.util.ArrayList;

public class ScheduleListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Serializable
{
    ScheduleTable scheduleTable;
    ArrayList<ForecastAreaData> nForecastData;
    private SparseIntArray viewTypeArr;
    public static final int HEADER = 0;
    public static final int CHILD = 1;

    public interface OnImageListener
    {
        void setSkyImage(String sky);
    }

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


    public ScheduleListAdapter(ScheduleTable scheduleTable, ArrayList<ForecastAreaData> nForecast)
    {
        this.scheduleTable = scheduleTable;
        this.nForecastData = nForecast;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;

        if (viewType == HEADER)
        {
            view = layoutInflater.inflate(R.layout.recyclerview_schedule_header, parent, false);
            return new HeaderViewHolder(view);
        } else
        {
            view = layoutInflater.inflate(R.layout.recycler_view_schedule_item, parent, false);
            return new ChildViewHolder(view);
        }
    }


    @Override
    public int getItemViewType(int position)
    {
        return scheduleTable.getViewType(position);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        final int viewType = scheduleTable.getViewType(position);

        if (viewType == HEADER)
        {
            onBindHeaderViewHolder(holder, position);
        } else
        {
            // CHILD
            onBindChildViewHolder(holder, position);
        }
    }

    private void onBindHeaderViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        String date = (String) scheduleTable.get(position);

        ((HeaderViewHolder) holder).textViewDate.setText(date);
    }

    private void onBindChildViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
    {
        ScheduleNode schedule = (ScheduleNode) scheduleTable.get(position);
        final String headerDate = scheduleTable.getHeaderDate(position);
        String morningSky = null, daySky = null, eveningSky = null;

        RootLoop:
        for (int headerIdx = 0; headerIdx < scheduleTable.getSize(); headerIdx++)
        {
            for (int nodeIdx = 0; nodeIdx < scheduleTable.getNodesCount(headerIdx); nodeIdx++)
            {
                ScheduleNode node = scheduleTable.getNode(headerIdx, nodeIdx);

                String areaX = node.getSchedule().getAreaX();
                String areaY = node.getSchedule().getAreaY();

                for (ForecastAreaData forecastArea : nForecastData)
                {
                    if (areaX.equals(forecastArea.getAreaX()) && areaY.equals(forecastArea.getAreaY()))
                    {
                        for (WeatherData weather : forecastArea.getForecastData())
                        {
                            try
                            {
                                if (headerDate.equals(weather.getFcstDate()))
                                {
                                    if (weather.getFcstTime().equals("0900"))
                                    {
                                        morningSky = weather.getSky();
                                    } else if (weather.getFcstTime().equals("1500"))
                                    {
                                        daySky = weather.getSky();
                                    } else if (weather.getFcstTime().equals("2100"))
                                    {
                                        eveningSky = weather.getSky();
                                    }
                                }
                            } catch (NullPointerException e)
                            {
                                break RootLoop;
                            }
                        }

                        break RootLoop;
                    }
                }
            }
        }

        ((ChildViewHolder) holder).textViewArea.setText(schedule.getSchedule().getAreaName());
        ((ChildViewHolder) holder).morningSky.setImageResource(getSkyImage(morningSky));
        ((ChildViewHolder) holder).daySky.setImageResource(getSkyImage(daySky));
        ((ChildViewHolder) holder).eveningSky.setImageResource(getSkyImage(eveningSky));
    }

    @Override
    public int getItemCount()
    {
        return scheduleTable.getSize();
    }

    private int getSkyImage(String sky)
    {
        int skyImageNumber = 0;

        if (sky == null)
        {
            skyImageNumber = R.drawable.past_date_icon;
        } else
        {
            switch (sky)
            {
                case "맑음":
                    skyImageNumber = R.drawable.fine_icon;
                    break;
                case "구름 많음":
                    skyImageNumber = R.drawable.partly_cloudy_icon;
                    break;
                case "흐림":
                    skyImageNumber = R.drawable.cloudy_icon;
                    break;
            }
        }
        return skyImageNumber;
    }

}
