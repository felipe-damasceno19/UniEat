package com.example.unieat.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.unieat.R;
import com.example.unieat.model.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    public interface OnBannerClickListener {
        void onClick(Banner banner);
    }

    private final Context context;
    private final List<Banner> banners;
    private final OnBannerClickListener listener;

    public BannerAdapter(Context context, List<Banner> banners, OnBannerClickListener listener) {
        this.context  = context;
        this.banners  = banners;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);

        holder.tvTag.setText(banner.getTag());
        holder.tvTitle.setText(banner.getTitle());
        holder.tvSubtitle.setText(banner.getSubtitle());

        if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.viewOverlay.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(banner.getImageUrl())
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setVisibility(View.GONE);
            holder.viewOverlay.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onClick(banner));
    }

    @Override
    public int getItemCount() { return banners.size(); }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        View viewOverlay;
        TextView tvTag, tvTitle, tvSubtitle;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage     = itemView.findViewById(R.id.ivBannerImage);
            viewOverlay = itemView.findViewById(R.id.viewOverlay);
            tvTag       = itemView.findViewById(R.id.tvBannerTag);
            tvTitle     = itemView.findViewById(R.id.tvBannerTitle);
            tvSubtitle  = itemView.findViewById(R.id.tvBannerSubtitle);
        }
    }
}
