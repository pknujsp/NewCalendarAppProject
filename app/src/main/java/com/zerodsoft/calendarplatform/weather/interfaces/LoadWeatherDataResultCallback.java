package com.zerodsoft.calendarplatform.weather.interfaces;

import androidx.annotation.Nullable;

import java.util.LinkedList;
import java.util.List;

public abstract class LoadWeatherDataResultCallback {
	int loadRequestCount = 0;
	int loadResponseCount = 0;
	List<Boolean> resultList = new LinkedList<>();
	List<Exception> exceptionList = new LinkedList<>();

	public final void onResult(boolean success, @Nullable Exception e) {
		++loadResponseCount;
		resultList.add(success);

		if (e != null) {
			exceptionList.add(e);
		}

		if (loadRequestCount == loadResponseCount) {
			for (Boolean result : resultList) {
				if (!result) {
					onFinalResult(false);
					clear();
					return;
				}
			}
			onFinalResult(true);
			clear();
		}
	}

	public abstract void onFinalResult(boolean allSucceed);

	public final void onLoadStarted() {
		if (loadRequestCount == 0) {
			resultList.clear();
			exceptionList.clear();
		}
		++loadRequestCount;
	}

	public final List<Exception> getExceptionList() {
		return exceptionList;
	}

	public final boolean isLoading() {
		return loadRequestCount > 0;
	}

	public final void clear() {
		loadRequestCount = 0;
		loadResponseCount = 0;
		resultList.clear();
		exceptionList.clear();
	}
}
