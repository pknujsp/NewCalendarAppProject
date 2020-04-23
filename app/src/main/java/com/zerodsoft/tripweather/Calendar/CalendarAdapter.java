package com.zerodsoft.tripweather.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.google.gson.internal.$Gson$Preconditions;
import com.zerodsoft.tripweather.DatePickerFragment;
import com.zerodsoft.tripweather.R;

import java.util.ArrayList;
import java.util.Map;

public class CalendarAdapter extends BaseAdapter
{
    ArrayList<Integer> calenderData;
    int thisMonthStartIdx, thisMonthEndIdx;
    LayoutInflater layoutInflater;
    Context context;
    View dialog;
    public static final int START_DATE = 0;
    public static final int END_DATE = 1;

    private OnDaySelectedListener onDaySelectedListener;

    public interface OnDaySelectedListener
    {
        void onDaySelected(String day);
    }


    public CalendarAdapter(Map<String, ArrayList<Integer>> calenderData, Context context, View dialog, DialogFragment dialogFragment)
    {
        this.calenderData = new ArrayList<>();

        this.dialog = dialog;
        setData(calenderData);
        this.context = context;
        this.layoutInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        this.onDaySelectedListener = (OnDaySelectedListener) dialogFragment;
    }

    @Override
    public int getCount()
    {
        return calenderData.size();
    }

    @Override
    public Object getItem(int i)
    {
        return calenderData.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            view = layoutInflater.inflate(R.layout.gridview_item, null);
        }
        Button text = (Button) view.findViewById(R.id.text_date);

        text.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (onDaySelectedListener != null)
                {
                    onDaySelectedListener.onDaySelected(text.getText().toString());
                }
            }
        });

        if (thisMonthStartIdx > 0 && i < thisMonthStartIdx)
        {
            text.setText(calenderData.get(i).toString());
            text.setTypeface(text.getTypeface(), Typeface.ITALIC);
            text.setClickable(false);
        } else if (thisMonthEndIdx > 0 && i > thisMonthEndIdx)
        {
            text.setText(calenderData.get(i).toString());
            text.setTypeface(text.getTypeface(), Typeface.ITALIC);
            text.setClickable(false);
        } else
        {
            text.setText(calenderData.get(i).toString());
            text.setTypeface(text.getTypeface(), Typeface.BOLD);
            text.setClickable(true);
        }

        return view;
    }

    public void changeData(Map<String, ArrayList<Integer>> calenderData)
    {
        setData(calenderData);
        notifyDataSetChanged();
    }

    public void setData(Map<String, ArrayList<Integer>> calenderData)
    {
        int index = 0;
        this.thisMonthEndIdx = 0;
        this.thisMonthStartIdx = 0;

        if (calenderData.get("previous_month") != null)
        {
            for (int date : (ArrayList<Integer>) calenderData.get("previous_month"))
            {
                this.calenderData.add(date);
                ++index;
            }

            thisMonthStartIdx = index;
        }

        if (calenderData.get("this_month") != null)
        {
            for (int date : (ArrayList<Integer>) calenderData.get("this_month"))
            {
                this.calenderData.add(date);
                ++index;
            }
        }

        if (calenderData.get("next_month") != null)
        {
            thisMonthEndIdx = index - 1;

            for (int date : (ArrayList<Integer>) calenderData.get("next_month"))
            {
                this.calenderData.add(date);
                ++index;
            }
        }
    }

    private void setDate(SelectedDate date, int type)
    {
        switch (type)
        {
            case CalendarAdapter.START_DATE:
                ((TextView) dialog.findViewById(R.id.textview_start)).setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
                break;
            case CalendarAdapter.END_DATE:
                ((TextView) dialog.findViewById(R.id.textview_end)).setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay());
                break;
        }
    }

}