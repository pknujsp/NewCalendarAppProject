package com.zerodsoft.scheduleweather.CustomDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.zerodsoft.scheduleweather.AddScheduleActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.TravelScheduleThread;
import com.zerodsoft.scheduleweather.Utility.Actions;

public class EditDialog extends CustomDialog implements View.OnClickListener
{
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            Bundle bundle = msg.getData();

            switch (msg.what)
            {
                case Actions.START_EDIT_SCHEDULE_ACTIVITY:
                    Intent editScheduleActivityIntent = new Intent(activity, AddScheduleActivity.class);
                    bundle.putInt("action", Actions.START_EDIT_SCHEDULE_ACTIVITY);
                    editScheduleActivityIntent.putExtras(bundle);
                    activity.startActivity(editScheduleActivityIntent);
                    dismiss();
                    break;
            }
        }
    };

    public EditDialog(Activity activity, int travelId)
    {
        super(activity, travelId);
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
                TravelScheduleThread travelScheduleThread = new TravelScheduleThread(activity);
                travelScheduleThread.setTravelId(travelId);
                travelScheduleThread.setAction(Actions.UPDATE_SCHEDULE);
                travelScheduleThread.setDialogHandler(handler);
                travelScheduleThread.start();
                break;
        }
    }
}
