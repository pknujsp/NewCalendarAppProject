package com.zerodsoft.scheduleweather.Fragment;

import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.Activity.AddScheduleActivity;
import com.zerodsoft.scheduleweather.Etc.SelectedNotificationTime;
import com.zerodsoft.scheduleweather.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationFragment extends DialogFragment
{
    public static final String TAG = "NotificationFragment";

    private RelativeLayout mainLayout;
    private RelativeLayout hourLayout;
    private RelativeLayout minuteLayout;
    private Spinner notiSpinner;

    private ImageButton mainMinusButton;
    private ImageButton mainPlusButton;
    private ImageButton hourMinusButton;
    private ImageButton hourPlusButton;
    private ImageButton minuteMinusButton;
    private ImageButton minutePlusButton;

    private EditText mainValueEditText;
    private EditText hourValueEditText;
    private EditText minuteValueEditText;

    private TextView resultValueTextView;

    private Button cancelButton;
    private Button okButton;

    private int mainValue = 1;
    private int hourValue = 0;
    private int minuteValue = 0;

    private StringBuilder stringBuilder = new StringBuilder();

    private MainType mainType = MainType.DAY;

    public enum MainType
    {
        DAY, HOUR, MINUTE
    }

    private OnNotificationTimeListener onNotificationTimeListener;

    public interface OnNotificationTimeListener
    {
        void onNotiTimeSelected(SelectedNotificationTime selectedNotificationTime);
    }

    public NotificationFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        this.onNotificationTimeListener = (AddScheduleActivity) getActivity();
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
        mainLayout = (RelativeLayout) view.findViewById(R.id.noti_main_layout);
        hourLayout = (RelativeLayout) view.findViewById(R.id.noti_hour_layout);
        minuteLayout = (RelativeLayout) view.findViewById(R.id.noti_minute_layout);
        notiSpinner = (Spinner) view.findViewById(R.id.noti_spinner);

        mainMinusButton = (ImageButton) view.findViewById(R.id.minus_noti_value_button);
        mainPlusButton = (ImageButton) view.findViewById(R.id.plus_noti_value_button);
        hourMinusButton = (ImageButton) view.findViewById(R.id.minus_noti_hour_button);
        hourPlusButton = (ImageButton) view.findViewById(R.id.plus_noti_hour_button);
        minuteMinusButton = (ImageButton) view.findViewById(R.id.minus_noti_minute_button);
        minutePlusButton = (ImageButton) view.findViewById(R.id.plus_noti_minute_button);

        mainValueEditText = (EditText) view.findViewById(R.id.noti_main_value_edittext);
        hourValueEditText = (EditText) view.findViewById(R.id.noti_hour_value_edittext);
        minuteValueEditText = (EditText) view.findViewById(R.id.noti_minute_value_edittext);

        resultValueTextView = (TextView) view.findViewById(R.id.noti_result_value_textview);

        cancelButton = (Button) view.findViewById(R.id.noti_cancel_button);
        okButton = (Button) view.findViewById(R.id.noti_ok_button);

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
                SelectedNotificationTime selectedNotificationTime = new SelectedNotificationTime();
                selectedNotificationTime.setMainType(mainType).setResultStr(resultValueTextView.getText().toString());

                switch (mainType)
                {
                    case DAY:
                        selectedNotificationTime.setDay(mainValue);
                        selectedNotificationTime.setHour(hourValue);
                        selectedNotificationTime.setMinute(minuteValue);
                        break;
                    case MINUTE:
                        selectedNotificationTime.setMinute(mainValue);
                        break;
                    case HOUR:
                        selectedNotificationTime.setHour(mainValue);
                        selectedNotificationTime.setMinute(minuteValue);
                        break;
                }
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

        List<String> valueList = new ArrayList<>();
        valueList.add("일");
        valueList.add("시간");
        valueList.add("분");

        SpinnerAdapter spinnerAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, valueList);
        notiSpinner.setAdapter(spinnerAdapter);

        notiSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                if (hourValue != 0)
                {
                    hourValue = 0;
                    hourValueEditText.setText(Integer.toString(hourValue));
                }
                if (minuteValue != 0)
                {
                    minuteValue = 0;
                    minuteValueEditText.setText(Integer.toString(minuteValue));
                }

                switch (i)
                {
                    case 0:
                        // 일
                        mainType = MainType.DAY;
                        hourLayout.setVisibility(View.VISIBLE);
                        minuteLayout.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        // 시간
                        mainType = MainType.HOUR;
                        hourLayout.setVisibility(View.GONE);
                        minuteLayout.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        // 분
                        mainType = MainType.MINUTE;
                        hourLayout.setVisibility(View.GONE);
                        minuteLayout.setVisibility(View.GONE);
                        break;
                }
                showResultValue();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

        super.onViewCreated(view, savedInstanceState);
    }

    final View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            switch (view.getId())
            {
                case R.id.minus_noti_value_button:
                    if (mainValue != 0)
                    {
                        mainValueEditText.setText(Integer.toString(--mainValue));
                    }
                    break;
                case R.id.plus_noti_value_button:
                    mainValueEditText.setText(Integer.toString(++mainValue));
                    break;
                case R.id.minus_noti_hour_button:
                    if (hourValue != 0)
                    {
                        hourValueEditText.setText(Integer.toString(--hourValue));
                    }
                    break;
                case R.id.plus_noti_hour_button:
                    if (hourValue != 23)
                    {
                        hourValueEditText.setText(Integer.toString(++hourValue));
                    }
                    break;

                case R.id.minus_noti_minute_button:
                    if (minuteValue != 0)
                    {
                        minuteValueEditText.setText(Integer.toString(--minuteValue));
                    }
                    break;
                case R.id.plus_noti_minute_button:
                    if (minuteValue != 59)
                    {
                        minuteValueEditText.setText(Integer.toString(++minuteValue));
                    }
                    break;
            }
            showResultValue();
        }
    };

    private void showResultValue()
    {
        if (stringBuilder.length() != 0)
        {
            stringBuilder.delete(0, stringBuilder.length());
        }
        switch (mainType)
        {
            case DAY:
                stringBuilder.append(mainValueEditText.getText().toString()).append(" 일 ");
                stringBuilder.append(hourValueEditText.getText().toString()).append(" 시간 ");
                stringBuilder.append(minuteValueEditText.getText().toString()).append(" 분 ");
                break;
            case MINUTE:
                stringBuilder.append(mainValueEditText.getText().toString()).append(" 분 ");
                break;
            case HOUR:
                stringBuilder.append(mainValueEditText.getText().toString()).append(" 시간 ");
                stringBuilder.append(minuteValueEditText.getText().toString()).append(" 분 ");
                break;
        }
        stringBuilder.append(" 전에 알림");
        resultValueTextView.setText(stringBuilder.toString());
    }

    @Override
    public void onStart()
    {
        Point point = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getRealSize(point);
        int width = point.x;

        WindowManager.LayoutParams layoutParams = getDialog().getWindow().getAttributes();
        layoutParams.width = width;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        getDialog().getWindow().setAttributes(layoutParams);

        mainValueEditText.setText(Integer.toString(mainValue));
        hourValueEditText.setText(Integer.toString(hourValue));
        minuteValueEditText.setText(Integer.toString(minuteValue));

        hourLayout.setVisibility(View.GONE);
        minuteLayout.setVisibility(View.GONE);

        super.onStart();
    }
}