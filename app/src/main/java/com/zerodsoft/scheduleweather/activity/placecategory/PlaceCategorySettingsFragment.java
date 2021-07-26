package com.zerodsoft.scheduleweather.activity.placecategory;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.CategoryExpandableListAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.adapter.PlaceCategoryAdapter;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.OnItemMoveListener;
import com.zerodsoft.scheduleweather.activity.placecategory.interfaces.PlaceCategoryEditPopup;
import com.zerodsoft.scheduleweather.activity.placecategory.model.PlaceCategoryData;
import com.zerodsoft.scheduleweather.activity.placecategory.viewmodel.PlaceCategoryViewModel;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentPlaceCategorySettingsBinding;
import com.zerodsoft.scheduleweather.retrofit.KakaoLocalApiCategoryUtil;
import com.zerodsoft.scheduleweather.room.dto.CustomPlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.PlaceCategoryDTO;
import com.zerodsoft.scheduleweather.room.dto.SelectedPlaceCategoryDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlaceCategorySettingsFragment extends Fragment implements PlaceCategoryAdapter.OnStartDragListener, PlaceCategoryEditPopup {
	public static final int DEFAULT_CATEGORY_INDEX = 0;
	public static final int CUSTOM_CATEGORY_INDEX = 1;

	private FragmentPlaceCategorySettingsBinding binding;
	private PlaceCategoryViewModel placeCategoryViewModel;

	private ItemTouchHelper itemTouchHelper;
	private ItemTouchHelperCallback itemTouchHelperCallback;
	private PlaceCategoryAdapter selectedPlaceCategoryAdapter;

	private CategoryExpandableListAdapter categoryExpandableListAdapter;
	private List<PlaceCategoryDTO> customCategories;
	private List<PlaceCategoryDTO> defaultCategories;
	private AlertDialog customCategoryDialog;

	private boolean initializing = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		placeCategoryViewModel = new ViewModelProvider(requireActivity()).get(PlaceCategoryViewModel.class);

		placeCategoryViewModel.getOnSelectedCategoryLiveData().observe(this, new Observer<PlaceCategoryDTO>() {
			@Override
			public void onChanged(PlaceCategoryDTO selectedCategory) {
				if (!initializing) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							selectedPlaceCategoryAdapter.getPlaceCategoryList().add(selectedCategory);
							selectedPlaceCategoryAdapter.notifyItemInserted(selectedPlaceCategoryAdapter.getItemCount() - 1);
						}
					});
				}
			}
		});

		placeCategoryViewModel.getOnUnSelectedCategoryLiveData().observe(this, new Observer<String>() {
			@Override
			public void onChanged(String unselectedCategoryCode) {
				if (!initializing) {
					requireActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							final int position = selectedPlaceCategoryAdapter.getItemPosition(unselectedCategoryCode);
							selectedPlaceCategoryAdapter.getPlaceCategoryList().remove(position);
							selectedPlaceCategoryAdapter.notifyItemRemoved(position);
						}
					});
				}
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentPlaceCategorySettingsBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.selectedPlaceCategoryList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.customProgressView.setContentView(binding.selectedPlaceCategoryList);
		binding.customProgressView.onSuccessfulProcessingData();

		binding.addCustomPlaceCategoryBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// 커스텀 카테고리 추가 다이얼로그 표시
				customCategoryDialog.show();
			}
		});

		FrameLayout container = new FrameLayout(getContext());
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		params.leftMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());
		params.rightMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16f, getResources().getDisplayMetrics());

		EditText editText = new EditText(getContext());
		editText.setLayoutParams(params);
		container.addView(editText);

		customCategoryDialog = new MaterialAlertDialogBuilder(requireActivity()).setTitle(R.string.add_custom_category)
				.setMessage(R.string.add_custom_category_message)
				.setView(container)
				.setCancelable(false)
				.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						String newCategoryName = editText.getText().toString();

						//중복 검사
						placeCategoryViewModel.contains(newCategoryName, new DbQueryCallback<Boolean>() {
							@Override
							public void onResultSuccessful(Boolean contains) {
								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										if (contains) {
											Toast.makeText(getContext(), getString(R.string.existing_place_category), Toast.LENGTH_SHORT).show();
										} else {
											placeCategoryViewModel.addCustom(newCategoryName, new DbQueryCallback<CustomPlaceCategoryDTO>() {
												@Override
												public void onResultSuccessful(CustomPlaceCategoryDTO addedCustomPlaceCategoryDTO) {
													PlaceCategoryDTO placeCategoryDTO = new PlaceCategoryDTO();
													placeCategoryDTO.setCustom(true);
													placeCategoryDTO.setCode(addedCustomPlaceCategoryDTO.getCode());
													placeCategoryDTO.setDescription(addedCustomPlaceCategoryDTO.getCode());

													customCategories.add(placeCategoryDTO);
													categoryExpandableListAdapter.getCheckedStatesMap().get(CUSTOM_CATEGORY_INDEX).add(false);

													requireActivity().runOnUiThread(new Runnable() {
														@Override
														public void run() {
															categoryExpandableListAdapter.notifyDataSetChanged();
															dialogInterface.dismiss();
														}
													});

												}

												@Override
												public void onResultNoData() {

												}
											});
										}
									}
								});
							}

							@Override
							public void onResultNoData() {

							}
						});

					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialogInterface, int i) {
						dialogInterface.cancel();
					}
				}).create();

		selectedPlaceCategoryAdapter = new PlaceCategoryAdapter(this);
		selectedPlaceCategoryAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onChanged() {
				super.onChanged();
				if (selectedPlaceCategoryAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_selected_place_category));
				} else {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (positionStart == 0) {
					binding.customProgressView.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (selectedPlaceCategoryAdapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_selected_place_category));
				}
			}
		});
		binding.selectedPlaceCategoryList.setAdapter(selectedPlaceCategoryAdapter);

		placeCategoryViewModel.selectConvertedSelected(new DbQueryCallback<List<PlaceCategoryDTO>>() {
			@Override
			public void onResultSuccessful(List<PlaceCategoryDTO> selectedPlaceCategoryList) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						itemTouchHelperCallback = new ItemTouchHelperCallback(selectedPlaceCategoryAdapter);
						itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
						itemTouchHelper.attachToRecyclerView(binding.selectedPlaceCategoryList);

						selectedPlaceCategoryAdapter.setPlaceCategoryList(selectedPlaceCategoryList);
						selectedPlaceCategoryAdapter.notifyDataSetChanged();

						initializing = false;
					}
				});
			}

			@Override
			public void onResultNoData() {
				binding.customProgressView.onFailedProcessingData(getString(R.string.not_selected_place_category));
				initializing = false;
			}
		});

		placeCategoryViewModel.getSettingsData(new DbQueryCallback<PlaceCategoryData>() {
			@Override
			public void onResultSuccessful(PlaceCategoryData resultData) {
				customCategories = resultData.getCustomCategories();
				defaultCategories = resultData.getDefaultPlaceCategories();
				List<SelectedPlaceCategoryDTO> selectedCategories = resultData.getSelectedPlaceCategories();

				boolean[][] checkedStates = new boolean[2][];
				checkedStates[DEFAULT_CATEGORY_INDEX] = new boolean[defaultCategories.size()];
				checkedStates[CUSTOM_CATEGORY_INDEX] = new boolean[customCategories.size()];

				int index = 0;

				//기본 카테고리 체크여부 설정
				for (PlaceCategoryDTO defaultPlaceCategoryDTO : defaultCategories) {
					for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategories) {
						if (selectedPlaceCategory.getCode().equals(defaultPlaceCategoryDTO.getCode())) {
							checkedStates[DEFAULT_CATEGORY_INDEX][index] = true;
							break;
						}
					}
					index++;
				}
				index = 0;

				//커스텀 카테고리 체크여부 설정
				for (PlaceCategoryDTO customPlaceCategory : customCategories) {
					for (SelectedPlaceCategoryDTO selectedPlaceCategory : selectedCategories) {
						if (selectedPlaceCategory.getCode().equals(customPlaceCategory.getCode())) {
							checkedStates[CUSTOM_CATEGORY_INDEX][index] = true;
							break;
						}
					}
					index++;
				}

				categoryExpandableListAdapter = new CategoryExpandableListAdapter(getContext(), PlaceCategorySettingsFragment.this,
						placeCategoryViewModel,
						defaultCategories,
						customCategories,
						checkedStates);

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.categoryExpandableList.setAdapter(categoryExpandableListAdapter);
						binding.categoryExpandableList.expandGroup(0);
						binding.categoryExpandableList.expandGroup(1);
					}
				});

			}

			@Override
			public void onResultNoData() {

			}
		});
	}

	@Override
	public void showPopup(View view) {
		CategoryExpandableListAdapter.EditButtonHolder editButtonHolder = (CategoryExpandableListAdapter.EditButtonHolder) view.getTag();

		PopupMenu popupMenu = new PopupMenu(requireActivity(), view, Gravity.BOTTOM);
		requireActivity().getMenuInflater().inflate(R.menu.place_category_edit_menu, popupMenu.getMenu());

		final String code = editButtonHolder.getPlaceCategoryDTO().getCode();

		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				switch (item.getItemId()) {
					case R.id.delete_place_category:
						placeCategoryViewModel.deleteCustom(code, new DbQueryCallback<Boolean>() {
							@Override
							public void onResultSuccessful(Boolean deleted) {
								for (int index = 0; index < customCategories.size(); index++) {
									if (customCategories.get(index).getCode().equals(code)) {
										if (categoryExpandableListAdapter.getCheckedStatesMap().get(CUSTOM_CATEGORY_INDEX).get(index)) {
											placeCategoryViewModel.deleteSelected(code, new DbQueryCallback<Boolean>() {
												@Override
												public void onResultSuccessful(Boolean result) {

												}

												@Override
												public void onResultNoData() {

												}
											});
										}
										categoryExpandableListAdapter.getCheckedStatesMap().get(CUSTOM_CATEGORY_INDEX).remove(index);
										customCategories.remove(index);
										break;
									}
								}

								requireActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										categoryExpandableListAdapter.notifyDataSetChanged();
									}
								});


							}

							@Override
							public void onResultNoData() {

							}
						});
						break;
				}
				popupMenu.dismiss();
				return true;
			}
		});
		popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
			@Override
			public void onDismiss(PopupMenu menu) {
				popupMenu.dismiss();
			}
		});

		popupMenu.show();
	}

	@Override
	public void onStartDrag(PlaceCategoryAdapter.CategoryViewHolder viewHolder) {
		itemTouchHelper.startDrag(viewHolder);
	}


	class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
		private final OnItemMoveListener onItemMoveListener;

		public ItemTouchHelperCallback(OnItemMoveListener onItemMoveListener) {
			this.onItemMoveListener = onItemMoveListener;
		}

		@Override
		public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
			int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
			int swipeFlags = ItemTouchHelper.START;
			return makeMovementFlags(dragFlags, swipeFlags);
		}

		@Override
		public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
			// 움직이면 어떻게 할것인지 구현
			onItemMoveListener.onItemMove(viewHolder.getBindingAdapterPosition(), target.getBindingAdapterPosition());
			return true;
		}

		@Override
		public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
		}

		@Override
		public boolean isItemViewSwipeEnabled() {
			return false;
		}


	}
}