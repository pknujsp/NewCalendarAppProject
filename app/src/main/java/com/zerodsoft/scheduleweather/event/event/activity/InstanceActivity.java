package com.zerodsoft.scheduleweather.event.event.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.event.event.activity.fragment.EventFragment;

public class InstanceActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instance);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        EventFragment eventFragment = new EventFragment(this);
        eventFragment.setArguments(getIntent().getExtras());
        fragmentTransaction.add(R.id.instance_fragment_container, eventFragment, EventFragment.TAG).commit();
    }
}