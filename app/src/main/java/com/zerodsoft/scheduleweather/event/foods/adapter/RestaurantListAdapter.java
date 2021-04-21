package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.RemoteException;
import android.service.carrier.CarrierMessagingService;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedRestaurantItem;
import com.zerodsoft.scheduleweather.kakaomap.callback.PlaceItemCallback;
import com.zerodsoft.scheduleweather.kakaoplace.KakaoPlace;
import com.zerodsoft.scheduleweather.retrofit.DataWrapper;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.KakaoPlaceJsonRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.menuinfo.MenuInfo;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.menuinfo.MenuItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RestaurantListAdapter extends PagedListAdapter<PlaceDocuments, RestaurantListAdapter.ItemViewHolder>
{
    private final OnClickedRestaurantItem onClickedRestaurantItem;

    private SparseArray<KakaoPlaceJsonRoot> kakaoPlacesArr = new SparseArray<>();
    private SparseArray<Bitmap> restaurantImagesArr = new SparseArray<>();

    private Activity activity;
    private Context context;

    public RestaurantListAdapter(Activity activity, OnClickedRestaurantItem onClickedRestaurantItem)
    {
        super(new PlaceItemCallback());
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.onClickedRestaurantItem = onClickedRestaurantItem;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView restaurantName;
        private ImageView restaurantImage;
        private TextView restaurantMenuInfo;
        private TextView restaurantRating;
        private LinearLayout restaurantReviewLayout;

        public ItemViewHolder(View view)
        {
            super(view);

            restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
            restaurantImage = (ImageView) view.findViewById(R.id.restaurant_image);
            restaurantRating = (TextView) view.findViewById(R.id.restaurant_rating);
            restaurantReviewLayout = (LinearLayout) view.findViewById(R.id.restaurant_review_layout);
            restaurantMenuInfo = (TextView) view.findViewById(R.id.restaurant_menuinfo);
            restaurantMenuInfo.setSelected(true);

            restaurantName.setText("");
            restaurantRating.setText("");
            restaurantMenuInfo.setText("");
        }

        public void bind(PlaceDocuments item)
        {
            restaurantName.setText(item.getPlaceName());

            if (kakaoPlacesArr.get(getAdapterPosition()) == null)
            {
                KakaoPlace.getKakaoPlaceData(item.getId(), new CarrierMessagingService.ResultCallback<DataWrapper<KakaoPlaceJsonRoot>>()
                {
                    @Override
                    public void onReceiveResult(@NonNull DataWrapper<KakaoPlaceJsonRoot> kakaoPlaceJsonRootDataWrapper) throws RemoteException
                    {
                        KakaoPlaceJsonRoot kakaoPlaceJsonRoot = kakaoPlaceJsonRootDataWrapper.getData();
                        kakaoPlacesArr.put(getAdapterPosition(), kakaoPlaceJsonRoot);

                        setData(kakaoPlaceJsonRoot);
                    }
                });
            } else
            {
                setData(kakaoPlacesArr.get(getAdapterPosition()));
            }

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedRestaurantItem.onClickedRestaurantItem(getItem(getAdapterPosition()));
                }
            });
        }

        public void setData(KakaoPlaceJsonRoot kakaoPlaceJsonRoot)
        {
            setRestaurantImage(kakaoPlaceJsonRoot);
            StringBuffer menuStr = new StringBuffer();

            if (kakaoPlaceJsonRoot.getMenuInfo() != null)
            {
                MenuInfo menuInfo = kakaoPlaceJsonRoot.getMenuInfo();
                List<MenuItem> menuItems = menuInfo.getMenuList();

                for (MenuItem menuItem : menuItems)
                {
                    menuStr.append(menuItem.getMenu()).append(", ");
                }

                if (menuStr.length() >= 1)
                {
                    menuStr.delete(menuStr.length() - 2, menuStr.length());
                }
            }

            String rating = "";

            if (kakaoPlaceJsonRoot.getBasicInfo().getFeedback() != null)
            {
                kakaoPlaceJsonRoot.getBasicInfo().getFeedback().setScoreAvg();
                if (kakaoPlaceJsonRoot.getBasicInfo().getFeedback().getScoreAvg() != null)
                {
                    rating = kakaoPlaceJsonRoot.getBasicInfo().getFeedback().getScoreAvg() + " / 5";
                }
            }

            String finalRating = rating;
            activity.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    restaurantMenuInfo.setText(menuStr.toString());
                    restaurantRating.setText(finalRating);
                }
            });
        }


        public void setRestaurantImage(KakaoPlaceJsonRoot kakaoPlaceJsonRoot)
        {
            if (kakaoPlaceJsonRoot.getBasicInfo().getMainPhotoUrl() != null)
            {
                if (restaurantImagesArr.get(getAdapterPosition()) == null)
                {
                    App.executorService.execute(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            try
                            {
                                String img_url = kakaoPlaceJsonRoot.getBasicInfo().getMainPhotoUrl(); //url of the image
                                URL url = new URL(img_url);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inSampleSize = 3;
                                Bitmap bmp =  BitmapFactory.decodeStream(url.openConnection().getInputStream(),null,options);

                                restaurantImagesArr.put(getAdapterPosition(), bmp);

                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        restaurantImage.setImageBitmap(bmp);

                                        Glide.with(itemView)
                                                .load(restaurantImagesArr.get(getAdapterPosition())).circleCrop()
                                                .into(restaurantImage);
                                    }
                                });
                            } catch (MalformedURLException e)
                            {
                                e.printStackTrace();
                            } catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    });
                } else
                {
                    restaurantImage.setImageBitmap(restaurantImagesArr.get(getAdapterPosition()));
                    Glide.with(itemView)
                            .load(restaurantImagesArr.get(getAdapterPosition())).circleCrop()
                            .into(restaurantImage);
                }
            } else
            {
                Glide.with(itemView)
                        .load(context.getDrawable(R.drawable.not_image)).circleCrop()
                        .into(restaurantImage);
            }
        }

        public void clearData()
        {
            restaurantName.setText("");
            restaurantMenuInfo.setText("");
            restaurantRating.setText("");
            Glide.with(itemView)
                    .load(context.getDrawable(R.drawable.not_image)).circleCrop()
                    .into(restaurantImage);
        }
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurant_itemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position)
    {
        holder.bind(getItem(position));
    }

    @Override
    public void onViewRecycled(@NonNull ItemViewHolder holder)
    {
        super.onViewRecycled(holder);
        holder.clearData();
    }
}
