package com.zerodsoft.tripweather;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zerodsoft.tripweather.Calendar.SelectedDate;
import com.zerodsoft.tripweather.Room.DTO.Area;

public class NewScheduleActivity extends AppCompatActivity implements DatePickerFragment.OnPositiveListener
{
    Toolbar toolbar;
    TextView textArea, textStartDate, textEndDate;
    ImageButton btnArea, btnDate;
    Area area;
    SelectedDate startDate, endDate;
    Intent editIntent = null;
    private static final int ADD_AREA = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_schedule);

        toolbar = (Toolbar) findViewById(R.id.toolBar_new_schedule);
        textArea = (TextView) findViewById(R.id.textview_selected_area);
        textStartDate = (TextView) findViewById(R.id.textview_start);
        textEndDate = (TextView) findViewById(R.id.textview_end);
        btnArea = (ImageButton) findViewById(R.id.btn_edit_area);
        btnDate = (ImageButton) findViewById(R.id.btn_edit_start);

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if (getIntent().getAction().equals("EDIT_SCHEDULE"))
        {
            editIntent = getIntent();
            startDate = (SelectedDate) editIntent.getSerializableExtra("startDate");
            endDate = (SelectedDate) editIntent.getSerializableExtra("endDate");
            area = (Area) editIntent.getSerializableExtra("area");

            textArea.setText(area.getPhase1() + " " + area.getPhase2() + " " + area.getPhase3());
            textStartDate.setText(startDate.getYear() + "/" + startDate.getMonth() + "/" + startDate.getDay());
            textEndDate.setText(endDate.getYear() + "/" + endDate.getMonth() + "/" + endDate.getDay());
        }

        btnArea.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(NewScheduleActivity.this, AreaSelectionActivity.class);
                startActivityForResult(intent, ADD_AREA);
            }
        });

        btnDate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), "TEST DIALOG");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.new_schedule_toolbar_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menu_check:
                if (editIntent != null)
                {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("startDate", startDate);
                    bundle.putSerializable("endDate", endDate);
                    bundle.putSerializable("area", area);
                    editIntent.putExtras(bundle);

                    setResult(RESULT_OK, editIntent);
                } else
                {
                    Intent intent = getIntent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("startDate", startDate);
                    bundle.putSerializable("endDate", endDate);
                    bundle.putSerializable("area", area);
                    intent.putExtras(bundle);

                    setResult(RESULT_OK, intent);
                }
                finish();
                return true;
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case ADD_AREA:
                    area = (Area) data.getSerializableExtra("area");
                    textArea.setText(area.getPhase1() + " " + area.getPhase2() + " " + area.getPhase3());
                    break;
            }
        }
    }

    @Override
    public void onPositiveSelected(SelectedDate startDate, SelectedDate endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;

        textStartDate.setText(startDate.getYear() + "/" + startDate.getMonth() + "/" + startDate.getDay());
        textEndDate.setText(endDate.getYear() + "/" + endDate.getMonth() + "/" + endDate.getDay());
    }

}
