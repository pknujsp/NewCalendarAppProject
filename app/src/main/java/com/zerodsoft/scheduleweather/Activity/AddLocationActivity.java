package com.zerodsoft.scheduleweather.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.Room.DTO.LocationDTO;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.security.MessageDigest;

public class AddLocationActivity extends AppCompatActivity
{
    private TextView addressTextView;
    private ImageButton checkButton;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private ImageButton gpsButton;

    private LocationDTO locationDTO;
    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_location);

        addressTextView = (TextView) findViewById(R.id.address_textview);
        checkButton = (ImageButton) findViewById(R.id.add_location_check_button);
        zoomInButton = (ImageButton) findViewById(R.id.zoom_in_button);
        zoomOutButton = (ImageButton) findViewById(R.id.zoom_out_button);
        gpsButton = (ImageButton) findViewById(R.id.gps_button);

        mapView = new MapView(this);
        ConstraintLayout mapViewContainer = (ConstraintLayout) findViewById(R.id.add_location_map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);

        checkButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Bundle bundle = new Bundle();
                bundle.putSerializable("location", locationDTO);
                getIntent().putExtras(bundle);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        addressTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(AddLocationActivity.this, SearchAddressActivity.class);
                startActivity(intent);
            }
        });
    }


}