package com.zerodsoft.scheduleweather.fragment;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.activity.ScheduleInfoActivity;
import com.zerodsoft.scheduleweather.etc.SelectedNotificationTime;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.room.dto.ScheduleDTO;


public class NotificationFragment extends DialogFragment
{
    public static final String TAG = "NotificationFragment";

    private static SelectedNotificationTime selectedNotificationTime;

    private RadioGroup typeRadioGroup;
    private RadioButton typeDayRadio;
    private RadioButton typeHourRadio;
    private RadioButton typeMinuteRadio;
    private RadioButton notNotiRadio;

    private RelativeLayout dayLayout;
    private RelativeLayout hourLayout;
    private RelativeLayout minuteLayout;

    private ImageButton mainMinusButton;
    private ImageButton mainPlusButton;
    private ImageButton hourMinusButton;
    private ImageButton hourPlusButton;
    private ImageButton minuteMinusButton;
    private ImageButton minutePlusButton;

    private TextView dayValueTextview;
    private TextView hourValueTextview;
    private TextView minuteValueTextview;
    private TextView resultValueTextView;

    private Button cancelButton;
    private Button okButton;

    private boolean restartedFragment = false;

    private static NotificationFragment notificationFragment = null;

    public static NotificationFragment getInstance()
    {
        if (notificationFragment == null)
        {
            notificationFragment = new NotificationFragment();
        }
        return notificationFragment;
    }

    private boolean buttonLongClick = false;

    private OnNotificationTimeListener onNotificationTimeListener;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            moveValue(msg.what);
            selectedNotificationTime.setResultStr();
            resultValueTextView.setText(selectedNotificationTime.getResultStr());
            handler.sendEmptyMessageDelayed(msg.what, 50);
        }
    };

    public interface OnNotificationTimeListener
    {
        void onNotiTimeSelected(SelectedNotificationTime selectedNotificationTime);
    }

    public NotificationFragment()
    {
        selectedNotificationTime = new SelectedNotificationTime();
        selectedNotificationTime.setMainType(ScheduleDTO.NOT_NOTI).setDay(1).setHour(0).setMinute(0).setResultStr();
    }

    public static void close()
    {
        selectedNotificationTime = null;
        notificationFragment = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        this.onNotificationTimeListener = (ScheduleInfoActivity) getActivity();
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        dayLayout = (RelativeLayout) view.findViewById(R.id.noti_day_layout);
        hourLayout = (RelativeLayout) view.findViewById(R.id.noti_hour_layout);
        minuteLayout = (RelativeLayout) view.findViewById(R.id.noti_minute_layout);

        typeRadioGroup = (RadioGroup) view.findViewById(R.id.notification_type_radio_group);

        typeDayRadio = (RadioButton) view.findViewById(R.id.notification_day_radio);
        typeHourRadio = (RadioButton) view.findViewById(R.id.notification_hour_radio);
        typeMinuteRadio = (RadioButton) view.findViewById(R.id.notification_minute_radio);
        notNotiRadio = (RadioButton) view.findViewById(R.id.notification_disable_radio);

        mainMinusButton = (ImageButton) view.findViewById(R.id.minus_noti_day_button);
        mainPlusButton = (ImageButton) view.findViewById(R.id.plus_noti_day_button);
        hourMinusButton = (ImageButton) view.findViewById(R.id.minus_noti_hour_button);
        hourPlusButton = (ImageButton) view.findViewById(R.id.plus_noti_hour_button);
        minuteMinusButton = (ImageButton) view.findViewById(R.id.minus_noti_minute_button);
        minutePlusButton = (ImageButton) view.findViewById(R.id.plus_noti_minute_button);

        dayValueTextview = (TextView) view.findViewById(R.id.noti_day_value_textview);
        hourValueTextview = (TextView) view.findViewById(R.id.noti_hour_value_textview);
        minuteValueTextview = (TextView) view.findViewById(R.id.noti_minute_value_textview);

        resultValueTextView = (TextView) view.findViewById(R.id.noti_result_value_textview);

        cancelButton = (Button) view.findViewById(R.id.noti_cancel_button);
        okButton = (Button) view.findViewById(R.id.noti_ok_button);

        typeRadioGroup.setOnCheckedChangeListener(onCheckedChangeListener);

        cancelButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                onNotificationTimeListener.onNotiTimeSelected(selectedNotificationTime);
                dismiss();
            }
        });

        mainMinusButton.setOnClickListener(onClickListener);
        mainPlusButton.setOnClickListener(onClickListener);
        hourMinusButton.setOnClickListener(onClickListener);
        hourPlusButton.setOnClickListener(onClickListener);
        minuteMinusButton.setOnClickListener(onClickListener);
        minutePlusButton.setOnClickListener(onClickListener);

        mainMinusButton.setOnLongClickListener(onLongClickListener);
        mainPlusButton.setOnLongClickListener(onLongClickListener);
        hourMinusButton.setOnLongClickListener(onLongClickListener);
        hourPlusButton.setOnLongClickListener(onLongClickListener);
        minuteMinusButton.setOnLongClickListener(onLongClickListener);
        minutePlusButton.setOnLongClickListener(onLongClickListener);

        mainMinusButton.setOnTouchListener(onTouchListener);
        mainPlusButton.setOnTouchListener(onTouchListener);
        hourMinusButton.setOnTouchListener(onTouchListener);
        hourPlusButton.setOnTouchListener(onTouchListener);
        minuteMinusButton.setOnTouchListener(onTouchListener);
        minutePlusButton.setOnTouchListener(onTouchListener);

        super.onViewCreated(view, savedInstanceState);
    }

    final RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int viewId)
        {
            switch (viewId)
            {
                case R.id.notification_day_radio:
                    // 일
                    selectedNotificationTime.setMainType(ScheduleDTO.MAIN_DAY);
                    dayLayout.setVisibility(View.VISIBLE);
                    hourLayout.setVisibility(View.VISIBLE);
                    minuteLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.notification_hour_radio:
                    // 시간
                    selectedNotificationTime.setMainType(ScheduleDTO.MAIN_HOUR);
                    dayLayout.setVisibility(View.GONE);
                    hourLayout.setVisibility(View.VISIBLE);
                    minuteLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.notification_minute_radio:
                    // 분
                    selectedNotificationTime.setMainType(ScheduleDTO.MAIN_MINUTE);
                    dayLayout.setVisibility(View.GONE);
                    hourLayout.setVisibility(View.GONE);
                    minuteLayout.setVisibility(View.VISIBLE);
                    break;
                case R.id.notification_disable_radio:
                    // 일
                    selectedNotificationTime.setMainType(ScheduleDTO.NOT_NOTI);
                    dayLayout.setVisibility(View.GONE);
                    hourLayout.setVisibility(View.GONE);
                    minuteLayout.setVisibility(View.GONE);
                    break;
            }

            if (!restartedFragment)
            {
                checkValue(viewId);
                selectedNotificationTime.setResultStr();
            }

            dayValueTextview.setText(Integer.toString(selectedNotificationTime.getDay()));
            hourValueTextview.setText(Integer.toString(selectedNotificationTime.getHour()));
            minuteValueTextview.setText(Integer.toString(selectedNotificationTime.getMinute()));
            resultValueTextView.setText(selectedNotificationTime.getResultStr());
        }
    };


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
            selectedNotificationTime.setResultStr();
            resultValueTextView.setText(selectedNotificationTime.getResultStr());
        }
    };

    private void moveValue(int viewId)
    {
        switch (viewId)
        {
            case R.id.minus_noti_day_button:
                if (selectedNotificationTime.getDay() != 0)
                {
                    selectedNotificationTime.setDay(selectedNotificationTime.getDay() - 1);
                    dayValueTextview.setText(Integer.toString(selectedNotificationTime.getDay()));
                }
                break;
            case R.id.plus_noti_day_button:
                selectedNotificationTime.setDay(selectedNotificationTime.getDay() + 1);
                dayValueTextview.setText(Integer.toString(selectedNotificationTime.getDay()));
                break;
            case R.id.minus_noti_hour_button:
                if (selectedNotificationTime.getHour() != 0)
                {
                    selectedNotificationTime.setHour(selectedNotificationTime.getHour() - 1);
                    hourValueTextview.setText(Integer.toString(selectedNotificationTime.getHour()));
                }
                break;
            case R.id.plus_noti_hour_button:
                selectedNotificationTime.setHour(selectedNotificationTime.getHour() + 1);

                if (selectedNotificationTime.getMainType() == ScheduleDTO.MAIN_DAY)
                {
                    if (selectedNotificationTime.getHour() > 23)
                    {
                        selectedNotificationTime.setHour(23);
                    }
                }
                hourValueTextview.setText(Integer.toString(selectedNotificationTime.getHour()));

                break;
            case R.id.minus_noti_minute_button:
                if (selectedNotificationTime.getMinute() != 0)
                {
                    selectedNotificationTime.setMinute(selectedNotificationTime.getMinute() - 1);
                    minuteValueTextview.setText(Integer.toString(selectedNotificationTime.getMinute()));
                }
                break;
            case R.id.plus_noti_minute_button:
                selectedNotificationTime.setMinute(selectedNotificationTime.getMinute() + 1);

                if (selectedNotificationTime.getMainType() == ScheduleDTO.MAIN_DAY || selectedNotificationTime.getMainType() == ScheduleDTO.MAIN_HOUR)
                {
                    if (selectedNotificationTime.getMinute() > 59)
                    {
                        selectedNotificationTime.setMinute(59);
                    }
                }

                minuteValueTextview.setText(Integer.toString(selectedNotificationTime.getMinute()));
                break;
        }
    }

    private void checkValue(int viewId)
    {
        switch (viewId)
        {
            case R.id.notification_day_radio:
                if (selectedNotificationTime.getHour() > 23)
                {
                    selectedNotificationTime.setHour(23);
                }
                if (selectedNotificationTime.getMinute() > 59)
                {
                    selectedNotificationTime.setMinute(59);
                }
                break;

            case R.id.notification_hour_radio:
                if (selectedNotificationTime.getMinute() > 59)
                {
                    selectedNotificationTime.setMinute(59);
                }
                break;

            case R.id.notification_minute_radio:
            case R.id.notification_disable_radio:
                break;
        }
    }


    @Override
    public void onStart()
    {
        super.onStart();

        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);
        int width = point.x;

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(layoutParams);

        switch (selectedNotificationTime.getMainType())
        {
            case ScheduleDTO.MAIN_DAY:
                typeDayRadio.performClick();
                break;
            case ScheduleDTO.MAIN_HOUR:
                typeHourRadio.performClick();
                break;
            case ScheduleDTO.MAIN_MINUTE:
                typeMinuteRadio.performClick();
                break;
            case ScheduleDTO.NOT_NOTI:
                notNotiRadio.performClick();
                break;
        }
        restartedFragment = false;
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    public void setSelectedNotificationTime(SelectedNotificationTime selectedNotificationTime)
    {
        this.selectedNotificationTime = selectedNotificationTime;
        restartedFragment = true;
    }
}