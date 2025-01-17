package com.zerodsoft.calendarplatform.activity.preferences.customfoodmenu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerodsoft.calendarplatform.R;
import com.zerodsoft.calendarplatform.activity.preferences.customfoodmenu.adapter.CustomFoodMenuAdapter;
import com.zerodsoft.calendarplatform.common.interfaces.DbQueryCallback;
import com.zerodsoft.calendarplatform.common.interfaces.OnClickedListItem;
import com.zerodsoft.calendarplatform.databinding.FragmentCustomFoodMenuSettingsBinding;
import com.zerodsoft.calendarplatform.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.calendarplatform.room.dto.CustomFoodMenuDTO;

import java.util.List;

public class CustomFoodMenuSettingsFragment extends Fragment implements OnClickedListItem<CustomFoodMenuDTO> {
	private FragmentCustomFoodMenuSettingsBinding binding;
	private CustomFoodMenuAdapter adapter;
	private CustomFoodMenuViewModel customFoodMenuViewModel;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		customFoodMenuViewModel = new ViewModelProvider(requireActivity()).get(CustomFoodMenuViewModel.class);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentCustomFoodMenuSettingsBinding.inflate(inflater);
		return binding.getRoot();
	}


	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressView.setContentView(binding.customFoodMenuRecyclerview);
		binding.customProgressView.onSuccessfulProcessingData();

		binding.customFoodMenuRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.customFoodMenuRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		adapter = new CustomFoodMenuAdapter(CustomFoodMenuSettingsFragment.this);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onChanged() {
				super.onChanged();
				if (adapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_added_custom_food_menu));
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
				if (adapter.getItemCount() == 0) {
					binding.customProgressView.onFailedProcessingData(getString(R.string.not_added_custom_food_menu));
				}
			}
		});
		binding.customFoodMenuRecyclerview.setAdapter(adapter);

		customFoodMenuViewModel.select(new DbQueryCallback<List<CustomFoodMenuDTO>>() {
			@Override
			public void onResultSuccessful(List<CustomFoodMenuDTO> customFoodMenuResultDto) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.setList(customFoodMenuResultDto);
						adapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});

		binding.addButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (binding.edittextCustomFoodmenu.getText().length() > 0) {
					String value = binding.edittextCustomFoodmenu.getText().toString();
					//중복검사
					customFoodMenuViewModel.containsMenu(value, new DbQueryCallback<Boolean>() {
						@Override
						public void onResultSuccessful(Boolean isDuplicate) {
							if (isDuplicate) {
								Toast.makeText(getContext(), R.string.duplicate_value, Toast.LENGTH_SHORT).show();
							} else {
								customFoodMenuViewModel.insert(value, new DbQueryCallback<CustomFoodMenuDTO>() {
									@Override
									public void onResultSuccessful(CustomFoodMenuDTO customFoodMenuResultDto) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												binding.edittextCustomFoodmenu.setText("");
												adapter.getList().add(customFoodMenuResultDto);
												adapter.notifyItemInserted(adapter.getItemCount() - 1);
											}
										});
									}

									@Override
									public void onResultNoData() {

									}
								});
							}
						}

						@Override
						public void onResultNoData() {

						}
					});


				} else {
					Toast.makeText(getContext(), R.string.hint_request_input_custom_food_menu, Toast.LENGTH_SHORT).show();
				}
			}
		});

		binding.edittextCustomFoodmenu.setOnKeyListener(new View.OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
					binding.addButton.callOnClick();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public void onClickedListItem(CustomFoodMenuDTO e, int position) {

	}

	@Override
	public void deleteListItem(CustomFoodMenuDTO e, int position) {
		customFoodMenuViewModel.delete(e.getId(), new DbQueryCallback<Boolean>() {
			@Override
			public void onResultSuccessful(Boolean result) {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						adapter.getList().remove(e);
						adapter.notifyItemRemoved(position);
					}
				});
			}

			@Override
			public void onResultNoData() {

			}
		});
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}