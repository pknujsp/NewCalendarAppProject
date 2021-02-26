package com.zerodsoft.scheduleweather.activity.placecategory.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.OnItemMoveListener;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.PlaceCategoryAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.databinding.ActivityPlaceCategoryBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        SharedPreferences defaultCategory = getSharedPreferences("default_category", MODE_PRIVATE);
        SharedPreferences customCategory = getSharedPreferences("custom_category", MODE_PRIVATE);

        final Set<String> selectedDefaultSet = new HashSet<>();
        final Set<String> selectedCustomSet = new HashSet<>();

        defaultCategory.getStringSet("selected_categories", selectedDefaultSet);
        customCategory.getStringSet("selected_categories", selectedCustomSet);

        final List<PlaceCategoryDTO> placeCategoryDTOList = new ArrayList<>();
        Collections.copy(placeCategoryDTOList, KakaoLocalApiCategoryUtil.getList());

        List<Integer> removeIndex = new ArrayList<>();

        //기본 카테고리 체크여부 설정
        for (int i = placeCategoryDTOList.size() - 1; i >= 0; i--)
        {
            String code = placeCategoryDTOList.get(i).getCode();
            for (String selectedCode : selectedDefaultSet)
            {
                if (selectedCode.equals(code))
                {
                    removeIndex.add(i);
                    break;
                }
            }
        }

        if (!removeIndex.isEmpty())
        {
            for (int i = removeIndex.size() - 1; i >= 0; i--)
            {
                placeCategoryDTOList.remove(removeIndex.get(i).intValue());
            }
        }

        adapter = new PlaceCategoryAdapter(placeCategoryDTOList, PlaceCategoryActivity.this);

        ItemTouchHelperCallback itemTouchHelperCallback = new ItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(binding.placeCategoryList);

        binding.placeCategoryList.setAdapter(adapter);
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.place_category_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.category_settings:
            {
                Intent intent = new Intent(this, CategorySettingsActivity.class);
                activityResultLauncher.launch(intent);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>()
            {
                @Override
                public void onActivityResult(ActivityResult result)
                {
                    final int resultCode = result.getResultCode();

                }
            }
    );

}