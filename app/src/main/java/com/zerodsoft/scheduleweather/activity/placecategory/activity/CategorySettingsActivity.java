package com.zerodsoft.scheduleweather.activity.placecategory.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.CategoryExpandableListAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.PlaceCategoryEditPopup;
import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.databinding.ActivityCategorySettingsBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategorySettingsActivity extends AppCompatActivity implements PlaceCategoryEditPopup
{
    public static final int DEFAULT_CATEGORY_INDEX = 0;
    public static final int CUSTOM_CATEGORY_INDEX = 1;

    private ActivityCategorySettingsBinding binding;
    private CategoryExpandableListAdapter adapter;
    private PlaceCategoryViewModel viewModel;
    private List<PlaceCategoryDTO> customCategories;
    private List<PlaceCategoryDTO> defaultCategories;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_settings);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        binding.addCategoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 커스텀 카테고리 추가 다이얼로그 표시
                FrameLayout container = new FrameLayout(getApplicationContext());
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.leftMargin = getResources().getDimensionPixelSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics()));
                params.rightMargin = getResources().getDimensionPixelSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics()));

                EditText editText = new EditText(getApplicationContext());
                editText.setLayoutParams(params);
                container.addView(editText);

                new MaterialAlertDialogBuilder(getApplicationContext()).setTitle(R.string.add_custom_category)
                        .setMessage(R.string.add_custom_category_message)
                        .setView(container)
                        .setCancelable(false)
                        .setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                String result = editText.getText().toString();

                                //중복 검사
                                viewModel.containsCode(result, new CarrierMessagingService.ResultCallback<Boolean>()
                                {
                                    @Override
                                    public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                                    {
                                        runOnUiThread(new Runnable()
                                        {
                                            @Override
                                            public void run()
                                            {
                                                if (aBoolean)
                                                {
                                                    Toast.makeText(CategorySettingsActivity.this, getString(R.string.existing_place_category), Toast.LENGTH_SHORT).show();
                                                } else
                                                {
                                                    viewModel.insertCustom(result);
                                                    dialogInterface.dismiss();
                                                }
                                            }
                                        });
                                    }
                                });

                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                }).create().show();

            }
        });

        viewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        viewModel.getPlaceCategoryDataLiveData().observe(this, new Observer<PlaceCategoryData>()
        {
            @Override
            public void onChanged(PlaceCategoryData resultData)
            {
                if (resultData != null)
                {
                    customCategories = resultData.getCustomCategories();
                    defaultCategories = resultData.getDefaultPlaceCategories();
                    List<SelectedPlaceCategoryDTO> selectedCategories = resultData.getSelectedPlaceCategories();

                    boolean[][] checkedStates = new boolean[2][];
                    checkedStates[DEFAULT_CATEGORY_INDEX] = new boolean[defaultCategories.size()];
                    checkedStates[CUSTOM_CATEGORY_INDEX] = new boolean[customCategories.size()];

                    int index = 0;

                    //기본 카테고리 체크여부 설정
                    for (PlaceCategoryDTO defaultPlaceCategoryDTO : defaultCategories)
                    {
                        for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategories)
                        {
                            if (selectedPlaceCategory.getCode().equals(defaultPlaceCategoryDTO.getCode()))
                            {
                                checkedStates[DEFAULT_CATEGORY_INDEX][index] = true;
                                break;
                            }
                        }
                        index++;
                    }
                    index = 0;

                    //커스텀 카테고리 체크여부 설정
                    for (PlaceCategoryDTO customPlaceCategory : customCategories)
                    {
                        for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategories)
                        {
                            if (selectedPlaceCategory.getCode().equals(customPlaceCategory.getCode()))
                            {
                                checkedStates[CUSTOM_CATEGORY_INDEX][index] = true;
                                break;
                            }
                        }
                        index++;
                    }

                    adapter = new CategoryExpandableListAdapter(CategorySettingsActivity.this, viewModel, defaultCategories, customCategories, checkedStates);
                    binding.categoryExpandableList.setAdapter(adapter);
                }
            }
        });
        viewModel.selectCustom();
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    @Override
    public void showPopup(View view)
    {
        CategoryExpandableListAdapter.EditButtonHolder editButtonHolder = (CategoryExpandableListAdapter.EditButtonHolder) view.getTag();

        PopupMenu popupMenu = new PopupMenu(getApplicationContext(), view, Gravity.BOTTOM);
        popupMenu.getMenuInflater().inflate(R.menu.place_category_edit_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.delete_place_category:
                        viewModel.deleteCustom(editButtonHolder.getPlaceCategoryDTO().getCode());
                        break;
                    case R.id.edit_place_category:
                        showEditDialog(editButtonHolder.getPlaceCategoryDTO().getCode());
                        break;
                }
                popupMenu.dismiss();
                return true;
            }
        });
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener()
        {
            @Override
            public void onDismiss(PopupMenu menu)
            {
                popupMenu.dismiss();
            }
        });

        popupMenu.show();
    }

    private void showEditDialog(String code)
    {
        // 커스텀 카테고리 추가 다이얼로그 표시
        FrameLayout container = new FrameLayout(getApplicationContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics()));
        params.rightMargin = getResources().getDimensionPixelSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics()));

        TextView currentCodeTextView = new TextView(getApplicationContext());
        currentCodeTextView.setLayoutParams(params);
        container.addView(currentCodeTextView);
        currentCodeTextView.setText(code);

        EditText newCodeEditText = new EditText(getApplicationContext());
        newCodeEditText.setLayoutParams(params);
        container.addView(newCodeEditText);

        new MaterialAlertDialogBuilder(getApplicationContext()).setTitle(R.string.add_custom_category)
                .setMessage(R.string.add_custom_category_message)
                .setView(container)
                .setCancelable(false)
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String currentCode = currentCodeTextView.getText().toString();
                        String newCode = newCodeEditText.getText().toString();

                        //중복 검사
                        viewModel.containsCode(newCode, new CarrierMessagingService.ResultCallback<Boolean>()
                        {
                            @Override
                            public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException
                            {
                                if (aBoolean)
                                {
                                    Toast.makeText(CategorySettingsActivity.this, getString(R.string.existing_place_category), Toast.LENGTH_SHORT).show();
                                } else
                                {
                                    viewModel.updateCustom(currentCode, newCode);
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                dialogInterface.cancel();
            }
        }).create().show();
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