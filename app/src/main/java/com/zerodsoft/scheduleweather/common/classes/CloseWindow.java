package com.zerodsoft.scheduleweather.common.classes;

import android.app.Activity;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;

import java.util.Timer;
import java.util.TimerTask;

public class CloseWindow {
	private long firstPressedTime = 0L;
	private final long DURATION = 2000L;
	private Toast toast;

	public void clicked(Activity activity) {
		if (firstPressedTime > 0L) {
			long secondPressedTime = System.currentTimeMillis();

			if (secondPressedTime - firstPressedTime < DURATION) {
                    /*
                    activity.moveTaskToBack(true); // 태스크를 백그라운드로 이동
                    activity.finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                    android.os.Process.killProcess(android.os.Process.myPid()); // 앱 프로세스 종료

                     */
				activity.finish();
			}
		} else {
			firstPressedTime = System.currentTimeMillis();
			if (toast == null) {
				toast = Toast.makeText(activity, R.string.message_request_double_click_for_close, Toast.LENGTH_SHORT);
				toast.setDuration((int) DURATION);
			}

			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					firstPressedTime = 0L;
				}
			}, DURATION);
			toast.show();
		}
	}

}
