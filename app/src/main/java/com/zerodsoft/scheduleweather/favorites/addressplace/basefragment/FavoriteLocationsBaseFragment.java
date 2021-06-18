package com.zerodsoft.scheduleweather.favorites.addressplace.basefragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.FragmentAllFavoriteAddressPlaceBinding;
import com.zerodsoft.scheduleweather.event.foods.favorite.restaurant.FavoriteLocationViewModel;
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


public abstract class FavoriteLocationsBaseFragment extends Fragment implements OnClickedFavoriteItem {
	protected FragmentAllFavoriteAddressPlaceBinding binding;
	protected FavoriteLocationViewModel favoriteLocationViewModel;
	protected ArrayAdapter<CharSequence> spinnerAdapter;
	protected Set<FavoriteLocationDTO> checkedFavoriteLocationSet = new HashSet<>();
	protected FavoriteLocationAdapter favoriteLocationAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		favoriteLocationViewModel.getAddedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				if (favoriteLocationDTO.getType() != FavoriteLocationDTO.RESTAURANT) {
					onAddedFavoriteLocation(favoriteLocationDTO);
				}
			}
		});

		favoriteLocationViewModel.getRemovedFavoriteLocationMutableLiveData().observe(this, new Observer<FavoriteLocationDTO>() {
			@Override
			public void onChanged(FavoriteLocationDTO favoriteLocationDTO) {
				if (favoriteLocationDTO.getType() != FavoriteLocationDTO.RESTAURANT) {
					onRemovedFavoriteLocation(favoriteLocationDTO);
				}
			}
		});
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		binding = FragmentAllFavoriteAddressPlaceBinding.inflate(inflater);
		return binding.getRoot();
	}

	protected abstract void onAddedFavoriteLocation(FavoriteLocationDTO addedFavoriteLocation);

	protected void onRemovedFavoriteLocation(FavoriteLocationDTO removedFavoriteLocation) {
		List<FavoriteLocationDTO> list = favoriteLocationAdapter.getList();

		int indexOfList = 0;
		int listSize = list.size();
		for (; indexOfList < listSize; indexOfList++) {
			if (list.get(indexOfList).getId().equals(removedFavoriteLocation.getId())) {
				list.remove(indexOfList);
				break;
			}
		}

		favoriteLocationAdapter.notifyItemRemoved(indexOfList);
	}

	@Override
	public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		binding.customProgressViewForFavoriteAddressPlace.setContentView(binding.favoriteAddressPlaceList);
		binding.deleteBtn.setVisibility(View.GONE);

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
		favoriteLocationAdapter.registerAdapterDataObserver(favorteLocationsListDataObserver);
		binding.favoriteAddressPlaceList.setAdapter(favoriteLocationAdapter);
	}

	protected final void setFavoriteLocationList() {
		binding.customProgressViewForFavoriteAddressPlace.onStartedProcessingData();

		favoriteLocationViewModel.getFavoriteLocations(FavoriteLocationDTO.EXCEPT_RESTAURANT, new DbQueryCallback<List<FavoriteLocationDTO>>() {
			@Override
			public void onResultSuccessful(List<FavoriteLocationDTO> list) {
				onLoadedFavoriteLocationsList(list);
			}

			@Override
			public void onResultNoData() {
				requireActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						binding.customProgressViewForFavoriteAddressPlace.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
						binding.editButton.setVisibility(View.GONE);
					}
				});
			}
		});
	}

	protected abstract void onLoadedFavoriteLocationsList(List<FavoriteLocationDTO> list);

	protected final AdapterView.OnItemSelectedListener spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
			sort(favoriteLocationAdapter.getList());
			favoriteLocationAdapter.notifyDataSetChanged();
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {

		}
	};


	protected final Comparator<FavoriteLocationDTO> addedDateTimeComparator = new Comparator<FavoriteLocationDTO>() {
		@Override
		public int compare(FavoriteLocationDTO t1, FavoriteLocationDTO t2) {
			return Long.compare(Long.parseLong(t1.getAddedDateTime()), Long.parseLong(t2.getAddedDateTime()));
		}
	};

	protected final void sortByAddedDateTime(List<FavoriteLocationDTO> list) {
		list.sort(addedDateTimeComparator);
	}

	protected final void sortByAddedCategory(List<FavoriteLocationDTO> list) {
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

	protected abstract void sort(List<FavoriteLocationDTO> list);

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

	protected final RecyclerView.AdapterDataObserver favorteLocationsListDataObserver = new RecyclerView.AdapterDataObserver() {
		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			super.onItemRangeInserted(positionStart, itemCount);
			if (positionStart == 0 && itemCount > 0) {
				binding.customProgressViewForFavoriteAddressPlace.onSuccessfulProcessingData();
				binding.editButton.setVisibility(View.VISIBLE);
			}
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			super.onItemRangeRemoved(positionStart, itemCount);
			if (favoriteLocationAdapter.getItemCount() == 0) {
				binding.customProgressViewForFavoriteAddressPlace.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
				binding.editButton.setVisibility(View.GONE);
			}
		}

		@Override
		public void onChanged() {
			super.onChanged();
			if (favoriteLocationAdapter.getItemCount() == 0) {
				binding.customProgressViewForFavoriteAddressPlace.onFailedProcessingData(getString(R.string.empty_favorite_locations_list));
				binding.editButton.setVisibility(View.GONE);
			} else {
				binding.customProgressViewForFavoriteAddressPlace.onSuccessfulProcessingData();
				binding.editButton.setVisibility(View.VISIBLE);
			}
		}
	};
}