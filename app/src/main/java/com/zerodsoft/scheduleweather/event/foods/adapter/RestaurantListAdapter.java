package com.zerodsoft.scheduleweather.event.foods.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.zerodsoft.scheduleweather.R;
import com.zerodsoft.scheduleweather.activity.App;
import com.zerodsoft.scheduleweather.common.interfaces.OnClickedListItem;
import com.zerodsoft.scheduleweather.common.interfaces.DbQueryCallback;
import com.zerodsoft.scheduleweather.event.foods.interfaces.OnClickedFavoriteButtonListener;
import com.zerodsoft.scheduleweather.navermap.callback.PlaceItemCallback;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.KakaoPlaceJsonRoot;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.menuinfo.MenuInfo;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.kakaoplace.menuinfo.MenuItem;
import com.zerodsoft.scheduleweather.retrofit.queryresponse.map.placeresponse.PlaceDocuments;
import com.zerodsoft.scheduleweather.room.dto.FavoriteLocationDTO;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class RestaurantListAdapter extends PagedListAdapter<PlaceDocuments, RestaurantListAdapter.ItemViewHolder>
{
    private final OnClickedListItem<PlaceDocuments> onClickedListItem;
    private final OnClickedFavoriteButtonListener onClickedFavoriteButtonListener;

    private SparseArray<KakaoPlaceJsonRoot> kakaoPlacesArr = new SparseArray<>();
    private SparseArray<Bitmap> restaurantImagesArr = new SparseArray<>();
    private OnContainsRestaurantListener onContainsRestaurantListener;

    private final Drawable favoriteDisabledDrawable;
    private final Drawable favoriteEnabledDrawable;

    private Context context;

    public RestaurantListAdapter(Context context, OnContainsRestaurantListener onContainsRestaurantListener, OnClickedListItem<PlaceDocuments> onClickedListItem, OnClickedFavoriteButtonListener onClickedFavoriteButtonListener)
    {
        super(new PlaceItemCallback());
        this.context = context;
        this.onContainsRestaurantListener = onContainsRestaurantListener;
        this.onClickedListItem = onClickedListItem;
        this.onClickedFavoriteButtonListener = onClickedFavoriteButtonListener;
        favoriteDisabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_disabled);
        favoriteEnabledDrawable = ContextCompat.getDrawable(context, R.drawable.favorite_enabled);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder
    {
        private TextView restaurantName;
        private ImageView restaurantImage;
        private TextView restaurantMenuInfo;
        private TextView restaurantRating;
        private ImageView favoriteButton;
        private LinearLayout restaurantReviewLayout;

        private FavoriteLocationDTO favoriteLocationDTO;

        public ItemViewHolder(View view)
        {
            super(view);

            restaurantName = (TextView) view.findViewById(R.id.restaurant_name);
            restaurantImage = (ImageView) view.findViewById(R.id.restaurant_image);
            restaurantRating = (TextView) view.findViewById(R.id.restaurant_rating);
            favoriteButton = (ImageView) view.findViewById(R.id.favorite_button);
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

            /*
            if (kakaoPlacesArr.get(getBindingAdapterPosition()) == null)
            {
                KakaoPlace.getKakaoPlaceData(item.getId(), new CarrierMessagingService.ResultCallback<DataWrapper<KakaoPlaceJsonRoot>>()
                {
                    @Override
                    public void onReceiveResult(@NonNull DataWrapper<KakaoPlaceJsonRoot> kakaoPlaceJsonRootDataWrapper) throws RemoteException
                    {
                        KakaoPlaceJsonRoot kakaoPlaceJsonRoot = kakaoPlaceJsonRootDataWrapper.getData();
                        kakaoPlacesArr.put(getBindingAdapterPosition(), kakaoPlaceJsonRoot);

                        setData(kakaoPlaceJsonRoot);
                    }
                });
            } else
            {
                setData(kakaoPlacesArr.get(getBindingAdapterPosition()));
            }

             */

            itemView.getRootView().setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedListItem.onClickedListItem(getItem(getBindingAdapterPosition()), getBindingAdapterPosition());
                }
            });

            favoriteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    onClickedFavoriteButtonListener.onClickedFavoriteButton(item, favoriteLocationDTO, getBindingAdapterPosition());
                }
            });

            onContainsRestaurantListener.contains(item.getId(), new DbQueryCallback<FavoriteLocationDTO>()
            {
                @Override
                public void onResultSuccessful(FavoriteLocationDTO result)
                {
                    if (result != null)
                    {
                        favoriteLocationDTO = result;
                        favoriteButton.setImageDrawable(favoriteEnabledDrawable);
                    } else
                    {
                        favoriteButton.setImageDrawable(favoriteDisabledDrawable);
                    }

                }

                @Override
                public void onResultNoData()
                {

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
            /*
            context.runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    restaurantMenuInfo.setText(menuStr.toString());
                    restaurantRating.setText(finalRating);
                }
            });

             */
        }


        public void setRestaurantImage(KakaoPlaceJsonRoot kakaoPlaceJsonRoot)
        {
            if (kakaoPlaceJsonRoot.getBasicInfo().getMainPhotoUrl() != null)
            {
                if (restaurantImagesArr.get(getBindingAdapterPosition()) == null)
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
                                Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream(), null, options);

                                restaurantImagesArr.put(getBindingAdapterPosition(), bmp);

                                /*
                                activity.runOnUiThread(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        restaurantImage.setImageBitmap(bmp);

                                        Glide.with(itemView)
                                                .load(restaurantImagesArr.get(getBindingAdapterPosition())).circleCrop()
                                                .into(restaurantImage);
                                    }
                                });

                                 */
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
                    restaurantImage.setImageBitmap(restaurantImagesArr.get(getBindingAdapterPosition()));
                    Glide.with(itemView)
                            .load(restaurantImagesArr.get(getBindingAdapterPosition())).circleCrop()
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

            favoriteButton.setImageDrawable(favoriteDisabledDrawable);

            /*
            Glide.with(itemView)
                    .load(context.getDrawable(R.drawable.not_image)).circleCrop()
                    .into(restaurantImage);

             */
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

    public interface OnContainsRestaurantListener
    {
        void contains(String placeId, DbQueryCallback<FavoriteLocationDTO> callback);
    }
}
