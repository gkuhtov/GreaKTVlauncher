package com.gerak.tvlauncher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GridAppsAdapter extends RecyclerView.Adapter<GridAppsAdapter.ViewHolder> {

    public interface OnAppInteractionListener {
        void onAppClick(AppModel app);
        void onAppLongClick(AppModel app);
    }

    private final Context context;
    private final List<AppModel> apps;
    private final OnAppInteractionListener listener;

    public GridAppsAdapter(Context context, List<AppModel> apps, OnAppInteractionListener listener) {
        this.context = context;
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_aero_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = apps.get(position);
        holder.tvLabel.setText(app.getLabel());
        holder.ivIcon.setImageDrawable(app.getIcon());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onAppClick(app);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onAppLongClick(app);
            return true;
        });

        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.08f).scaleY(1.08f).translationZ(10f).setDuration(140).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(140).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvLabel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_grid_icon);
            tvLabel = itemView.findViewById(R.id.tv_grid_label);
        }
    }
}
