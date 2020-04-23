package com.zerodsoft.tripweather.Utility;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GpsLocation extends Service implements LocationListener
{
    private final Context context;
    private final float MIN_DISTANCE = 10.0F;
    private final long MIN_TIME = 60000;
    private LocationManager locationManager;

    public GpsLocation(Context context)
    {
        this.context = context;
    }

    public String getCurrentAddress(double latitude, double longitude)
    {
        Geocoder geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());
        List<Address> addressList = null;

        try
        {
            addressList = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    5);
        } catch (IOException e)
        {
            Toast.makeText(context.getApplicationContext(), "ERROR : network", Toast.LENGTH_SHORT).show();
        } catch (IllegalArgumentException illegalArgumentException)
        {
            Toast.makeText(context.getApplicationContext(), "ERROR : latitude, longitude", Toast.LENGTH_SHORT).show();
        }

        if (addressList == null || addressList.size() == 0)
        {
            Toast.makeText(context.getApplicationContext(), "ERROR : failed to resolve address", Toast.LENGTH_SHORT).show();
        }
        Address address = addressList.get(0);
        return address.getAddressLine(0);
    }

    public Location getCurrentLocation()
    {
        Location location = null;

        try
        {
            locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

            boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isGPSEnabled && isNetworkEnabled)
            {
                int fineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
                int coarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);

                if (fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                        coarseLocationPermission == PackageManager.PERMISSION_GRANTED)
                {

                } else
                {
                    return null;
                }

                if (isNetworkEnabled)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);

                    if (locationManager != null)
                    {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    }
                }


                if (isGPSEnabled)
                {
                    if (location == null)
                    {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
                        if (locationManager != null)
                        {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        }
                    }
                }
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location)
    {
    }


    @Override
    public void onProviderDisabled(String provider)
    {
    }

    @Override
    public void onProviderEnabled(String provider)
    {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }


    public void stopGps()
    {
        if (locationManager != null)
        {
            locationManager.removeUpdates(GpsLocation.this);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }
}
