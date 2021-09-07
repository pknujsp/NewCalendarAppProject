package com.zerodsoft.calendarplatform.common.classes;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class RetrofitCallListManager {
	protected volatile List<Call> callList = new ArrayList<>();

	public void add(Call call) {
		callList.add(call);
	}

	public Call get(Call call) {
		return callList.get(callList.indexOf(call));
	}

	public void remove(Call call) {
		callList.remove(call);
	}

	public void clear() {
		for (Call call : callList) {
			call.cancel();
		}
		callList.clear();
	}

	public boolean isEmpty() {
		return callList.isEmpty();
	}

	public interface CallManagerListener {
		void clear();
	}
}
