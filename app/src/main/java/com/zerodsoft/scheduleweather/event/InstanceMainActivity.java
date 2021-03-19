package com.zerodsoft.scheduleweather.event;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.databinding.InstanceMainActivityBinding;
import com.zerodsoft.scheduleweather.event.util.EventUtil;

import java.util.Calendar;

public class InstanceMainActivity extends AppCompatActivity
{
    private InstanceMainActivityBinding binding;
    private CalendarViewModel calendarViewModel;

    private Long instanceId;
    private Integer calendarId;
    private Long eventId;
    private Long begin;
    private Long end;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.instance_main_activity);

        instanceId = getIntent().getLongExtra("instanceId", 0);
        calendarId = getIntent().getIntExtra("calendarId", 0);
        eventId = getIntent().getLongExtra("eventId", 0);
        begin = getIntent().getLongExtra("begin", 0);
        end = getIntent().getLongExtra("end", 0);

        calendarViewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        //인스턴스의 일부 정보(title, description, begin, end)를 표시한다.
        setSimpleInstanceData();
    }

    private void setSimpleInstanceData()
    {
        ContentValues instance = calendarViewModel.getInstance(calendarId, instanceId, begin, end);

        //title
        if (instance.getAsString(CalendarContract.Instances.TITLE) != null)
        {
            if (!instance.getAsString(CalendarContract.Instances.TITLE).isEmpty())
            {
                binding.instanceTitleTextview.setText(instance.getAsString(CalendarContract.Instances.TITLE));
            } else
            {
                binding.instanceTitleTextview.setText(getString(R.string.empty_title));
            }
        } else
        {
            binding.instanceTitleTextview.setText(getString(R.string.empty_title));
        }

        //description
        if (instance.getAsString(CalendarContract.Instances.DESCRIPTION) != null)
        {
            if (!instance.getAsString(CalendarContract.Instances.DESCRIPTION).isEmpty())
            {
                binding.instanceDescriptionTextview.setText(instance.getAsString(CalendarContract.Instances.DESCRIPTION));
            }
        }

        //begin, end
        final boolean allDay = instance.getAsBoolean(CalendarContract.Instances.ALL_DAY);
        if (allDay)
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(begin);
            calendar.add(Calendar.HOUR_OF_DAY, -9);
            begin = calendar.getTimeInMillis();

            calendar.setTimeInMillis(end);
            calendar.add(Calendar.HOUR_OF_DAY, -9);
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            end = calendar.getTimeInMillis();
        }
        String beginStr = EventUtil.convertDateTime(begin, allDay, App.isPreference_key_using_24_hour_system());
        String endStr = EventUtil.convertDateTime(end, allDay, App.isPreference_key_using_24_hour_system());

        binding.instanceBeginTextview.setText(beginStr);
        binding.instanceEndTextview.setText(endStr);
    }

    private final View.OnClickListener instanceCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

        }
    };

    private final View.OnClickListener weatherCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

        }
    };

    private final View.OnClickListener placesCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

        }
    };

    private final View.OnClickListener foodsCardOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {

        }
    };

    private final ActivityResultLauncher<Intent> instanceActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {

                }
            });

    private final ActivityResultLauncher<Intent> mapActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {

                }
            });

    private final ActivityResultLauncher<Intent> weatherActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {

                }
            });

    private final ActivityResultLauncher<Intent> foodsActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {

                }
            });
}
