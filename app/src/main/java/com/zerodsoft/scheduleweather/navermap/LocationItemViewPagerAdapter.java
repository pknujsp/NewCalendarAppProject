package com.zerodsoft.scheduleweather.navermap;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.common.view.CustomProgressView;
import com.zerodsoft.scheduleweather.databinding.CardviewPlacesItemBinding;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.navermap.util.LocationUtil;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

import java.util.ArrayList;
import java.util.List;

public class LocationItemViewPagerAdapter extends RecyclerView.Adapter<LocationItemViewPagerAdapter.PlaceItemInMapViewHolder> {
	protected List<KakaoLocalDocument> localDocumentsList = new ArrayList<>();
	protected PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;
	protected OnClickedBottomSheetListener onClickedBottomSheetListener;
	protected FavoriteLocationQuery favoriteLocationQuery;

	protected int isVisibleSelectBtn = View.GONE;
	protected int isVisibleUnSelectBtn = View.GONE;
	protected int isVisibleFavoriteBtn = View.VISIBLE;

	private final Drawable favoriteEnabledDrawable;
	private final Drawable favoriteDisabledDrawable;
	protected final MarkerType MARKER_TYPE;

	public LocationItemViewPagerAdapter(Context context, MarkerType MARKER_TYPE) {
		favoriteEnabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_enabled);
		favoriteDisabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_disabled);
		this.MARKER_TYPE = MARKER_TYPE;
	}

	public void setFavoriteLocationQuery(FavoriteLocationQuery favoriteLocationQuery) {
		this.favoriteLocationQuery = favoriteLocationQuery;
	}

	public void setPlacesItemBottomSheetButtonOnClickListener(PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener) {
		this.placesItemBottomSheetButtonOnClickListener = placesItemBottomSheetButtonOnClickListener;
	}

	public int getItemPosition(KakaoLocalDocument kakaoLocalDocument) {
		int position = 0;

		if (kakaoLocalDocument instanceof PlaceDocuments) {
			String placeId = ((PlaceDocuments) kakaoLocalDocument).getId();

			for (KakaoLocalDocument document : localDocumentsList) {
				if (((PlaceDocuments) document).getId().equals(placeId)) {
					break;
				}
				position++;
			}
		} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
			String x = ((AddressResponseDocuments) kakaoLocalDocument).getX();
			String y = ((AddressResponseDocuments) kakaoLocalDocument).getY();

			AddressResponseDocuments addressResponseDocument = null;
			for (KakaoLocalDocument document : localDocumentsList) {
				addressResponseDocument = (AddressResponseDocuments) document;

				if (addressResponseDocument.getX().equals(x) &&
						addressResponseDocument.getY().equals(y)) {
					break;
				}
				position++;
			}
		} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
			String longitude = ((CoordToAddressDocuments) kakaoLocalDocument).getCoordToAddressAddress().getLongitude();
			String latitude = ((CoordToAddressDocuments) kakaoLocalDocument).getCoordToAddressAddress().getLatitude();

			CoordToAddressDocuments coordToAddressDocument = null;
			for (KakaoLocalDocument document : localDocumentsList) {
				coordToAddressDocument = (CoordToAddressDocuments) document;

				if (coordToAddressDocument.getCoordToAddressAddress().getLatitude().equals(latitude) &&
						coordToAddressDocument.getCoordToAddressAddress().getLongitude().equals(longitude)) {
					break;
				}
				position++;
			}
		}

		return position;
	}

	public void setIsVisibleFavoriteBtn(int isVisibleFavoriteBtn) {
		this.isVisibleFavoriteBtn = isVisibleFavoriteBtn;
	}

	public LocationItemViewPagerAdapter setVisibleSelectBtn(int visibleSelectBtn) {
		isVisibleSelectBtn = visibleSelectBtn;
		return this;
	}

	public LocationItemViewPagerAdapter setVisibleUnSelectBtn(int visibleUnSelectBtn) {
		isVisibleUnSelectBtn = visibleUnSelectBtn;
		return this;

	}

	public void setLocalDocumentsList(List<? extends KakaoLocalDocument> localDocumentsList) {
		this.localDocumentsList.addAll(localDocumentsList);
	}

	public void setOnClickedBottomSheetListener(OnClickedBottomSheetListener onClickedBottomSheetListener) {
		this.onClickedBottomSheetListener = onClickedBottomSheetListener;
	}

	@NonNull
	@Override
	public PlaceItemInMapViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		return new PlaceItemInMapViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_places_item, parent, false));
	}

	@Override
	public void onBindViewHolder(@NonNull PlaceItemInMapViewHolder holder, int position) {
		holder.bind();
	}

	public List<KakaoLocalDocument> getLocalDocumentsList() {
		return localDocumentsList;
	}

	@Override
	public int getItemCount() {
		return localDocumentsList.size();
	}

	public class PlaceItemInMapViewHolder extends RecyclerView.ViewHolder {
		protected CardviewPlacesItemBinding binding;
		protected Integer favoriteLocationId;
		protected CustomProgressView customProgressView;


		public PlaceItemInMapViewHolder(@NonNull View view) {
			super(view);
			binding = CardviewPlacesItemBinding.bind(view);
			binding.addressLayout.addressIndex.setVisibility(View.GONE);
			binding.placeItemCardviewInBottomsheet.setOnClickListener(onClickListener);

			customProgressView = binding.customProgressView;
			customProgressView.setContentView(view.findViewById(R.id.map_place_item_rows));
			customProgressView.onStartedProcessingData();

			binding.selectThisPlaceButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					placesItemBottomSheetButtonOnClickListener.onSelectedLocation(localDocumentsList.get(getBindingAdapterPosition()));
				}
			});

			binding.unselectThisPlaceButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					placesItemBottomSheetButtonOnClickListener.onRemovedLocation();
				}
			});

			binding.addToFavoritePlaceitemButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					onClickedFavoriteBtn();
				}
			});
		}

		public void bind() {
			setDataView(localDocumentsList.get(getBindingAdapterPosition()));
		}

		public void setDataView(KakaoLocalDocument kakaoLocalDocument) {
			final int position = getBindingAdapterPosition();
			String itemPosition = (position + 1) + " / " + getItemCount();
			binding.itemPosition.setText(itemPosition);

			PlaceDocuments placeDocuments = null;
			AddressResponseDocuments addressDocuments = null;
			CoordToAddressDocuments coordToAddressDocuments = null;

			final ViewHolderData viewHolderData = new ViewHolderData(kakaoLocalDocument);
			binding.placeItemCardviewInBottomsheet.setTag(viewHolderData);

			if (kakaoLocalDocument instanceof PlaceDocuments) {
				placeDocuments = (PlaceDocuments) kakaoLocalDocument;

				binding.placeLayout.placeItemName.setText(placeDocuments.getPlaceName());
				binding.placeLayout.placeItemAddress.setText(placeDocuments.getAddressName());
				binding.placeLayout.placeItemCategory.setText(placeDocuments.getCategoryName());
				binding.placeItemDistance.setText(placeDocuments.getDistance() + "m");

				binding.placeLayout.getRoot().setVisibility(View.VISIBLE);
				binding.addressLayout.getRoot().setVisibility(View.GONE);
				binding.placeItemDistance.setVisibility(View.VISIBLE);
			} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
				addressDocuments = (AddressResponseDocuments) kakaoLocalDocument;

				binding.addressLayout.addressName.setText(addressDocuments.getAddressName());
				if (addressDocuments.getAddressResponseRoadAddress() != null) {
					binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
					binding.addressLayout.anotherAddressName.setText(addressDocuments.getAddressResponseRoadAddress().getAddressName());
				} else if (addressDocuments.getAddressResponseAddress() != null) {
					binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
					binding.addressLayout.anotherAddressName.setText(addressDocuments.getAddressResponseAddress().getAddressName());
				}

				binding.placeLayout.getRoot().setVisibility(View.GONE);
				binding.addressLayout.getRoot().setVisibility(View.VISIBLE);
				binding.placeItemDistance.setVisibility(View.GONE);
			} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
				coordToAddressDocuments = (CoordToAddressDocuments) kakaoLocalDocument;

				binding.addressLayout.addressName.setText(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
				if (coordToAddressDocuments.getCoordToAddressRoadAddress() != null) {
					binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
					binding.addressLayout.anotherAddressName.setText(coordToAddressDocuments.getCoordToAddressRoadAddress().getAddressName());
				} else if (coordToAddressDocuments.getCoordToAddressAddress() != null) {
					binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
					binding.addressLayout.anotherAddressName.setText(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
				}

				binding.placeLayout.getRoot().setVisibility(View.GONE);
				binding.addressLayout.getRoot().setVisibility(View.VISIBLE);
				binding.placeItemDistance.setVisibility(View.GONE);
			}

			binding.selectThisPlaceButton.setVisibility(isVisibleSelectBtn);
			binding.unselectThisPlaceButton.setVisibility(isVisibleUnSelectBtn);
			binding.addToFavoritePlaceitemButton.setVisibility(isVisibleFavoriteBtn);

			if (isVisibleFavoriteBtn == View.VISIBLE) {
				String placeId = null;
				String latitude = null;
				String longitude = null;

				if (kakaoLocalDocument instanceof PlaceDocuments) {
					longitude = placeDocuments.getX();
					latitude = placeDocuments.getY();
					placeId = placeDocuments.getId();
				} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
					latitude = addressDocuments.getY();
					longitude = addressDocuments.getX();
				} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
					latitude = coordToAddressDocuments.getCoordToAddressAddress().getLatitude();
					longitude = coordToAddressDocuments.getCoordToAddressAddress().getLongitude();
				}

				favoriteLocationQuery.contains(placeId, latitude, longitude
						, new DbQueryCallback<FavoriteLocationDTO>() {
							@Override
							public void onResultSuccessful(FavoriteLocationDTO result) {
								favoriteLocationId = result.getId();
								binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteEnabledDrawable);
							}

							@Override
							public void onResultNoData() {
								binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteDisabledDrawable);
							}
						});
			}

			customProgressView.onSuccessfulProcessingData();
		}

		protected void onClickedFavoriteBtn() {
			FavoriteLocationDTO newFavoriteLocationDTO = new FavoriteLocationDTO();
			KakaoLocalDocument data = localDocumentsList.get(getBindingAdapterPosition());

			if (data instanceof PlaceDocuments) {
				PlaceDocuments placeDocuments = (PlaceDocuments) data;

				newFavoriteLocationDTO.setType(LocationUtil.isRestaurant(placeDocuments.getCategoryName()) ? FavoriteLocationDTO.RESTAURANT : FavoriteLocationDTO.PLACE);
				newFavoriteLocationDTO.setAddress(placeDocuments.getAddressName());
				newFavoriteLocationDTO.setLongitude(String.valueOf(placeDocuments.getX()));
				newFavoriteLocationDTO.setLatitude(String.valueOf(placeDocuments.getY()));
				newFavoriteLocationDTO.setPlaceName(placeDocuments.getPlaceName());
				newFavoriteLocationDTO.setPlaceId(placeDocuments.getId());
			} else if (data instanceof AddressResponseDocuments) {
				AddressResponseDocuments addressDocuments = (AddressResponseDocuments) data;

				newFavoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);
				newFavoriteLocationDTO.setAddress(addressDocuments.getAddressName());
				newFavoriteLocationDTO.setLatitude(String.valueOf(addressDocuments.getY()));
				newFavoriteLocationDTO.setLongitude(String.valueOf(addressDocuments.getX()));
			} else if (data instanceof CoordToAddressDocuments) {
				CoordToAddressDocuments coordToAddressDocuments = (CoordToAddressDocuments) data;

				newFavoriteLocationDTO.setType(FavoriteLocationDTO.ADDRESS);
				newFavoriteLocationDTO.setAddress(coordToAddressDocuments.getCoordToAddressAddress().getAddressName());
				newFavoriteLocationDTO.setLatitude(coordToAddressDocuments.getCoordToAddressAddress().getLatitude());
				newFavoriteLocationDTO.setLongitude(coordToAddressDocuments.getCoordToAddressAddress().getLongitude());
			}
			newFavoriteLocationDTO.setAddedDateTime(String.valueOf(System.currentTimeMillis()));

			favoriteLocationQuery.contains(newFavoriteLocationDTO.getPlaceId()
					, newFavoriteLocationDTO.getLatitude(), newFavoriteLocationDTO.getLongitude()
					, new DbQueryCallback<FavoriteLocationDTO>() {
						@Override
						public void onResultSuccessful(FavoriteLocationDTO result) {

							favoriteLocationQuery.delete(result, new DbQueryCallback<Boolean>() {
								@Override
								public void onResultSuccessful(Boolean result) {
									binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteDisabledDrawable);
								}

								@Override
								public void onResultNoData() {

								}
							});

						}

						@Override
						public void onResultNoData() {
							favoriteLocationQuery.addNewFavoriteLocation(newFavoriteLocationDTO, new DbQueryCallback<FavoriteLocationDTO>() {
								@Override
								public void onResultSuccessful(FavoriteLocationDTO addedFavoriteLocationDto) {
									favoriteLocationId = addedFavoriteLocationDto.getId();
									binding.addToFavoritePlaceitemButton.setImageDrawable(favoriteEnabledDrawable);
								}

								@Override
								public void onResultNoData() {

								}
							});
						}
					});
		}
	}

	static class ViewHolderData {
		KakaoLocalDocument kakaoLocalDocument;

		public ViewHolderData(KakaoLocalDocument kakaoLocalDocument) {
			this.kakaoLocalDocument = kakaoLocalDocument;
		}
	}

	private final View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ViewHolderData viewHolderData = (ViewHolderData) view.getTag();
			onClickedBottomSheetListener.onClickedPlaceBottomSheet(viewHolderData.kakaoLocalDocument);
		}
	};
}
