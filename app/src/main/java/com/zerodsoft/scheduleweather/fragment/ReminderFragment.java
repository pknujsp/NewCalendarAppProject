package com.zerodsoft.scheduleweather.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.zerodsoft.scheduleweather.AppMainActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentNotificationBinding;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;
import com.zerodsoft.scheduleweather.utility.ScheduleAlarm;


public class ReminderFragment extends DialogFragment
{
    public static final String TAG = "NotificationFragment";
    private static ReminderFragment instance;
    private FragmentNotificationBinding fragmentBinding;
    private OnNotificationTimeListener onNotificationTimeListener;

    private boolean buttonLongClick = false;

    private int day;
    private int hour;
    private int minute;

    public static ReminderFragment getInstance()
    {
        return instance;
    }

    public static ReminderFragment newInstance()
    {
        return instance;
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            moveValue(msg.what);
            handler.sendEmptyMessageDelayed(msg.what, 50);
        }
    };

    public interface OnNotificationTimeListener
    {
        public void onReminderSelected();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onNotificationTimeListener = (OnNotificationTimeListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        fragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        return fragmentBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        fragmentBinding.minusNotiDayButton.setOnClickListener(onClickListener);
        fragmentBinding.plusNotiDayButton.setOnClickListener(onClickListener);
        fragmentBinding.minusNotiHourButton.setOnClickListener(onClickListener);
        fragmentBinding.plusNotiHourButton.setOnClickListener(onClickListener);
        fragmentBinding.minusNotiMinuteButton.setOnClickListener(onClickListener);
        fragmentBinding.plusNotiMinuteButton.setOnClickListener(onClickListener);

        fragmentBinding.minusNotiDayButton.setOnLongClickListener(onLongClickListener);
        fragmentBinding.plusNotiDayButton.setOnLongClickListener(onLongClickListener);
        fragmentBinding.minusNotiHourButton.setOnLongClickListener(onLongClickListener);
        fragmentBinding.plusNotiHourButton.setOnLongClickListener(onLongClickListener);
        fragmentBinding.minusNotiMinuteButton.setOnLongClickListener(onLongClickListener);
        fragmentBinding.plusNotiMinuteButton.setOnLongClickListener(onLongClickListener);

        fragmentBinding.minusNotiDayButton.setOnTouchListener(onTouchListener);
        fragmentBinding.plusNotiDayButton.setOnTouchListener(onTouchListener);
        fragmentBinding.minusNotiHourButton.setOnTouchListener(onTouchListener);
        fragmentBinding.plusNotiHourButton.setOnTouchListener(onTouchListener);
        fragmentBinding.minusNotiMinuteButton.setOnTouchListener(onTouchListener);
        fragmentBinding.plusNotiMinuteButton.setOnTouchListener(onTouchListener);

        fragmentBinding.notiCancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        fragmentBinding.notiOkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ScheduleAlarm.init(day, hour, minute);
                onNotificationTimeListener.onReminderSelected();
                dismiss();
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


    final View.OnLongClickListener onLongClickListener = new View.OnLongClickListener()
    {
        @Override
        public boolean onLongClick(View view)
        {
            buttonLongClick = true;
            handler.sendEmptyMessageDelayed(view.getId(), 70);
            return true;
        }
    };

    final View.OnTouchListener onTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            Log.e(TAG, "ON TOUCH");

            if (buttonLongClick)
            {
                switch (motionEvent.getAction())
                {
                    case MotionEvent.ACTION_UP:
                        buttonLongClick = false;
                        handler.removeMessages(view.getId());
                        view.cancelLongPress();
                        break;
                }
                return false;
            }
            return false;
        }
    };

    final View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            moveValue(view.getId());
        }
    };

    private void moveValue(int viewId)
    {
        switch (viewId)
        {
            case R.id.minus_noti_day_button:
                if (day != 0)
                {
                    fragmentBinding.notiDay.setText(Integer.toString(--day));
                    ScheduleAlarm.setDAY(day);
                }
                break;
            case R.id.plus_noti_day_button:
                fragmentBinding.notiDay.setText(Integer.toString(++day));
                ScheduleAlarm.setDAY(day);
                break;
            case R.id.minus_noti_hour_button:
                if (hour != 0)
                {
                    fragmentBinding.notiHour.setText(Integer.toString(--hour));
                    ScheduleAlarm.setHOUR(hour);
                }
                break;
            case R.id.plus_noti_hour_button:
                hour++;

                if (hour > 23)
                {
                    hour = 23;
                }
                fragmentBinding.notiHour.setText(Integer.toString(hour));
                ScheduleAlarm.setHOUR(hour);
                break;
            case R.id.minus_noti_minute_button:
                if (minute != 0)
                {
                    fragmentBinding.notiMinute.setText(Integer.toString(--minute));
                    ScheduleAlarm.setMINUTE(minute);
                }
                break;
            case R.id.plus_noti_minute_button:
                minute++;

                if (minute > 59)
                {
                    minute = 59;
                }
                fragmentBinding.notiMinute.setText(Integer.toString(minute));
                ScheduleAlarm.setMINUTE(minute);
                break;
        }
        fragmentBinding.notiResult.setText(ScheduleAlarm.getResultText(getContext()));
    }


    @Override
    public void onStart()
    {
        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = (int) (AppMainActivity.getDisplayWidth() * 0.9);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(layoutParams);

        fragmentBinding.notiDay.setText(Integer.toString(day));
        fragmentBinding.notiHour.setText(Integer.toString(hour));
        fragmentBinding.notiMinute.setText(Integer.toString(minute));

        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void init(ScheduleDTO schedule)
    {
        ScheduleAlarm.init(schedule);
        day = ScheduleAlarm.getDAY();
        hour = ScheduleAlarm.getHOUR();
        minute = ScheduleAlarm.getMINUTE();
    }

}