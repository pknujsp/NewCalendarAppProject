package com.zerodsoft.scheduleweather.calendarview.instancedialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.CalendarContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendar.CalendarInstanceUtil;
import com.zerodsoft.scheduleweather.calendar.CalendarViewModel;
import com.zerodsoft.scheduleweather.calendar.CommonPopupMenu;
import com.zerodsoft.scheduleweather.calendar.dto.CalendarInstance;
import com.zerodsoft.scheduleweather.calendarview.EventTransactionFragment;
import com.zerodsoft.scheduleweather.calendarview.instancedialog.adapter.InstancesOfDayAdapter;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IConnectedCalendars;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IControlEvent;
import com.zerodsoft.scheduleweather.calendarview.interfaces.IRefreshView;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemClickListener;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;
import com.zerodsoft.scheduleweather.databinding.FragmentMonthEventsInfoBinding;
import com.zerodsoft.scheduleweather.utility.ClockUtil;

import java.util.Date;
import java.util.Map;

import lombok.SneakyThrows;

public class InstanceListOnDayFragment extends DialogFragment implements OnEventItemLongClickListener, IRefreshView, IControlEvent, InstancesOfDayView.InstanceDialogMenuListener
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

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        return new Dialog(getContext(), R.style.DialogTransparent);
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
        binding.instancesDialogViewpager.setOffscreenPageLimit(3);

        final int nextItemVisiblePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 26f, getContext().getResources().getDisplayMetrics());
        final int currentItemHorizontalMarginPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, getContext().getResources().getDisplayMetrics());
        final int pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;

        ViewPager2.PageTransformer pageTransformer = new ViewPager2.PageTransformer()
        {
            @Override
            public void transformPage(@NonNull View page, float position)
            {
                page.setTranslationX(-pageTranslationX * position);
                page.setScaleY(1 - (0.25f * Math.abs(position)));
            }
        };

        binding.instancesDialogViewpager.setPageTransformer(pageTransformer);
        HorizontalMarginItemDecoration horizontalMarginItemDecoration = new HorizontalMarginItemDecoration(getContext());
        binding.instancesDialogViewpager.addItemDecoration(horizontalMarginItemDecoration);

        adapter = new InstancesOfDayAdapter(begin, end, onEventItemClickListener, iConnectedCalendars, this);
        binding.instancesDialogViewpager.setAdapter(adapter);
        binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, false);
        /*
        // MyRecyclerViewAdapter is an standard RecyclerView.Adapter :)

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

    @Override
    public void createInstancePopupMenu(ContentValues instance, View anchorView, int gravity)
    {
        commonPopupMenu.createInstancePopupMenu(instance, getActivity(), anchorView, Gravity.CENTER);
    }

    static class HorizontalMarginItemDecoration extends RecyclerView.ItemDecoration
    {
        private int horizontalMarginInPx;

        public HorizontalMarginItemDecoration(Context context)
        {
            horizontalMarginInPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 42f, context.getResources().getDisplayMetrics());
        }

        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
        {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.left = horizontalMarginInPx;
            outRect.right = horizontalMarginInPx;
        }
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
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 400f, getContext().getResources().getDisplayMetrics()));
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
    public void showPopupMenu(ContentValues instance, View anchorView, int gravity)
    {
        PopupMenu popupMenu = new PopupMenu(getContext(), anchorView, gravity);

        popupMenu.getMenuInflater().inflate(R.menu.instance_dialog_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.go_to_today_menu:
                        goToToday();
                        break;
                    case R.id.go_to_first_selected_day_menu:
                        goToFirstSelectedDay();
                        break;
                }
                return true;
            }
        });

        popupMenu.show();
    }

    private void goToFirstSelectedDay()
    {
        if (binding.instancesDialogViewpager.getCurrentItem() != EventTransactionFragment.FIRST_VIEW_POSITION)
        {
            binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION, true);
            refreshView();
        }
    }

    public void goToToday()
    {
        Date today = new Date(System.currentTimeMillis());
        Date firstSelectedDay = new Date(begin);

        int dayDifference = ClockUtil.calcDayDifference(today, firstSelectedDay);
        binding.instancesDialogViewpager.setCurrentItem(EventTransactionFragment.FIRST_VIEW_POSITION + dayDifference
                , true);
        if (adapter.containsPosition(EventTransactionFragment.FIRST_VIEW_POSITION + dayDifference))
        {
            refreshView();
        }
    }


}

