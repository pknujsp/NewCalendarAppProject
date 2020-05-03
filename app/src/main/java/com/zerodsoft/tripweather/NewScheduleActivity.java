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

import com.zerodsoft.tripweather.Room.DTO.Area;
import com.zerodsoft.tripweather.Utility.Clock;

import java.util.Date;

public class NewScheduleActivity extends AppCompatActivity implements DatePickerFragment.OnPositiveListener
{
    Toolbar toolbar;
    TextView textArea, textStartDate, textEndDate;
    ImageButton btnArea, btnDate;
    Area area = null;
    Date startDate = null, endDate = null;
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
            startDate = (Date) editIntent.getSerializableExtra("startDate");
            endDate = (Date) editIntent.getSerializableExtra("endDate");
            area = (Area) editIntent.getSerializableExtra("area");

            textArea.setText(area.getPhase1() + " " + area.getPhase2() + " " + area.getPhase3());
            textStartDate.setText(Clock.dateFormatSlash.format(startDate.getTime()));
            textEndDate.setText(Clock.dateFormatSlash.format(endDate.getTime()));
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
                if (area != null && startDate != null && endDate != null)
                {
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
                } else
                {
                    Toast.makeText(getApplicationContext(), "지역, 날짜를 선택해주세요", Toast.LENGTH_SHORT).show();
                }
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
    public void onPositiveSelected(Date startDate, Date endDate)
    {
        this.startDate = startDate;
        this.endDate = endDate;

        textStartDate.setText(Clock.dateDayNameFormatSlash.format(startDate.getTime()));
        textEndDate.setText(Clock.dateDayNameFormatSlash.format(endDate.getTime()));
    }

}
