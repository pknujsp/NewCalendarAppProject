package com.zerodsoft.scheduleweather.activity.editevent.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.ITimeZone;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TimeZoneRecyclerViewAdapter extends RecyclerView.Adapter<TimeZoneRecyclerViewAdapter.TimeZoneViewHolder> implements Filterable
{
    private final SimpleDateFormat DATE_FORMAT;
    private Date DATE;
    private ITimeZone iTimeZone;

    private List<TimeZone> unFilteredList;
    private List<TimeZone> filteredList;

    public TimeZoneRecyclerViewAdapter(ITimeZone iTimeZone, List<TimeZone> TIMEZONE_LIST, Date date)
    {
        this.iTimeZone = iTimeZone;
        this.unFilteredList = TIMEZONE_LIST;
        this.filteredList = TIMEZONE_LIST;
        this.DATE = date;
        DATE_FORMAT = new SimpleDateFormat("HH:mm (z)", Locale.KOREAN);
    }

    @NonNull
    @Override
    public TimeZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new TimeZoneViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.timezone_itemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TimeZoneViewHolder holder, int position)
    {
        holder.onBind(position);
    }

    @Override
    public int getItemCount()
    {
        return filteredList.size();
    }

    @Override
    public Filter getFilter()
    {
        return new Filter()
        {
            @Override
            protected FilterResults performFiltering(CharSequence constraint)
            {
                String charString = constraint.toString();
                if (charString.isEmpty())
                {
                    filteredList = unFilteredList;
                } else
                {
                    List<TimeZone> filteringList = new ArrayList<>();
                    for (TimeZone timeZone : unFilteredList)
                    {
                        DATE_FORMAT.setTimeZone(timeZone);
                        if (timeZone.getDisplayName(Locale.KOREAN).toLowerCase().contains(charString.toLowerCase())
                                || timeZone.getID().toLowerCase().contains(charString.toLowerCase())
                                || DATE_FORMAT.format(DATE).toLowerCase().contains(charString.toLowerCase()))
                        {
                            filteringList.add(timeZone);
                        }
                    }
                    filteredList = filteringList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults)
            {
                filteredList = (ArrayList<TimeZone>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }


    class TimeZoneViewHolder extends RecyclerView.ViewHolder
    {
        private TextView timeZoneName;
        private TextView timeValue;
        private TextView areaName;

        public TimeZoneViewHolder(@NonNull View itemView)
        {
            super(itemView);
            timeZoneName = itemView.findViewById(R.id.timezone_name);
            timeValue = itemView.findViewById(R.id.time_value);
            areaName = itemView.findViewById(R.id.area_name);

            super.itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    iTimeZone.onSelectedTimeZone(filteredList.get(getAdapterPosition()));
                }
            });
        }

        public void onBind(int position)
        {
            TimeZone timeZone = filteredList.get(position);
            DATE_FORMAT.setTimeZone(timeZone);

            timeZoneName.setText(timeZone.getDisplayName(Locale.KOREAN));
            timeValue.setText(DATE_FORMAT.format(DATE));
            areaName.setText(timeZone.getID());
        }
    }
}
