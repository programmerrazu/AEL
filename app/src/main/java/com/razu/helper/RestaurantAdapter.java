package com.razu.helper;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.razu.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantInfoHolder> {

    private Context context;
    private List<RestaurantInfo> restaurantInfoList;
    private OnItemClickListener clickListener;

    public RestaurantAdapter(Context context, List<RestaurantInfo> restaurantInfoList, OnItemClickListener clickListener) {
        this.context = context;
        this.restaurantInfoList = restaurantInfoList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public RestaurantInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_restaurant, parent, false);
        return new RestaurantInfoHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantInfoHolder holder, int position) {
        if (restaurantInfoList.size() != 0) {
            holder.tvRestaurantName.setText(restaurantInfoList.get(position).getName());
            holder.tvRestaurantSlogan.setText(restaurantInfoList.get(position).getSlogan());
            Glide.with(context)
                    .load(restaurantInfoList.get(position).getImage())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .fitCenter())
                    .into(holder.ivRestaurantImage);
            Glide.with(context)
                    .load(restaurantInfoList.get(position).getIcon())
                    .apply(new RequestOptions()
                            .centerCrop()
                            .fitCenter())
                    .into(holder.civRestaurantIcon);
            if (restaurantInfoList.get(position).getStatus()) {
                holder.ivRestaurantStatus.setImageResource(android.R.drawable.presence_online);
            } else {
                holder.ivRestaurantStatus.setImageResource(android.R.drawable.presence_offline);
            }
        }
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RestaurantInfoHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RestaurantInfoHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return restaurantInfoList.size();
    }

    class RestaurantInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView ivRestaurantImage, ivRestaurantStatus;
        private CircleImageView civRestaurantIcon;
        private TextView tvRestaurantName, tvRestaurantSlogan;

        RestaurantInfoHolder(View view) {
            super(view);
            ivRestaurantImage = (ImageView) view.findViewById(R.id.iv_restaurant_image);
            civRestaurantIcon = (CircleImageView) view.findViewById(R.id.civ_restaurant_icon);
            ivRestaurantStatus = (ImageView) view.findViewById(R.id.iv_restaurant_status);
            tvRestaurantName = (TextView) view.findViewById(R.id.tv_restaurant_name);
            tvRestaurantSlogan = (TextView) view.findViewById(R.id.tv_restaurant_slogan);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.onClick(v, getAdapterPosition());
            }
        }
    }
}