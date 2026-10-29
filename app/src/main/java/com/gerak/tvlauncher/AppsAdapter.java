package com.gerak.tvlauncher;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.ViewHolder> {
    private final Context context;
    private final List<AppModel> apps;

    public AppsAdapter(Context context, List<AppModel> apps) {
        this.context = context;
        this.apps = apps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_app, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppModel app = apps.get(position);
        holder.appName.setText(app.label);
        holder.appIcon.setImageDrawable(app.icon);

        holder.itemView.setOnClickListener(v -> {
            Intent launch = context.getPackageManager().getLaunchIntentForPackage(app.packageName);
            if (launch != null) context.startActivity(launch);
        });

        // Плавная анимация фокуса в стиле Arc / tvOS
        holder.itemView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.12f).scaleY(1.12f).translationZ(12f).setDuration(180)
                        .setInterpolator(new DecelerateInterpolator()).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(160)
                        .setInterpolator(new DecelerateInterpolator()).start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIcon;
        TextView appName;

        public ViewHolder(View itemView) {
            super(itemView);
            appIcon = itemView.findViewById(R.id.appIcon);
            appName = itemView.findViewById(R.id.appName);
        }
    }
}
