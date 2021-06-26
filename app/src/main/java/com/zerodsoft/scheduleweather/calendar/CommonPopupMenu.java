package com.zerodsoft.scheduleweather.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.editevent.activity.ModifyInstanceFragment;
import com.zerodsoft.scheduleweather.event.common.viewmodel.LocationViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationHistoryViewModel;
import com.zerodsoft.scheduleweather.event.foods.viewmodel.FoodCriteriaLocationInfoViewModel;

import lombok.SneakyThrows;

public abstract class CommonPopupMenu {
	public CommonPopupMenu() {

	}

	public void createInstancePopupMenu(ContentValues instance, Activity activity, View anchorView, int gravity,
	                                    CalendarViewModel calendarViewModel, LocationViewModel locationViewModel, FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel,
	                                    FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel) {
		Context context = activity.getApplicationContext();
		PopupMenu popupMenu = new PopupMenu(context, anchorView, gravity);

		popupMenu.getMenuInflater().inflate(R.menu.edit_instance_menu, popupMenu.getMenu());
		popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@SuppressLint("NonConstantResourceId")
			@Override
			public boolean onMenuItemClick(MenuItem menuItem) {
				switch (menuItem.getItemId()) {
					case R.id.edit_instance: {
						ModifyInstanceFragment modifyInstanceFragment = new ModifyInstanceFragment(new ModifyInstanceFragment.OnModifyInstanceResultListener() {
							@Override
							public void onResultModifiedEvent(long begin) {

							}

							@Override
							public void onResultModifiedThisInstance(long eventId, long begin) {

							}

							@Override
							public void onResultModifiedAfterAllInstancesIncludingThisInstance(long eventId, long begin) {

							}
						});
						Bundle bundle = new Bundle();

						bundle.putLong(CalendarContract.Instances.EVENT_ID, instance.getAsLong(CalendarContract.Instances.EVENT_ID));
						bundle.putLong(CalendarContract.Instances._ID, instance.getAsLong(CalendarContract.Instances._ID));
						bundle.putLong(CalendarContract.Instances.BEGIN, instance.getAsLong(CalendarContract.Instances.BEGIN));
						bundle.putLong(CalendarContract.Instances.END, instance.getAsLong(CalendarContract.Instances.END));

						modifyInstanceFragment.setArguments(bundle);

						break;
					}
					case R.id.delete_instance: {
						//인스턴스 수정 다이얼로그 표시
						//이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                        /*
                        반복없는 이벤트 인 경우 : 일정 삭제
                       반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                          */

						String[] items = null;

						if (instance.getAsString(CalendarContract.Instances.RRULE) != null) {
							items = new String[]{context.getString(R.string.remove_this_instance), context.getString(R.string.remove_all_future_instance_including_current_instance)
									, context.getString(R.string.remove_event)};
						} else {
							items = new String[]{context.getString(R.string.remove_event)};
						}
						new MaterialAlertDialogBuilder(activity).setTitle(context.getString(R.string.remove_event))
								.setItems(items, new DialogInterface.OnClickListener() {
									@SneakyThrows
									@Override
									public void onClick(DialogInterface dialogInterface, int index) {
										if (instance.getAsString(CalendarContract.Instances.RRULE) != null) {
											switch (index) {
												case 0:
													// 이번 일정만 삭제
													// 완성
													showExceptThisInstanceDialog(activity, calendarViewModel, instance.getAsLong(CalendarContract.Instances.BEGIN)
															, instance.getAsLong(CalendarContract.Instances.EVENT_ID));
													break;
												case 1:
													// 향후 모든 일정만 삭제
													// deleteSubsequentIncludingThis();
													Toast.makeText(activity, "작성 중", Toast.LENGTH_SHORT).show();
													break;
												case 2:
													// 모든 일정 삭제
													showDeleteEventDialog(activity, calendarViewModel, locationViewModel
															, foodCriteriaLocationInfoViewModel, foodCriteriaLocationHistoryViewModel
															, instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
															, instance.getAsLong(CalendarContract.Instances.EVENT_ID));
													break;
											}
										} else {
											switch (index) {
												case 0:
													// 모든 일정 삭제
													showDeleteEventDialog(activity, calendarViewModel, locationViewModel
															, foodCriteriaLocationInfoViewModel, foodCriteriaLocationHistoryViewModel
															, instance.getAsInteger(CalendarContract.Instances.CALENDAR_ID)
															, instance.getAsLong(CalendarContract.Instances.EVENT_ID));
													break;
											}
										}
									}
								}).create().show();
						break;
					}
				}
				return true;
			}
		});

		popupMenu.show();
	}

	public abstract void onExceptedInstance(boolean isSuccessful);

	public abstract void onDeletedEvent(boolean isSuccessful);


	private void showDeleteEventDialog(Activity activity, CalendarViewModel calendarViewModel, LocationViewModel locationViewModel
			, FoodCriteriaLocationInfoViewModel foodCriteriaLocationInfoViewModel
			, FoodCriteriaLocationHistoryViewModel foodCriteriaLocationHistoryViewModel, final int CALENDAR_ID
			, final long EVENT_ID) {
		new MaterialAlertDialogBuilder(activity)
				.setTitle(R.string.remove_event)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CalendarInstanceUtil.deleteEvent(calendarViewModel, locationViewModel
								, foodCriteriaLocationInfoViewModel, foodCriteriaLocationHistoryViewModel, EVENT_ID);
						onDeletedEvent(true);
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}

	private void showExceptThisInstanceDialog(Activity activity, CalendarViewModel calendarViewModel, final long BEGIN, final long EVENT_ID) {
		new MaterialAlertDialogBuilder(activity)
				.setTitle(R.string.remove_this_instance)
				.setPositiveButton(R.string.check, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						CalendarInstanceUtil.exceptThisInstance(calendarViewModel, BEGIN, EVENT_ID);
						onExceptedInstance(true);
						dialog.dismiss();
					}
				})
				.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create().show();
	}
}
