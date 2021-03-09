package com.zerodsoft.scheduleweather.calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.CalendarContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.calendarview.interfaces.OnEventItemLongClickListener;

import lombok.SneakyThrows;

public abstract class CommonPopupMenu
{
    public CommonPopupMenu()
    {

    }

    public void createInstancePopupMenu(ContentValues instance, Activity activity, View anchorView, int gravity)
    {
        Context context = activity.getApplicationContext();
        PopupMenu popupMenu = new PopupMenu(context, anchorView, gravity);

        popupMenu.getMenuInflater().inflate(R.menu.edit_instance_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemClick(MenuItem menuItem)
            {
                switch (menuItem.getItemId())
                {
                    case R.id.edit_instance:
                    {
                        //인스턴스 수정 액티비티 실행
                        /*
                        Intent intent = new Intent(EventActivity.this, EditEventActivity.class);
                        intent.putExtra("requestCode", EventDataController.MODIFY_EVENT);
                        intent.putExtra("calendarId", calendarId.intValue());
                        intent.putExtra("eventId", eventId.longValue());
                        startActivity(intent);
                         */
                        Toast.makeText(activity, "작성 중", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case R.id.delete_instance:
                    {
                        //인스턴스 수정 다이얼로그 표시
                        //이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                        /*
                        반복없는 이벤트 인 경우 : 일정 삭제
                       반복있는 이벤트 인 경우 : 이번 일정만 삭제, 향후 모든 일정 삭제, 모든 일정 삭제
                          */

                        String[] items = null;

                        if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
                        {
                            items = new String[]{context.getString(R.string.remove_this_instance), context.getString(R.string.remove_all_future_instance_including_current_instance)
                                    , context.getString(R.string.remove_event)};
                        } else
                        {
                            items = new String[]{context.getString(R.string.remove_event)};
                        }
                        new MaterialAlertDialogBuilder(activity).setTitle(context.getString(R.string.remove_event))
                                .setItems(items, new DialogInterface.OnClickListener()
                                {
                                    @SneakyThrows
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int index)
                                    {
                                        if (instance.getAsString(CalendarContract.Instances.RRULE) != null)
                                        {
                                            switch (index)
                                            {
                                                case 0:
                                                    // 이번 일정만 삭제
                                                    // 완성
                                                    onExceptedInstance(CalendarInstanceUtil.exceptThisInstance(instance));
                                                    break;
                                                case 1:
                                                    // 향후 모든 일정만 삭제
                                                    // deleteSubsequentIncludingThis();
                                                    Toast.makeText(activity, "작성 중", Toast.LENGTH_SHORT).show();
                                                    break;
                                                case 2:
                                                    // 모든 일정 삭제
                                                    onDeletedInstance(CalendarInstanceUtil.deleteEvent(instance));
                                                    break;
                                            }
                                        } else
                                        {
                                            switch (index)
                                            {
                                                case 0:
                                                    // 모든 일정 삭제
                                                    onDeletedInstance(CalendarInstanceUtil.deleteEvent(instance));
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

    public void onExceptedInstance(boolean isSuccessful)
    {

    }

    public void onDeletedInstance(boolean isSuccessful)
    {

    }
}
