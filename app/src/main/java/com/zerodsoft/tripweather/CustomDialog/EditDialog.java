package com.zerodsoft.tripweather.CustomDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zerodsoft.tripweather.AddScheduleActivity;
import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Utility.Actions;

public class EditDialog extends Dialog implements View.OnClickListener
{
    private Context context;
    private Button btnNo;
    private Button btnYes;
    private TextView notificationStr;
    private Bundle bundle;

    public EditDialog(Context context, Bundle bundle)
    {
        super(context);
        this.context = context;
        this.bundle = bundle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_yes_dialog);

        btnNo = (Button) findViewById(R.id.btnNo);
        btnYes = (Button) findViewById(R.id.btnYes);
        notificationStr = (TextView) findViewById(R.id.notification_string);

        btnNo.setOnClickListener(this);
        btnYes.setOnClickListener(this);
        notificationStr.setText("여행 일정을 수정하시겠습니까?");
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnNo:
                dismiss();
                break;
            case R.id.btnYes:
                Intent editScheduleActivityIntent = new Intent(context, AddScheduleActivity.class);
                bundle.putInt("action", Actions.UPDATE_SCHEDULE);
                editScheduleActivityIntent.putExtras(bundle);
                context.startActivity(editScheduleActivityIntent);
                dismiss();
                break;
        }
    }

    public EditDialog setBundle(Bundle bundle)
    {
        this.bundle = bundle;
        return this;
    }
}
