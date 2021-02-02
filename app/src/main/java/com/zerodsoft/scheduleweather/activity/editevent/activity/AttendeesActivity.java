package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.ContentValues;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.AttendeeListAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultListFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityAttendeesBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;

import java.util.List;

public class AttendeesActivity extends AppCompatActivity
{
    private ActivityAttendeesBinding binding;
    private SearchView searchView;
    private AttendeeListAdapter adapter;
    private List<ContentValues> attendeeList;
    public static final int SHOW_DETAILS_FOR_ATTENDEES = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendees);

        setSupportActionBar(binding.attendeeToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);

        binding.attendeeList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        binding.attendeeList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        attendeeList = getIntent().getParcelableArrayListExtra("attendeeList");
        ContentValues selectedCalendar = getIntent().getParcelableExtra("selectedCalendar");
        adapter = new AttendeeListAdapter(attendeeList, selectedCalendar);

        binding.attendeeList.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.attendee_toolbar, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.input_invite_attendee));
        searchView.setOnSearchClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            @Override
            public boolean onQueryTextSubmit(String query)
            {
                if (!query.isEmpty())
                {
                    ContentValues attendee = new ContentValues();
                    attendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, query);
                    attendeeList.add(attendee);
                    adapter.notifyItemInserted(attendeeList.size() - 1);
                    return true;
                } else
                {
                    return false;
                }
            }

            @Override
            public boolean onQueryTextChange(String newText)
            {
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener()
        {
            @Override
            public boolean onClose()
            {
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }
}