package com.zerodsoft.tripweather.ScheduleList;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.zerodsoft.tripweather.R;
import com.zerodsoft.tripweather.Room.DTO.Travel;
import com.zerodsoft.tripweather.Room.TravelScheduleThread;
import com.zerodsoft.tripweather.Utility.Actions;

import java.util.ArrayList;
import java.util.List;

public class TravelScheduleListAdapter extends RecyclerView.Adapter<TravelScheduleListAdapter.ViewHolder>
{
    private List<Travel> travelDataList = null;
    private Activity activity;
    private Context context;
    private Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Actions.FINISHED_DELETE_TRAVEL:
                    Toast.makeText(context, "삭제 완료", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
    private Handler mainActivityHandler;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewTravelName, textViewPeriod;
        ImageButton btnMore;
        LinearLayout linearLayout;
        int travelId;

        ViewHolder(View itemView)
        {
            super(itemView);

            textViewTravelName = (TextView) itemView.findViewById(R.id.text_view_travel_name);
            textViewPeriod = (TextView) itemView.findViewById(R.id.text_view_travel_period);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear_layout_travel_item);
            btnMore = (ImageButton) itemView.findViewById(R.id.btn_more);

            PopupMenu popupMenu = new PopupMenu(itemView.getContext(), itemView);
            popupMenu.setOnMenuItemClickListener(onMenuItemClickListener);
            popupMenu.inflate(R.menu.main_context_menu);

            btnMore.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    popupMenu.show();
                }
            });
        }

      /*
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
        {
            MenuItem editItem = contextMenu.add(Menu.NONE, 0, Menu.NONE, "수정");
            MenuItem removeItem = contextMenu.add(Menu.NONE, 1, Menu.NONE, "삭제");
            editItem.setOnMenuItemClickListener(onMenuItemClickListener);
            removeItem.setOnMenuItemClickListener(onMenuItemClickListener);
        }

       */

        private final PopupMenu.OnMenuItemClickListener onMenuItemClickListener = new PopupMenu.OnMenuItemClickListener()
        {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.item_edit_schedule:
                        Toast.makeText(context, "EDIT", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.item_remove_schedule:
                        TravelScheduleThread travelScheduleThread = new TravelScheduleThread(activity, travelId, Actions.DELETE_TRAVEL);
                        travelScheduleThread.setHandler(mainActivityHandler);
                        travelScheduleThread.start();
                        break;
                }
                return true;
            }
        };

        public void setTravelId(int travelId)
        {
            this.travelId = travelId;
        }

        public int getTravelId()
        {
            return travelId;
        }
    }

    public TravelScheduleListAdapter(Activity activity, List<Travel> travelDataList)
    {
        this.travelDataList = travelDataList;
        this.activity = activity;
        this.context = activity.getApplicationContext();
    }

    @NonNull
    @Override
    public TravelScheduleListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        Context context = parent.getContext();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.recycler_view_travel_item, parent, false);
        TravelScheduleListAdapter.ViewHolder viewHolder = new TravelScheduleListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TravelScheduleListAdapter.ViewHolder holder, int position)
    {
        holder.textViewTravelName.setText(travelDataList.get(position).getName());
        holder.textViewPeriod.setText(travelDataList.get(position).getPeriod());
        holder.travelId = travelDataList.get(position).getId();

        holder.linearLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                TravelScheduleThread travelScheduleThread = new TravelScheduleThread(activity, holder.travelId, Actions.CLICKED_TRAVEL_ITEM);
                travelScheduleThread.start();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return travelDataList.size();
    }


    public void setMainActivityHandler(Handler mainActivityHandler)
    {
        this.mainActivityHandler = mainActivityHandler;
    }
}
