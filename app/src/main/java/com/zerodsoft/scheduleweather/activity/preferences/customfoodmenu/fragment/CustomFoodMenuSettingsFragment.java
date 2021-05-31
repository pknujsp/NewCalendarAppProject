package com.zerodsoft.scheduleweather.activity.preferences.customfoodmenu.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.preferences.customfoodmenu.adapter.CustomFoodMenuAdapter;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.databinding.FragmentCustomFoodMenuSettingsBinding;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.CustomFoodMenuViewModel;
import com.zerodsoft.scheduleweather.room.dto.CustomFoodMenuDTO;

import java.util.List;

public class CustomFoodMenuSettingsFragment extends Fragment implements OnClickedListItem<CustomFoodMenuDTO> {
	public static final String TAG = "CustomFoodMenuSettingsFragment";
	private FragmentCustomFoodMenuSettingsBinding binding;
	private CustomFoodMenuAdapter adapter;
	private CustomFoodMenuViewModel viewModel;
	private boolean isEdited = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		binding.customFoodMenuRecyclerview.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
		binding.customFoodMenuRecyclerview.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
		adapter = new CustomFoodMenuAdapter(CustomFoodMenuSettingsFragment.this);
		adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {

			@Override
			public void onChanged() {
				super.onChanged();
				if (adapter.getItemCount() == 0) {
					binding.customFoodMenuRecyclerview.setVisibility(View.GONE);
					binding.error.setVisibility(View.VISIBLE);
				} else {
					binding.customFoodMenuRecyclerview.setVisibility(View.VISIBLE);
					binding.error.setVisibility(View.GONE);
				}
			}

			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (binding.customFoodMenuRecyclerview.getVisibility() == View.GONE) {
					binding.customFoodMenuRecyclerview.setVisibility(View.VISIBLE);
					binding.error.setVisibility(View.GONE);
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (adapter.getItemCount() == 0) {
					binding.customFoodMenuRecyclerview.setVisibility(View.GONE);
					binding.error.setVisibility(View.VISIBLE);
				}
			}
		});
		binding.customFoodMenuRecyclerview.setAdapter(adapter);

		viewModel = new ViewModelProvider(this).get(CustomFoodMenuViewModel.class);
		viewModel.select(new DbQueryCallback<List<CustomFoodMenuDTO>>() {
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
					viewModel.containsMenu(value, new CarrierMessagingService.ResultCallback<Boolean>() {
						@Override
						public void onReceiveResult(@NonNull Boolean isDuplicate) throws RemoteException {
							if (isDuplicate) {
								Toast.makeText(getContext(), R.string.duplicate_value, Toast.LENGTH_SHORT).show();
							} else {
								viewModel.insert(value, new DbQueryCallback<CustomFoodMenuDTO>() {
									@Override
									public void onResultSuccessful(CustomFoodMenuDTO customFoodMenuResultDto) {
										requireActivity().runOnUiThread(new Runnable() {
											@Override
											public void run() {
												isEdited = true;
												binding.edittextCustomFoodmenu.setText("");
												adapter.getList().add(customFoodMenuResultDto);
												adapter.notifyItemInserted(adapter.getItemCount());
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


				} else {
					Toast.makeText(getContext(), R.string.hint_request_input_custom_food_menu, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onClickedListItem(CustomFoodMenuDTO e, int position) {

	}

	@Override
	public void deleteListItem(CustomFoodMenuDTO e, int position) {
		viewModel.delete(e.getMenuName(), new CarrierMessagingService.ResultCallback<Boolean>() {
			@Override
			public void onReceiveResult(@NonNull Boolean aBoolean) throws RemoteException {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						isEdited = true;
						adapter.getList().remove(e);
						adapter.notifyItemRemoved(position);
					}
				});
			}
		});
	}

	public boolean isEdited() {
		return isEdited;
	}
}