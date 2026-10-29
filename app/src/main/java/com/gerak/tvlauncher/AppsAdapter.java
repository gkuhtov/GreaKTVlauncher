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

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {

    public interface OnAppClickListener {
        void onAppClick(AppModel app);
    }

    private final Context context;
    private final List<AppModel> apps;
    private final OnAppClickListener listener;

    public AppsAdapter(Context context, List<AppModel> apps, OnAppClickListener listener) {
        this.context = context;
        this.apps = apps;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_app_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = apps.get(position);
        holder.tvTitle.setText(app.getLabel());
        holder.ivIcon.setImageDrawable(app.getIcon());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAppClick(app);
            }
        });

        // Плавный зум карточки при наведении с пульта
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.10f).scaleY(1.10f).translationZ(10f).setDuration(160).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(160).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps != null ? apps.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_app_icon);
            tvTitle = itemView.findViewById(R.id.tv_app_label);
        }
    }
}
