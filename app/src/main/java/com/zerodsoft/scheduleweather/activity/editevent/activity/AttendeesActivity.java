package com.zerodsoft.scheduleweather.activity.editevent.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.adapter.AttendeeListAdapter;
import com.zerodsoft.scheduleweather.activity.map.fragment.search.SearchFragment;
import com.zerodsoft.scheduleweather.activity.map.fragment.searchresult.SearchResultListFragment;
import com.zerodsoft.scheduleweather.databinding.ActivityAttendeesBinding;
import com.zerodsoft.scheduleweather.etc.FragmentStateCallback;
import com.zerodsoft.scheduleweather.kakaomap.KakaoMapActivity;
import com.zerodsoft.scheduleweather.kakaomap.interfaces.IBottomSheet;

import java.util.ArrayList;
import java.util.List;

public class AttendeesActivity extends AppCompatActivity
{
    private ActivityAttendeesBinding binding;
    private SearchView searchView;
    private AttendeeListAdapter adapter;
    private List<ContentValues> attendeeList = new ArrayList<>();
    private ContentValues selectedCalendar;

    private static final String EMAIL_REGRESSION = "^[a-zA-Z0-9.+-/*]+@[a-zA-Z0-9/*-+]+\\.[a-zA-Z]{1,6}$";
    public static final int SHOW_DETAILS_FOR_ATTENDEES = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_attendees);

        setSupportActionBar(binding.attendeeToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(true);

        binding.attendeeList.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        binding.attendeeList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        attendeeList = getIntent().getParcelableArrayListExtra("attendeeList");
        selectedCalendar = getIntent().getParcelableExtra("selectedCalendar");

        adapter = new AttendeeListAdapter(attendeeList, selectedCalendar);
        adapter.registerAdapterDataObserver(adapterDataObserver);
        binding.attendeeList.setAdapter(adapter);

        if (attendeeList.isEmpty())
        {
            binding.authorityChipGroup.setVisibility(View.GONE);
        } else
        {
            binding.authorityChipGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        adapter.unregisterAdapterDataObserver(adapterDataObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.attendee_toolbar, menu);

        searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint(getString(R.string.input_invite_attendee));
        searchView.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
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
                    /*
                    이메일 주소인지 파악한다.
                    이메일이 아니면 검색 완료클릭시 이메일 주소가 아닙니다라는 내용의 메시지를 표시
                    */
                    if (query.matches(EMAIL_REGRESSION))
                    {
                        final String selectedCalendarOwnerAccount = selectedCalendar.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL);
                        final String selectedCalendarCalendarName = selectedCalendar.getAsString(CalendarContract.Attendees.ATTENDEE_NAME);
                        // 중복 여부 확인
                        // 리스트내에 이미 존재하는지 확인
                        for (ContentValues value : attendeeList)
                        {
                            if (value.getAsString(CalendarContract.Attendees.ATTENDEE_EMAIL).equals(query))
                            {
                                Toast.makeText(AttendeesActivity.this, getString(R.string.duplicate_attendee), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        }

                        // 이벤트의 캘린더와 중복되는지 확인
                        if (query.equals(selectedCalendarOwnerAccount) ||
                                query.equals(selectedCalendarCalendarName))
                        {
                            Toast.makeText(AttendeesActivity.this, getString(R.string.duplicate_attendee), Toast.LENGTH_SHORT).show();
                            return false;
                        }

                        if (attendeeList.isEmpty())
                        {
                            // 리스트가 비어있는 경우에는 이벤트에서 선택된 캘린더를 리스트의 맨 앞에 위치시킨다.
                            attendeeList.add(selectedCalendar);
                        }
                        ContentValues attendee = new ContentValues();
                        attendee.put(CalendarContract.Attendees.ATTENDEE_EMAIL, query);
                        attendeeList.add(attendee);
                        adapter.notifyDataSetChanged();

                        return true;
                    } else
                    {
                        Toast.makeText(AttendeesActivity.this, getString(R.string.not_matches_with_email), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } else
                {
                    Toast.makeText(AttendeesActivity.this, getString(R.string.not_matches_with_email), Toast.LENGTH_SHORT).show();
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
                searchView.setIconified(true);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        getIntent().putParcelableArrayListExtra("attendeeList", (ArrayList<? extends Parcelable>) attendeeList);
        setResult(RESULT_OK, getIntent());
        finish();
    }

    private final RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver()
    {
        @Override
        public void onChanged()
        {
            super.onChanged();

            if (adapter.getItemCount() == 0)
            {
                if (binding.authorityChipGroup.getVisibility() == View.VISIBLE)
                {
                    binding.authorityChipGroup.setVisibility(View.GONE);
                }
            } else if (binding.authorityChipGroup.getVisibility() == View.GONE)
            {
                binding.authorityChipGroup.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount)
        {
            super.onItemRangeInserted(positionStart, itemCount);


        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount)
        {
            super.onItemRangeRemoved(positionStart, itemCount);


        }
    };
}