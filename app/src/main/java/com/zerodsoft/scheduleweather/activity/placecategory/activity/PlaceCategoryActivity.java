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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.Menu;
import android.view.MenuItem;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.OnItemMoveListener;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.PlaceCategoryAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.databinding.ActivityPlaceCategoryBinding;
import com.zerodsoft.scheduleweather.event.main.InstanceMainActivity;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceCategoryActivity extends AppCompatActivity implements PlaceCategoryAdapter.OnStartDragListener
{
    private ItemTouchHelper itemTouchHelper;
    private ItemTouchHelperCallback itemTouchHelperCallback;
    private PlaceCategoryAdapter adapter;
    private ActivityPlaceCategoryBinding binding;
    private PlaceCategoryViewModel viewModel;
    private List<PlaceCategoryDTO> placeCategories;
    private boolean isEdited = false;

    public static final int RESULT_MODIFIED_CATEGORY = 10;

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
            isEdited = true;
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
        actionBar.setTitle(R.string.category_settings);

        binding.placeCategoryList.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        viewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        viewModel.selectSelected(resultCallback);
    }

    @Override
    public void onBackPressed()
    {
        // 변경된 순서 저장
        if (isEdited)
        {
            viewModel.deleteAllSelected(new CarrierMessagingService.ResultCallback<Boolean>()
            {
                @Override
                public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                {
                    if (aBoolean)
                    {
                        viewModel.insertAllSelected(placeCategories, new CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull List<SelectedPlaceCategoryDTO> selectedPlaceCategoryDTOS) throws RemoteException
                            {
                                setResult(InstanceMainActivity.RESULT_EDITED_PLACE_CATEGORY);
                                finish();
                            }
                        });
                    }
                }
            });
        } else
        {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.place_category_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
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
                Intent intent = new Intent(PlaceCategoryActivity.this, CategorySettingsActivity.class);
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
                    switch (result.getResultCode())
                    {
                        case RESULT_MODIFIED_CATEGORY:
                            viewModel.selectSelected(resultCallback);
                            isEdited = true;
                            break;
                        case RESULT_CANCELED:
                            break;
                    }
                }
            }
    );

    private final CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>> resultCallback = new CarrierMessagingService.ResultCallback<List<SelectedPlaceCategoryDTO>>()
    {
        @Override
        public void onReceiveResult(@NonNull List<SelectedPlaceCategoryDTO> result) throws RemoteException
        {
            Map<String, String> defaultPlaceCategoryMap = KakaoLocalApiCategoryUtil.getDefaultPlaceCategoryMap();
            placeCategories = new ArrayList<>();

            for (SelectedPlaceCategoryDTO selectedPlaceCategory : result)
            {
                PlaceCategoryDTO placeCategory = new PlaceCategoryDTO();

                //기본 카테고리 인 경우
                if (defaultPlaceCategoryMap.containsKey(selectedPlaceCategory.getCode()))
                {
                    placeCategory.setCode(selectedPlaceCategory.getCode());
                    placeCategory.setDescription(defaultPlaceCategoryMap.get(selectedPlaceCategory.getCode()));
                } else
                {
                    //커스텀인 경우
                    placeCategory.setCode(selectedPlaceCategory.getCode());
                    placeCategory.setDescription(selectedPlaceCategory.getCode());
                    placeCategory.setCustom(true);
                }
                placeCategories.add(placeCategory);
            }

            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    adapter = new PlaceCategoryAdapter(placeCategories, PlaceCategoryActivity.this);

                    itemTouchHelperCallback = new ItemTouchHelperCallback(adapter);
                    itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
                    itemTouchHelper.attachToRecyclerView(binding.placeCategoryList);

                    binding.placeCategoryList.setAdapter(adapter);
                }
            });

        }
    };

}