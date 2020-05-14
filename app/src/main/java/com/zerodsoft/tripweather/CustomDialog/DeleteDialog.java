package com.zerodsoft.tripweather.CustomDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.zerodsoft.tripweather.AddScheduleActivity;
import com.zerodsoft.tripweather.MainActivity;
import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.TravelScheduleThread;
import com.zerodsoft.tripweather.Utility.Actions;

public class DeleteDialog extends CustomDialog implements View.OnClickListener
{
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Actions.FINISHED_DELETE_TRAVEL:
                    Toast.makeText(activity, "삭제 완료", Toast.LENGTH_SHORT).show();
                    TravelScheduleThread travelScheduleThread = new TravelScheduleThread(activity);
                    travelScheduleThread.setAction(Actions.SET_MAINACTIVITY_VIEW);
                    travelScheduleThread.setMainActivityHandler(mainActivityHandler);
                    travelScheduleThread.start();
                    dismiss();
                    break;
            }
        }
    };
    private Handler mainActivityHandler;

    public DeleteDialog(Activity activity, int travelId)
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
                travelScheduleThread.setAction(Actions.DELETE_TRAVEL);
                travelScheduleThread.setDialogHandler(handler);
                travelScheduleThread.start();
                break;
        }
    }

    public DeleteDialog setMainActivityHandler(Handler mainActivityHandler)
    {
        this.mainActivityHandler = mainActivityHandler;
        return this;
    }
}

