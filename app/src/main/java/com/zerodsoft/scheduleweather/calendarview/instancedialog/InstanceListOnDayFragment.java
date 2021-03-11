package com.zerodsoft.scheduleweather.calendarview.instancedialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DimenRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.CalendarContract;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.activity.main.AppMainActivity;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.CommonPopupMenu;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter.EventsInfoRecyclerViewAdapter;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter.InstancesOfDayAdapter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IToolbar;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.calendarview.week.WeekViewPagerAdapter;
import com.zerodsoft.scheduleweather.databinding.FragmentMonthEventsInfoBinding;
import com.zerodsoft.scheduleweather.event.util.EventUtil;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.SneakyThrows;
import lombok.val;

public class InstanceListOnDayFragment extends DialogFragment implements OnEventItemLongClickListener, IRefreshView, IControlEvent
{
    public static final String TAG = "MonthEventsInfoFragment";

    private final IConnectedCalendars iConnectedCalendars;
    private final OnEventItemClickListener onEventItemClickListener;
    private final IRefreshView iRefreshView;

    private CalendarViewModel viewModel;
    private FragmentMonthEventsInfoBinding binding;
    private InstancesOfDayAdapter adapter;

    private Long begin;
    private Long end;

    private final CommonPopupMenu commonPopupMenu = new CommonPopupMenu()
    {
        @Override
        public void onExceptedInstance(boolean isSuccessful)
        {
            if (isSuccessful)
            {
                iRefreshView.refreshView();
                refreshView();
            }
        }

        @Override
        public void onDeletedInstance(boolean isSuccessful)
        {
            if (isSuccessful)
            {
                iRefreshView.refreshView();
                refreshView();
            }
        }
    };

    public InstanceListOnDayFragment(IConnectedCalendars iConnectedCalendars, Fragment fragment)
    {
        this.onEventItemClickListener = (OnEventItemClickListener) fragment;
        this.iRefreshView = (IRefreshView) fragment;
        this.iConnectedCalendars = iConnectedCalendars;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();

        begin = bundle.getLong("begin");
        end = bundle.getLong("end");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        binding = FragmentMonthEventsInfoBinding.inflate(inflater);
        return binding.getRoot();
    }

    @SneakyThrows
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(CalendarViewModel.class);

        adapter = new InstancesOfDayAdapter(begin, end, onEventItemClickListener, this, iConnectedCalendars, this);
        binding.instancesDialogViewpager.setAdapter(adapter);
        binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);

        /*
        // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)
viewPager2.adapter = MyRecyclerViewAdapter()

// You need to retain one page on each side so that the next and previous items are visible
viewPager2.offscreenPageLimit = 1

// Add a PageTransformer that translates the next and previous items horizontally
// towards the center of the screen, which makes them visible
val nextItemVisiblePx = resources.getDimension(R.dimen.viewpager_next_item_visible)
val currentItemHorizontalMarginPx = resources.getDimension(R.dimen.viewpager_current_item_horizontal_margin)
val pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx
val pageTransformer = ViewPager2.PageTransformer { page: View, position: Float ->
    page.translationX = -pageTranslationX * position
    // Next line scales the item's height. You can remove it if you don't want this effect
    page.scaleY = 1 - (0.25f * abs(position))
    // If you want a fading effect uncomment the next line:
    // page.alpha = 0.25f + (1 - abs(position))
}
viewPager2.setPageTransformer(pageTransformer)

// The ItemDecoration gives the current (centered) item horizontal margin so that
// it doesn't occupy the whole screen width. Without it the items overlap
val itemDecoration = HorizontalMarginItemDecoration(
    context,
    R.dimen.viewpager_current_item_horizontal_margin
)
viewPager2.addItemDecoration(itemDecoration)
         */
    }


    /*

    class HorizontalMarginItemDecoration(context: Context, @DimenRes horizontalMarginInDp: Int) :
        RecyclerView.ItemDecoration() {

    private val horizontalMarginInPx: Int =
            context.resources.getDimension(horizontalMarginInDp).toInt()

    override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
    ) {
        outRect.right = horizontalMarginInPx
        outRect.left = horizontalMarginInPx
    }

}


<dimen name="viewpager_next_item_visible">26dp</dimen>
<dimen name="viewpager_current_item_horizontal_margin">42dp</dimen>


     */


    @Override
    public void onResume()
    {
        super.onResume();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (AppMainActivity.getDisplayWidth() * 0.8);
        layoutParams.height = (int) (AppMainActivity.getDisplayHeight() * 0.6);
        window.setAttributes(layoutParams);
    }

    @Override
    public void refreshView()
    {
        int currentItem = binding.instancesDialogViewpager.getCurrentItem();
        adapter.refresh(currentItem);
        adapter.notifyDataSetChanged();
    }

    @Override
    public Map<Integer, CalendarInstance> getInstances(long begin, long end)
    {
        return viewModel.getInstances(begin, end);
    }

    @Override
    public void createInstancePopupMenu(ContentValues instance, View anchorView, int gravity)
    {
        commonPopupMenu.createInstancePopupMenu(instance, getActivity(), anchorView, Gravity.CENTER);
    }
}

