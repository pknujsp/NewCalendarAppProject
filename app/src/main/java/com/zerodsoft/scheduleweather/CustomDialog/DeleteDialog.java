package com.zerodsoft.scheduleweather.CustomDialog;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.TravelScheduleThread;
import com.zerodsoft.scheduleweather.Utility.Actions;

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

