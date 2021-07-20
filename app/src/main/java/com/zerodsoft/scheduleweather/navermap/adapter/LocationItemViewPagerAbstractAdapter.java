package com.zerodsoft.scheduleweather.navermap.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.databinding.CardviewPlacesItemBinding;
import com.zerodsoft.scheduleweather.navermap.MarkerType;
import com.zerodsoft.scheduleweather.navermap.interfaces.OnClickedBottomSheetListener;
import com.zerodsoft.scheduleweather.navermap.interfaces.PlacesItemBottomSheetButtonOnClickListener;
import com.zerodsoft.scheduleweather.navermap.util.LocationUtil;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.EmptyKakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.KakaoLocalDocument;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.addressresponse.AddressResponseDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.coordtoaddressresponse.CoordToAddressDocuments;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;
import com.zerodsoft.scheduleweather.room.interfaces.FavoriteLocationQuery;

public abstract class LocationItemViewPagerAbstractAdapter extends RecyclerView.Adapter<LocationItemViewPagerAbstractAdapter.PlaceItemInMapViewHolder> {
	private final Drawable favoriteEnabledDrawable;
	private final Drawable favoriteDisabledDrawable;
	protected final MarkerType MARKER_TYPE;

	protected PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener;
	protected OnClickedBottomSheetListener onClickedBottomSheetListener;
	protected FavoriteLocationQuery favoriteLocationQuery;
	protected Context context;

	protected int isVisibleSelectBtn = View.GONE;
	protected int isVisibleUnSelectBtn = View.GONE;
	protected int isVisibleFavoriteBtn = View.VISIBLE;

	public LocationItemViewPagerAbstractAdapter(Context context, MarkerType MARKER_TYPE) {
		this.context = context;
		this.MARKER_TYPE = MARKER_TYPE;
		favoriteEnabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_enabled);
		favoriteDisabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_disabled);
	}

	public final void setFavoriteLocationQuery(FavoriteLocationQuery favoriteLocationQuery) {
		this.favoriteLocationQuery = favoriteLocationQuery;
	}

	public final void setPlacesItemBottomSheetButtonOnClickListener(PlacesItemBottomSheetButtonOnClickListener placesItemBottomSheetButtonOnClickListener) {
		this.placesItemBottomSheetButtonOnClickListener = placesItemBottomSheetButtonOnClickListener;
	}

	public final void setIsVisibleFavoriteBtn(int isVisibleFavoriteBtn) {
		this.isVisibleFavoriteBtn = isVisibleFavoriteBtn;
	}

	public final LocationItemViewPagerAbstractAdapter setVisibleSelectBtn(int visibleSelectBtn) {
		isVisibleSelectBtn = visibleSelectBtn;
		return this;
	}

	public final LocationItemViewPagerAbstractAdapter setVisibleUnSelectBtn(int visibleUnSelectBtn) {
		isVisibleUnSelectBtn = visibleUnSelectBtn;
		return this;
	}

	public final void setOnClickedBottomSheetListener(OnClickedBottomSheetListener onClickedBottomSheetListener) {
		this.onClickedBottomSheetListener = onClickedBottomSheetListener;
	}

	abstract public int getItemsCount();

	abstract public KakaoLocalDocument getLocalItem(int position);

	abstract public int getLocalItemPosition(KakaoLocalDocument kakaoLocalDocument);

	abstract class PlaceItemInMapViewHolder extends RecyclerView.ViewHolder {
		protected CardviewPlacesItemBinding binding;
		protected Integer favoriteLocationId;

		public PlaceItemInMapViewHolder(@NonNull View view) {
			super(view);
			binding = CardviewPlacesItemBinding.bind(view);
			binding.addressLayout.addressIndex.setVisibility(View.GONE);
			binding.rootLayout.setOnClickListener(itemOnClickListener);

			binding.customProgressView.setContentView(view.findViewById(R.id.map_place_item_rows));
			binding.customProgressView.onStartedProcessingData();

			binding.selectThisPlaceButton.setVisibility(isVisibleSelectBtn);
			binding.unselectThisPlaceButton.setVisibility(isVisibleUnSelectBtn);
			binding.addToFavoritePlaceitemButton.setVisibility(isVisibleFavoriteBtn);

			binding.selectThisPlaceButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					placesItemBottomSheetButtonOnClickListener.onSelectedLocation(getKakaoLocalDocument(getBindingAdapterPosition()));
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
					onClickedFavoriteBtn(getBindingAdapterPosition());
				}
			});
		}

		abstract void bind();

		protected final void setDataView(KakaoLocalDocument kakaoLocalDocument) {
			final int position = getBindingAdapterPosition();
			String itemPosition = (position + 1) + " / " + getItemCount();
			binding.itemPosition.setText(itemPosition);

			if (kakaoLocalDocument instanceof EmptyKakaoLocalDocument) {
				binding.customProgressView.onFailedProcessingData(context.getString(R.string.error));
			} else {
				PlaceDocuments placeDocument = null;
				AddressResponseDocuments addressDocument = null;
				CoordToAddressDocuments coordToAddressDocument = null;

				final ViewHolderData viewHolderData = new ViewHolderData(kakaoLocalDocument);
				binding.rootLayout.setTag(viewHolderData);

				if (kakaoLocalDocument instanceof PlaceDocuments) {
					placeDocument = (PlaceDocuments) kakaoLocalDocument;

					binding.placeLayout.placeItemName.setText(placeDocument.getPlaceName());
					binding.placeLayout.placeItemAddress.setText(placeDocument.getAddressName());
					binding.placeLayout.placeItemCategory.setText(placeDocument.getCategoryName());

					binding.placeLayout.getRoot().setVisibility(View.VISIBLE);
					binding.addressLayout.getRoot().setVisibility(View.GONE);
				} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
					addressDocument = (AddressResponseDocuments) kakaoLocalDocument;

					binding.addressLayout.addressName.setText(addressDocument.getAddressName());
					if (addressDocument.getAddressResponseRoadAddress() != null) {
						binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
						binding.addressLayout.anotherAddressName.setText(addressDocument.getAddressResponseRoadAddress().getAddressName());
					} else if (addressDocument.getAddressResponseAddress() != null) {
						binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
						binding.addressLayout.anotherAddressName.setText(addressDocument.getAddressResponseAddress().getAddressName());
					}

					binding.placeLayout.getRoot().setVisibility(View.GONE);
					binding.addressLayout.getRoot().setVisibility(View.VISIBLE);
				} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
					coordToAddressDocument = (CoordToAddressDocuments) kakaoLocalDocument;

					binding.addressLayout.addressName.setText(coordToAddressDocument.getCoordToAddressAddress().getAddressName());
					if (coordToAddressDocument.getCoordToAddressRoadAddress() != null) {
						binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.road_addr));
						binding.addressLayout.anotherAddressName.setText(coordToAddressDocument.getCoordToAddressRoadAddress().getAddressName());
					} else if (coordToAddressDocument.getCoordToAddressAddress() != null) {
						binding.addressLayout.anotherAddressType.setText(itemView.getContext().getString(R.string.region_addr));
						binding.addressLayout.anotherAddressName.setText(coordToAddressDocument.getCoordToAddressAddress().getAddressName());
					}

					binding.placeLayout.getRoot().setVisibility(View.GONE);
					binding.addressLayout.getRoot().setVisibility(View.VISIBLE);
				}

				if (isVisibleFavoriteBtn == View.VISIBLE) {
					String placeId = null;
					String latitude = null;
					String longitude = null;

					if (kakaoLocalDocument instanceof PlaceDocuments) {
						longitude = placeDocument.getX();
						latitude = placeDocument.getY();
						placeId = placeDocument.getId();
					} else if (kakaoLocalDocument instanceof AddressResponseDocuments) {
						latitude = addressDocument.getY();
						longitude = addressDocument.getX();
					} else if (kakaoLocalDocument instanceof CoordToAddressDocuments) {
						latitude = coordToAddressDocument.getCoordToAddressAddress().getLatitude();
						longitude = coordToAddressDocument.getCoordToAddressAddress().getLongitude();
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

				binding.customProgressView.onSuccessfulProcessingData();
			}
		}

		protected final void processOnClickedFavoriteBtn(KakaoLocalDocument data) {
			FavoriteLocationDTO newFavoriteLocationDTO = new FavoriteLocationDTO();

			if (data instanceof PlaceDocuments) {
				PlaceDocuments placeDocuments = (PlaceDocuments) data;

				newFavoriteLocationDTO.setType(LocationUtil.isRestaurant(placeDocuments.getCategoryName()) ? FavoriteLocationDTO.RESTAURANT : FavoriteLocationDTO.PLACE);
				newFavoriteLocationDTO.setAddress(placeDocuments.getAddressName());
				newFavoriteLocationDTO.setLongitude(String.valueOf(placeDocuments.getX()));
				newFavoriteLocationDTO.setLatitude(String.valueOf(placeDocuments.getY()));
				newFavoriteLocationDTO.setPlaceName(placeDocuments.getPlaceName());
				newFavoriteLocationDTO.setPlaceId(placeDocuments.getId());
				newFavoriteLocationDTO.setPlaceCategoryName(placeDocuments.getCategoryName());
				newFavoriteLocationDTO.setPlaceUrl(placeDocuments.getPlaceUrl());
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

		protected final void onClickedFavoriteBtn(int position) {
			processOnClickedFavoriteBtn(getKakaoLocalDocument(position));
		}

		abstract KakaoLocalDocument getKakaoLocalDocument(int position);
	}

	static final class ViewHolderData {
		KakaoLocalDocument kakaoLocalDocument;

		public ViewHolderData(KakaoLocalDocument kakaoLocalDocument) {
			this.kakaoLocalDocument = kakaoLocalDocument;
		}
	}

	private final View.OnClickListener itemOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			ViewHolderData viewHolderData = (ViewHolderData) view.getTag();
			onClickedBottomSheetListener.onClickedPlaceBottomSheet(viewHolderData.kakaoLocalDocument);
		}
	};
}
