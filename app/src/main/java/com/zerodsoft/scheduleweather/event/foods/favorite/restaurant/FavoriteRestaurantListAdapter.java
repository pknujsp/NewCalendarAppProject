package com.zerodsoft.scheduleweather.event.foods.favorite.restaurant;

import android.content.Context;
import android.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.ICalendarCheckBox;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.util.List;

class FavoriteRestaurantListAdapter extends BaseExpandableListAdapter {
	private Context context;
	private ArrayMap<String, List<PlaceDocuments>> restaurantListMap = new ArrayMap<>();
	private LayoutInflater layoutInflater;

	private GroupViewHolder groupViewHolder;
	private ChildViewHolder childViewHolder;
	private OnClickedListItem<PlaceDocuments> onClickedListItem;
	private OnClickedFavoriteButtonListener onClickedFavoriteButtonListener;

	public FavoriteRestaurantListAdapter(Context context, OnClickedFavoriteButtonListener onClickedFavoriteButtonListener, OnClickedListItem<PlaceDocuments> onClickedListItem
			, ArrayMap<String, List<PlaceDocuments>> restaurantListMap) {
		this.context = context;
		this.onClickedFavoriteButtonListener = onClickedFavoriteButtonListener;
		this.onClickedListItem = onClickedListItem;
		this.restaurantListMap = restaurantListMap;
		this.layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void setRestaurantListMap(ArrayMap<String, List<PlaceDocuments>> restaurantListMap) {
		this.restaurantListMap = restaurantListMap;
	}

	public ArrayMap<String, List<PlaceDocuments>> getRestaurantListMap() {
		return restaurantListMap;
	}

	@Override
	public int getGroupCount() {
		return restaurantListMap.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return restaurantListMap.get(restaurantListMap.keyAt(groupPosition)).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return restaurantListMap.get(restaurantListMap.keyAt(groupPosition));
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return restaurantListMap.get(restaurantListMap.keyAt(groupPosition)).get(childPosition);
	}

	@Override
	public long getGroupId(int i) {
		return i;
	}

	@Override
	public long getChildId(int i, int i1) {
		return i1;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
		if (view == null) {
			view = layoutInflater.inflate(R.layout.expandablelist_group_view, null);

			groupViewHolder = new GroupViewHolder();
			groupViewHolder.foodMenuName = (TextView) view.findViewById(R.id.group_name);

			view.setTag(groupViewHolder);
		} else {
			groupViewHolder = (GroupViewHolder) view.getTag();
		}

		groupViewHolder.foodMenuName.setText(restaurantListMap.keyAt(i) + ", " + restaurantListMap.get(restaurantListMap.keyAt(i)).size());
		return view;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
		if (view == null) {
			view = layoutInflater.inflate(R.layout.restaurant_itemview, null);
			view.setClickable(true);

			childViewHolder = new ChildViewHolder();
			childViewHolder.restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
			childViewHolder.restaurantImage = (ImageView) view.findViewById(R.id.restaurant_image);
			childViewHolder.restaurantRating = (TextView) view.findViewById(R.id.restaurant_rating);
			childViewHolder.favoriteButton = (ImageView) view.findViewById(R.id.favorite_button);
			childViewHolder.restaurantReviewLayout = (LinearLayout) view.findViewById(R.id.restaurant_review_layout);
			childViewHolder.restaurantMenuInfo = (TextView) view.findViewById(R.id.restaurant_menuinfo);

			childViewHolder.restaurantMenuInfo.setSelected(true);
			childViewHolder.restaurantName.setText("");
			childViewHolder.restaurantRating.setText("");
			childViewHolder.restaurantMenuInfo.setText("");

			view.setTag(R.layout.restaurant_itemview, childViewHolder);
		} else {
			childViewHolder = (ChildViewHolder) view.getTag(R.layout.restaurant_itemview);
		}

		childViewHolder.restaurantName.setText(restaurantListMap.get(restaurantListMap.keyAt(groupPosition)).get(childPosition).getPlaceName());
		childViewHolder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.favorite_enabled_icon));

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickedListItem.onClickedListItem(restaurantListMap.get(restaurantListMap.keyAt(groupPosition)).get(childPosition), childPosition);
			}
		});

		childViewHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				onClickedFavoriteButtonListener.onClickedFavoriteButton(restaurantListMap.get(restaurantListMap.keyAt(groupPosition)).get(childPosition), null,
						groupPosition, childPosition);
			}
		});

		return view;
	}

	@Override
	public boolean isChildSelectable(int i, int i1) {
		return true;
	}

	static class GroupViewHolder {
		TextView foodMenuName;
	}

	static class ChildViewHolder {
		TextView restaurantName;
		ImageView restaurantImage;
		TextView restaurantMenuInfo;
		TextView restaurantRating;
		ImageView favoriteButton;
		LinearLayout restaurantReviewLayout;
	}
}
