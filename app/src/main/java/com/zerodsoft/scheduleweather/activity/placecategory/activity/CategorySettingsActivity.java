package com.zerodsoft.scheduleweather.activity.placecategory.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.CategoryExpandableListAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.PlaceCategoryEditPopup;
import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.databinding.ActivityCategorySettingsBinding;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import java.util.List;

public class CategorySettingsActivity extends AppCompatActivity implements PlaceCategoryEditPopup
{
    public static final int DEFAULT_CATEGORY_INDEX = 0;
    public static final int CUSTOM_CATEGORY_INDEX = 1;

    private ActivityCategorySettingsBinding binding;
    private CategoryExpandableListAdapter adapter;
    private PlaceCategoryViewModel viewModel;
    private List<PlaceCategoryDTO> customCategories;
    private List<PlaceCategoryDTO> defaultCategories;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_category_settings);

        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.category_detail_settings);

        FrameLayout container = new FrameLayout(getApplicationContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
        params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());

        EditText editText = new EditText(getApplicationContext());
        editText.setLayoutParams(params);
        container.addView(editText);

        dialog = new MaterialAlertDialogBuilder(this).setTitle(R.string.add_custom_category)
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
                                            viewModel.insertCustom(result, new CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO>()
                                            {
                                                @Override
                                                public void onReceiveResult(@NonNull CustomPlaceCategoryDTO customPlaceCategoryDTO) throws RemoteException
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            //리스트 갱신
                                                            PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
                                                            placeCategoryDTO.setCustom(true);
                                                            placeCategoryDTO.setCode(customPlaceCategoryDTO.getCode());
                                                            placeCategoryDTO.setDescription(customPlaceCategoryDTO.getCode());

                                                            customCategories.add(placeCategoryDTO);
                                                            adapter.getCheckedStatesMap().get(CUSTOM_CATEGORY_INDEX).add(false);
                                                            adapter.notifyDataSetChanged();
                                                            dialogInterface.dismiss();
                                                        }
                                                    });

                                                }
                                            });
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
                }).create();

        binding.addCategoryButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // 커스텀 카테고리 추가 다이얼로그 표시
                dialog.show();
            }
        });

        viewModel = new ViewModelProvider(this).get(PlaceCategoryViewModel.class);
        viewModel.getSettingsData(new CarrierMessagingService.ResultCallback<PlaceCategoryData>()
        {
            @Override
            public void onReceiveResult(@NonNull PlaceCategoryData resultData) throws RemoteException
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
                binding.categoryExpandableList.expandGroup(0);
                binding.categoryExpandableList.expandGroup(1);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        setResult(PlaceCategoryActivity.RESULT_MODIFIED_CATEGORY);
        finish();
    }

    @Override
    public void showPopup(View view)
    {
        CategoryExpandableListAdapter.EditButtonHolder editButtonHolder = (CategoryExpandableListAdapter.EditButtonHolder) view.getTag();

        PopupMenu popupMenu = new PopupMenu(this, view, Gravity.BOTTOM);
        getMenuInflater().inflate(R.menu.place_category_edit_menu, popupMenu.getMenu());

        final String code = editButtonHolder.getPlaceCategoryDTO().getCode();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.delete_place_category:
                        viewModel.deleteCustom(code, new CarrierMessagingService.ResultCallback<Boolean>()
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
                                            for (int index = 0; index < customCategories.size(); index++)
                                            {
                                                if (customCategories.get(index).getCode().equals(code))
                                                {
                                                    adapter.getCheckedStatesMap().get(CUSTOM_CATEGORY_INDEX).remove(index);
                                                    customCategories.remove(index);
                                                    break;
                                                }
                                            }
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                });
                            }
                        });
                        break;
                    case R.id.edit_place_category:
                        showUpdateDialog(code);
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

    private void showUpdateDialog(String code)
    {
        // 커스텀 카테고리 수정 다이얼로그 표시
        FrameLayout container = new FrameLayout(getApplicationContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
        params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());

        EditText newCodeEditText = new EditText(getApplicationContext());
        newCodeEditText.setLayoutParams(params);
        newCodeEditText.setText(code);
        container.addView(newCodeEditText);

        new MaterialAlertDialogBuilder(this).setTitle(R.string.update_custom_category)
                .setMessage(R.string.update_custom_category_message)
                .setView(container)
                .setCancelable(false)
                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        String newCode = newCodeEditText.getText().toString();

                        //중복 검사
                        viewModel.containsCode(newCode, new CarrierMessagingService.ResultCallback<Boolean>()
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
                                            viewModel.updateCustom(code, newCode, new CarrierMessagingService.ResultCallback<CustomPlaceCategoryDTO>()
                                            {
                                                @Override
                                                public void onReceiveResult(@NonNull CustomPlaceCategoryDTO customPlaceCategoryDTO) throws RemoteException
                                                {
                                                    runOnUiThread(new Runnable()
                                                    {
                                                        @Override
                                                        public void run()
                                                        {
                                                            PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
                                                            placeCategoryDTO.setCustom(true);
                                                            placeCategoryDTO.setCode(customPlaceCategoryDTO.getCode());
                                                            placeCategoryDTO.setDescription(customPlaceCategoryDTO.getCode());

                                                            for (int index = 0; index < customCategories.size(); index++)
                                                            {
                                                                if (customCategories.get(index).getCode().equals(code))
                                                                {
                                                                    customCategories.remove(index);
                                                                    customCategories.add(index, placeCategoryDTO);
                                                                    break;
                                                                }
                                                            }
                                                            adapter.notifyDataSetChanged();
                                                            dialogInterface.dismiss();
                                                        }
                                                    });

                                                }
                                            });
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