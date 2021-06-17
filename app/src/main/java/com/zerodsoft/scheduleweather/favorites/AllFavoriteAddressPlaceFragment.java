package com.zerodsoft.scheduleweather.favorites;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.naver.maps.geometry.LatLng;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentAllFavoriteAddressPlaceBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
import com.zerodsoft.scheduleweather.navermap.BottomSheetType;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.favorite.FavoriteLocationAdapter;
import com.zerodsoft.scheduleweather.navermap.favorite.OnClickedFavoriteItem;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class AllFavoriteAddressPlaceFragment extends Fragment implements OnClickedFavoriteItem {
	private FragmentAllFavoriteAddressPlaceBinding binding;
	private FavoriteLocationViewModel favoriteLocationViewModel;
	private ArrayAdapter<CharSequence> spinnerAdapter;
	private Set<FavoriteLocationDTO> checkedFavoriteLocationSet = new HashSet<>();
	private FavoriteLocationAdapter favoriteLocationAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		favoriteLocationViewModel = new ViewModelProvider(this).get(FavoriteLocationViewModel.class);

		favoriteLocationViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO addedFavoriteLocationDTO) {
				if (addedFavoriteLocationDTO.getType() != FavoriteLocationDTO.RESTAURANT) {
					List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();
					list.add(addedFavoriteLocationDTO);

					sort(list);
					favoriteLocationAdapter.notifyDataSetChanged();
				}
			}
		});

		favoriteLocationViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();

				int listSize = list.size();
				for (int indexOfList = 0; indexOfList < listSize; indexOfList++) {
					if (list.get(indexOfList).getId().equals(favoriteLocationDTO.getId())) {
						list.remove(indexOfList);
						favoriteLocationAdapter.notifyItemRemoved(indexOfList);
						break;
					}
				}

			}
		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);

		if (hidden) {
		} else {

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentAllFavoriteAddressPlaceBinding.inflate(inflater);
		return binding.getRoot();
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.deleteBtn.setVisibility(View.GONE);
		binding.customProgressViewForFavoriteAddressPlace.setContentView(binding.favoriteAddressPlaceList);

		spinnerAdapter = ArrayAdapter.createFromResource(getContext(),
				R.array.all_favorite_locations_sort_spinner, android.R.layout.simple_spinner_item);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		binding.sortSpinnerForAddressPlace.setAdapter(spinnerAdapter);
		binding.sortSpinnerForAddressPlace.setSelection(0);
		binding.sortSpinnerForAddressPlace.setOnItemSelectedListener(spinnerItemSelectedListener);

		binding.favoriteAddressPlaceList.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
		binding.favoriteAddressPlaceList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

		favoriteLocationAdapter = new FavoriteLocationAdapter(this, new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
				FavoriteLocationDTO favoriteLocationDTO = (FavoriteLocationDTO) compoundButton.getTag();

				if (checked) {
					checkedFavoriteLocationSet.add(favoriteLocationDTO);
				} else {
					checkedFavoriteLocationSet.remove(favoriteLocationDTO);
				}
			}
		});
		favoriteLocationAdapter.setDistanceVisibility(View.GONE);
		binding.favoriteAddressPlaceList.setAdapter(favoriteLocationAdapter);

		favoriteLocationAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				if (positionStart == 0 && itemCount > 0) {
					binding.customProgressViewForFavoriteAddressPlace.onSuccessfulProcessingData();
				}
			}

			@Override
			public void onItemRangeRemoved(int positionStart, int itemCount) {
				super.onItemRangeRemoved(positionStart, itemCount);
				if (favoriteLocationAdapter.getItemCount() == 0) {
					binding.customProgressViewForFavoriteAddressPlace.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
				}
			}

			@Override
			public void onChanged() {
				super.onChanged();
				if (favoriteLocationAdapter.getItemCount() == 0) {
					binding.customProgressViewForFavoriteAddressPlace.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
				} else {
					binding.customProgressViewForFavoriteAddressPlace.onSuccessfulProcessingData();
				}
			}
		});

		binding.editButton.setOnClickListener(new View.OnClickListener() {
			boolean isChecked = false;

			@Override
			public void onClick(View view) {
				isChecked = !isChecked;

				if (isChecked) {
					binding.editButton.setTextColor(Color.BLUE);
					binding.deleteBtn.setVisibility(View.VISIBLE);
					favoriteLocationAdapter.setCheckBoxVisibility(View.VISIBLE);
				} else {
					binding.editButton.setTextColor(ContextCompat.getColor(getContext(), R.color.gray_600));
					binding.deleteBtn.setVisibility(View.GONE);
					favoriteLocationAdapter.setCheckBoxVisibility(View.GONE);
				}
				checkedFavoriteLocationSet.clear();
				favoriteLocationAdapter.notifyDataSetChanged();
			}
		});

		binding.deleteBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (checkedFavoriteLocationSet.isEmpty()) {
					Toast.makeText(getContext(), R.string.not_checked_favorite_locations, Toast.LENGTH_SHORT).show();
				} else {
					for (FavoriteLocationDTO favoriteLocationDTO : checkedFavoriteLocationSet) {
						favoriteLocationViewModel.delete(favoriteLocationDTO, null);
					}

					binding.editButton.callOnClick();
				}
			}
		});

		setFavoriteLocationList();
	}

	private void setFavoriteLocationList() {
		binding.customProgressViewForFavoriteAddressPlace.onStartedProcessingData();

		favoriteLocationViewModel.getFavoriteLocations(FavoriteLocationDTO.ONLY_FOR_MAP, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> list) {
				sort(list);
				favoriteLocationAdapter.setList(list);

				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						favoriteLocationAdapter.notifyDataSetChanged();
					}
				});
			}

			@Override
			public void onResultNoData() {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressViewForFavoriteAddressPlace.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
					}
				});
			}
		});
	}

	private final AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
			sort(favoriteLocationAdapter.getList());
			favoriteLocationAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {

		}
	};


	private final Comparator<FavoriteLocationDTO> addedDateTimeComparator = new Comparator<FavoriteLocationDTO>() {
		@Override
		public int compare(FavoriteLocationDTO t1, FavoriteLocationDTO t2) {
			return Long.compare(Long.parseLong(t1.getAddedDateTime()), Long.parseLong(t2.getAddedDateTime()));
		}
	};

	private void sort(List<FavoriteLocationDTO> list) {
		if (binding.sortSpinnerForAddressPlace.getSelectedItemPosition() == 0) {
			//datetime
			list.sort(addedDateTimeComparator);
		} else {
			//카테고리 유형 순서 - 음식점, 주소, 장소
			//먼저 카테고리 분류를 시행
			Set<Integer> categoryTypeSet = new HashSet<>();
			Map<Integer, List<FavoriteLocationDTO>> sortedListMap = new HashMap<>();

			for (FavoriteLocationDTO favoriteLocationDTO : list) {
				categoryTypeSet.add(favoriteLocationDTO.getType());
			}

			for (Integer type : categoryTypeSet) {
				sortedListMap.put(type, new ArrayList<>());
			}

			for (FavoriteLocationDTO favoriteLocationDTO : list) {
				sortedListMap.get(favoriteLocationDTO.getType()).add(favoriteLocationDTO);
			}

			list.clear();
			for (Integer type : categoryTypeSet) {
				list.addAll(sortedListMap.get(type));
			}
		}
	}

	@Override
	public void onClickedListItem(FavoriteLocationDTO e, int position) {

	}

	@Override
	public void deleteListItem(FavoriteLocationDTO e, int position) {

	}

	@Override
	public void onClickedEditButton(FavoriteLocationDTO e, View anchorView, int index) {
		PopupMenu popupMenu = new PopupMenu(getContext(), anchorView, Gravity.LEFT);

		popupMenu.getMenuInflater().inflate(R.menu.favorite_locations_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.delete_favorite_location:
						favoriteLocationViewModel.delete(e, null);
						break;
				}
				return true;
			}
		});

		popupMenu.show();
	}

	@Override
	public void onClickedShareButton(FavoriteLocationDTO e) {

	}
}