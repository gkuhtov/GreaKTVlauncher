package com.gerak.tvlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavAppsAdapter extends RecyclerView.Adapter<FavAppsAdapter.ViewHolder> {

    public interface OnFavInteractionListener {
        void onFavFocused(AppModel app);
        void onFavClicked(AppModel app);
    }

    private final Context context;
    private final List<AppModel> apps;
    private final OnFavInteractionListener listener;

    public FavAppsAdapter(Context context, List<AppModel> apps, OnFavInteractionListener listener) {
        this.context = context;
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_aero_fav, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = apps.get(position);
        holder.ivIcon.setImageDrawable(app.getIcon());
        holder.tvTitle.setText(app.getLabel());

        holder.container.setOnClickListener(v -> {
            if (listener != null) listener.onFavClicked(app);
        });

        holder.container.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.08f).scaleY(1.08f).translationZ(8f).setDuration(120).start();
                holder.tvTitle.setVisibility(View.VISIBLE);
                if (listener != null) listener.onFavFocused(app);
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(120).start();
                holder.tvTitle.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout container;
        ImageView ivIcon;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.card_fav_container);
            ivIcon = itemView.findViewById(R.id.iv_fav_icon);
            tvTitle = itemView.findViewById(R.id.tv_fav_active_title);
        }
    }
}
