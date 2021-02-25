package com.zerodsoft.scheduleweather.activity.placecategory.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.OnItemMoveListener;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.PlaceCategoryAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.databinding.ActivityPlaceCategoryBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.retrofit.PlaceCategory;

import java.util.ArrayList;
import java.util.List;

public class PlaceCategoryActivity extends AppCompatActivity implements PlaceCategoryAdapter.OnStartDragListener
{
    private ItemTouchHelper itemTouchHelper;
    private PlaceCategoryAdapter adapter;
    private ActivityPlaceCategoryBinding binding;
    private PlaceCategoryViewModel viewModel;

    @Override
    public void onStartDrag(PlaceCategoryAdapter.CategoryViewHolder viewHolder)
    {
        itemTouchHelper.startDrag(viewHolder);
    }

    public class ItemTouchHelperCallback extends ItemTouchHelper.Callback
    {
        private final OnItemMoveListener onItemMoveListener;

        public ItemTouchHelperCallback(OnItemMoveListener onItemMoveListener)
        {
            this.onItemMoveListener = onItemMoveListener;
        }

        @Override
        public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder)
        {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target)
        {
            // 움직이면 어떻게 할것인지 구현
            onItemMoveListener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_category);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        binding.placeCategoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        viewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        viewModel.getPlaceCategoryListLiveData().observe(this, new Observer<List<PlaceCategory>>()
        {
            @Override
            public void onChanged(List<PlaceCategory> placeCategories)
            {
                //기본 카테고리
                List<PlaceCategory> categoryList = new ArrayList<>(KakaoLocalApiCategoryUtil.getList());

                if (!placeCategories.isEmpty())
                {
                    //커스텀 카테고리가 지정되어 있는 경우
                    categoryList.addAll(placeCategories);
                }
                adapter = new PlaceCategoryAdapter(categoryList, PlaceCategoryActivity.this);

                ItemTouchHelperCallback itemTouchHelperCallback = new ItemTouchHelperCallback(adapter);
                itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                itemTouchHelper.attachToRecyclerView(binding.placeCategoryList);

                binding.placeCategoryList.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}