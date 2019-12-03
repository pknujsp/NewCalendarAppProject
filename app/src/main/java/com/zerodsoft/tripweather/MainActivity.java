package com.zerodsoft.tripweather;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    ImageView BackGround;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BackGround = (ImageView) findViewById(R.id.background_image);
        Glide.with(MainActivity.this).load(getDrawable(R.drawable.sunrise_pic)).into(BackGround);
    }

    public void moveWeatherPage(View v) {
        Intent intent = new Intent(MainActivity.this, current_weather.class);
        startActivity(intent);
    }
}
