package com.zerodsoft.scheduleweather.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Instances;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.ModifyInstanceFragment;
import com.zerodsoft.scheduleweather.activity.editevent.interfaces.OnEditEventResultListener;

public abstract class EditEventPopupMenu {

	public PopupMenu createEditEventPopupMenu(ContentValues instance, Activity activity, View anchorView, int gravity,
	                                          @NonNull OnEditedEventCallback onEditedEventCallback,
	                                          OnEditEventResultListener onEditEventResultListener) {
		Context context = activity.getApplicationContext();
		PopupMenu popupMenu = new PopupMenu(context, anchorView, gravity);

		popupMenu.getMenuInflater().inflate(R.menu.edit_instance_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.edit_instance: {
						ModifyInstanceFragment modifyInstanceFragment = new ModifyInstanceFragment(onEditEventResultListener);
						Bundle bundle = new Bundle();

						bundle.putLong(Instances.EVENT_ID, instance.getAsLong(Instances.EVENT_ID));
						bundle.putLong(Instances._ID, instance.getAsLong(Instances._ID));
						bundle.putLong(Instances.BEGIN, instance.getAsLong(Instances.BEGIN));
						bundle.putLong(Instances.END, instance.getAsLong(Instances.END));

						modifyInstanceFragment.setArguments(bundle);
						onClickedEditEvent(modifyInstanceFragment);
					}
					break;

					case R.id.delete_instance: {
						showRemoveDialog(activity, instance, onEditedEventCallback, onEditEventResultListener);
					}
					break;
				}
				return true;
			}
		});

		popupMenu.show();
		return popupMenu;
	}

	public void showRemoveDialog(Activity activity, ContentValues instance, OnEditedEventCallback onEditedEventCallback,
	                             OnEditEventResultListener onEditEventResultListener) {
		final int REMOVE_ALL_EVENTS = 0;
		final int REMOVE_FOLLOWING_EVENTS = 1;
		final int REMOVE_ONLY_THIS_EVENT = 2;

		String[] dialogItems = null;
		int[] dialogItemsIndexArr = null;

		Context context = activity.getApplicationContext();
		//이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                /*
                반복없는 이벤트 인 경우 : 일정 삭제
                반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                 */
		if (instance.getAsString(Instances.RRULE) != null) {
			dialogItems = new String[]{context.getString(R.string.remove_this_instance),
					context.getString(R.string.remove_following_events)
					, context.getString(R.string.remove_event)};
			dialogItemsIndexArr = new int[]{REMOVE_ONLY_THIS_EVENT, REMOVE_FOLLOWING_EVENTS, REMOVE_ALL_EVENTS};
		} else {
			dialogItems = new String[]{context.getString(R.string.remove_event)};
			dialogItemsIndexArr = new int[]{REMOVE_ALL_EVENTS};
		}

		final int[] finalDialogItemsIndexArr = dialogItemsIndexArr;
		final int[] clickedTypeArr = new int[]{-1};

		new MaterialAlertDialogBuilder(activity).setTitle(R.string.remove_event)
				.setCancelable(false)
				.setSingleChoiceItems(dialogItems, -1, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int index) {
						clickedTypeArr[0] = finalDialogItemsIndexArr[index];
					}
				})
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int index) {
						if (clickedTypeArr[0] == -1) {
							Toast.makeText(activity, R.string.not_choiced_item, Toast.LENGTH_SHORT).show();
							return;
						}
						EventHelper eventHelper = new EventHelper(new AsyncQueryService(activity.getApplicationContext(), onEditEventResultListener));

						switch (clickedTypeArr[0]) {
							case REMOVE_ONLY_THIS_EVENT:
								// 이번 일정만 삭제
								eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_ONLY_THIS_EVENT, instance);
								break;
							case REMOVE_FOLLOWING_EVENTS:
								// 향후 모든 일정만 삭제
								eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_FOLLOWING_EVENTS, instance);
								break;
							case REMOVE_ALL_EVENTS:
								// 모든 일정 삭제
								eventHelper.removeEvent(EventHelper.EventEditType.REMOVE_ALL_EVENTS, instance);
								break;
						}
						onEditedEventCallback.onRemoved();
					}
				}).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

			}
		}).create().show();
	}

	public abstract void onClickedEditEvent(Fragment modificationFragment);

	public interface OnEditedEventCallback {
		void onRemoved();
	}
}
