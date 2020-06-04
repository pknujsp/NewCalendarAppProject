package com.zerodsoft.scheduleweather.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;

public class CustomDialog extends Dialog implements View.OnClickListener
{
    protected final Activity activity;
    private Button btnNo;
    private Button btnYes;
    private TextView textViewnotificationStr;
    private String notificationStr;
    protected int travelId;

    public CustomDialog(Activity activity, int travelId)
    {
        super(activity);
        this.activity = activity;
        this.travelId = travelId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_yes_dialog);

        btnNo = (Button) findViewById(R.id.btnNo);
        btnYes = (Button) findViewById(R.id.btnYes);
        textViewnotificationStr = (TextView) findViewById(R.id.notification_string);

        btnNo.setOnClickListener(this);
        btnYes.setOnClickListener(this);
        textViewnotificationStr.setText(notificationStr);
    }

    @Override
    public void onClick(View view)
    {

    }


    public CustomDialog setNotificationStr(String notificationStr)
    {
        this.notificationStr = notificationStr;
        return this;
    }

    public CustomDialog setTravelId(int travelId)
    {
        this.travelId = travelId;
        return this;
    }
}
