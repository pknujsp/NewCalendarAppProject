package com.zerodsoft.scheduleweather.fragment;

import android.annotation.SuppressLint;
import android.graphics.Point;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.databinding.FragmentNotificationBinding;
import com.zerodsoft.scheduleweather.etc.SelectedNotificationTime;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;


public class NotificationFragment extends DialogFragment
{
    public static final String TAG = "NotificationFragment";
    private static NotificationFragment notificationFragment = new NotificationFragment();
    private SelectedNotificationTime notification;
    private FragmentNotificationBinding fragmentBinding;

    private boolean buttonLongClick = false;

    public static NotificationFragment getInstance()
    {
        return notificationFragment;
    }

    private OnNotificationTimeListener onNotificationTimeListener;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            moveValue(msg.what);
            notification.setResultStr();
            fragmentBinding.setResult(notification.getResultStr());
            handler.sendEmptyMessageDelayed(msg.what, 50);
        }
    };

    public interface OnNotificationTimeListener
    {
        void onNotiTimeSelected(SelectedNotificationTime selectedNotificationTime);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        this.onNotificationTimeListener = (OnNotificationTimeListener) getActivity();
        super.onCreate(savedInstanceState);
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
        RadioListener radioListener = new RadioListener();
        fragmentBinding.notificationTypeRadioGroup.setOnCheckedChangeListener(radioListener);
        fragmentBinding.notificationDayRadio.setOnTouchListener(radioListener);
        fragmentBinding.notificationHourRadio.setOnTouchListener(radioListener);
        fragmentBinding.notificationMinuteRadio.setOnTouchListener(radioListener);
        fragmentBinding.notificationDisableRadio.setOnTouchListener(radioListener);

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
                onNotificationTimeListener.onNotiTimeSelected(notification);
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
            notification.setResultStr();
            fragmentBinding.setResult(notification.getResultStr());
        }
    };

    private void moveValue(int viewId)
    {
        switch (viewId)
        {
            case R.id.minus_noti_day_button:
                if (notification.getDay() != 0)
                {
                    notification.setDay(notification.getDay() - 1);
                    fragmentBinding.setDay(Integer.toString(notification.getDay()));
                }
                break;
            case R.id.plus_noti_day_button:
                notification.setDay(notification.getDay() + 1);
                fragmentBinding.setDay(Integer.toString(notification.getDay()));
                break;
            case R.id.minus_noti_hour_button:
                if (notification.getHour() != 0)
                {
                    notification.setHour(notification.getHour() - 1);
                    fragmentBinding.setHour(Integer.toString(notification.getHour()));
                }
                break;
            case R.id.plus_noti_hour_button:
                notification.setHour(notification.getHour() + 1);

                if (notification.getMainType() == ScheduleDTO.MAIN_DAY)
                {
                    if (notification.getHour() > 23)
                    {
                        notification.setHour(23);
                    }
                }
                fragmentBinding.setHour(Integer.toString(notification.getHour()));

                break;
            case R.id.minus_noti_minute_button:
                if (notification.getMinute() != 0)
                {
                    notification.setMinute(notification.getMinute() - 1);
                    fragmentBinding.setMinute(Integer.toString(notification.getMinute()));
                }
                break;
            case R.id.plus_noti_minute_button:
                notification.setMinute(notification.getMinute() + 1);

                if (notification.getMainType() == ScheduleDTO.MAIN_DAY || notification.getMainType() == ScheduleDTO.MAIN_HOUR)
                {
                    if (notification.getMinute() > 59)
                    {
                        notification.setMinute(59);
                    }
                }

                fragmentBinding.setMinute(Integer.toString(notification.getMinute()));
                break;
        }
    }

    private void checkValue(int viewId)
    {
        switch (viewId)
        {
            case R.id.notification_day_radio:
                if (notification.getHour() > 23)
                {
                    notification.setHour(23);
                }
                if (notification.getMinute() > 59)
                {
                    notification.setMinute(59);
                }
                break;

            case R.id.notification_hour_radio:
                if (notification.getMinute() > 59)
                {
                    notification.setMinute(59);
                }
                break;

            case R.id.notification_minute_radio:
                break;
            case R.id.notification_disable_radio:
                notification.setDay(0).setHour(0).setMinute(0);
                break;
        }
    }


    @Override
    public void onStart()
    {
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = point.x;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(layoutParams);

        switch (notification.getMainType())
        {
            case ScheduleDTO.MAIN_DAY:
                fragmentBinding.notificationDayRadio.performClick();
                break;
            case ScheduleDTO.MAIN_HOUR:
                fragmentBinding.notificationHourRadio.performClick();
                break;
            case ScheduleDTO.MAIN_MINUTE:
                fragmentBinding.notificationMinuteRadio.performClick();
                break;
            case ScheduleDTO.NOT_SELECTED:
                fragmentBinding.notificationDisableRadio.performClick();
                break;
        }
        super.onStart();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void setSelectedNotificationTime(SelectedNotificationTime selectedNotificationTime)
    {
        notification = selectedNotificationTime;
    }

    class RadioListener implements RadioGroup.OnCheckedChangeListener, View.OnTouchListener
    {
        boolean touched = false;

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            touched = true;
            return false;
        }

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int buttonId)
        {
            switch (buttonId)
            {
                case R.id.notification_day_radio:
                    // 일
                    notification.setMainType(ScheduleDTO.MAIN_DAY);
                    fragmentBinding.notiDayLayout.setVisibility(View.VISIBLE);
                    fragmentBinding.notiHourLayout.setVisibility(View.VISIBLE);
                    fragmentBinding.notiMinuteLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.notification_hour_radio:
                    // 시간
                    notification.setMainType(ScheduleDTO.MAIN_HOUR);
                    fragmentBinding.notiDayLayout.setVisibility(View.GONE);
                    fragmentBinding.notiHourLayout.setVisibility(View.VISIBLE);
                    fragmentBinding.notiMinuteLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.notification_minute_radio:
                    // 분
                    notification.setMainType(ScheduleDTO.MAIN_MINUTE);
                    fragmentBinding.notiDayLayout.setVisibility(View.GONE);
                    fragmentBinding.notiHourLayout.setVisibility(View.GONE);
                    fragmentBinding.notiMinuteLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.notification_disable_radio:
                    // 일
                    notification.setMainType(ScheduleDTO.NOT_SELECTED);
                    fragmentBinding.notiDayLayout.setVisibility(View.GONE);
                    fragmentBinding.notiHourLayout.setVisibility(View.GONE);
                    fragmentBinding.notiMinuteLayout.setVisibility(View.GONE);
                    break;
            }

            if (touched)
            {
                checkValue(buttonId);
                notification.setResultStr();
                touched = false;
            }

            fragmentBinding.setDay(Integer.toString(notification.getDay()));
            fragmentBinding.setHour(Integer.toString(notification.getHour()));
            fragmentBinding.setMinute(Integer.toString(notification.getMinute()));
            fragmentBinding.setResult(notification.getResultStr());
        }
    }
}