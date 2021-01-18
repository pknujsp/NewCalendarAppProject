package com.zerodsoft.scheduleweather.activity.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.zerodsoft.scheduleweather.R;

public class IntroActivity extends AppCompatActivity
{
    private Button btnSignin, btnNotSignin;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        btnSignin = (Button) findViewById(R.id.btn_signin);
        btnNotSignin = (Button) findViewById(R.id.btn_not_signin);

        btnSignin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });

        btnNotSignin.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(IntroActivity.this, AppMainActivity.class);
                startActivity(intent);
            }
        });
    }
}