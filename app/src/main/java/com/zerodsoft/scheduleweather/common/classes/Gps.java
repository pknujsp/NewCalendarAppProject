package com.zerodsoft.scheduleweather.common.classes;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;

import com.google.android.gms.location.LocationCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DataProcessingCallback;

import static androidx.core.content.ContextCompat.checkSelfPermission;

public class Gps {
	private LocationManager locationManager;
	private LocationListener locationListener;

	public void runGps(Activity activity, DataProcessingCallback<Location> callback, ActivityResultLauncher<Intent> requestOnGpsLauncher
			, ActivityResultLauncher<String> requestLocationPermissionLauncher) {
		//권한 확인
		Context context = activity.getApplicationContext();

		if (locationManager == null) {
			locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		}

		boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			if (isGpsEnabled) {
				locationListener = new LocationListener() {
					boolean isCompleted = false;

					@Override
					public void onLocationChanged(Location location) {
						if (!isCompleted) {
							isCompleted = true;
							callback.processResult(location);
							locationManager.removeUpdates(this);
						}
					}

					@Override
					public void onStatusChanged(String provider, int status, Bundle extras) {

					}

					@Override
					public void onProviderEnabled(String provider) {

					}

					@Override
					public void onProviderDisabled(String provider) {

					}
				};

				locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);

				if (isNetworkEnabled) {
					locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null);
				}
			} else {
				showRequestGpsDialog(activity, callback, requestOnGpsLauncher);
			}
		} else {
			requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
		}

	}

	private void showRequestGpsDialog(Activity activity, DataProcessingCallback<Location> callback
			, ActivityResultLauncher<Intent> requestOnGpsLauncher) {
		new MaterialAlertDialogBuilder(activity)
				.setMessage(activity.getString(R.string.request_to_make_gps_on))
				.setPositiveButton(activity.getString(R.string.check), new
						DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface paramDialogInterface, int paramInt) {
								requestOnGpsLauncher.launch(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
							}
						})
				.setNegativeButton(activity.getString(R.string.no), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						callback.onResultNoData();
					}
				})
				.setCancelable(false)
				.show();
	}

	public void removeUpdate() {
		if (locationListener != null) {
			locationManager.removeUpdates(locationListener);
		}
	}

}
