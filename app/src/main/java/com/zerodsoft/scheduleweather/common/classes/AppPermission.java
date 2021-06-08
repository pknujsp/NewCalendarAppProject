package com.zerodsoft.scheduleweather.common.classes;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class AppPermission {
	public static boolean grantedPermissions(Context context, String... permissions) {
		List<String> deniedList = new ArrayList<>();

		for (int i = 0; i < permissions.length; i++) {
			int result = ContextCompat.checkSelfPermission(context, permissions[i]);
			if (result == PackageManager.PERMISSION_DENIED) {
				deniedList.add(permissions[i]);
			}
		}

		if (deniedList.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
}
