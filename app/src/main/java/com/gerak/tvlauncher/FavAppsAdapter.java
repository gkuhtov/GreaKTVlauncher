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

import com.gerak.tvlauncher.focus.FocusEngine;

import java.util.List;

public class FavAppsAdapter extends RecyclerView.Adapter<FavAppsAdapter.ViewHolder> {

    public interface OnFavInteractionListener {
        void onFavFocused(AppModel app);
        void onFavClicked(AppModel app);
    }

    private final Context context;
    private final List<AppModel> apps;
    private final OnFavInteractionListener listener;
    private final FocusEngine focusEngine;

    public FavAppsAdapter(Context context, List<AppModel> apps, OnFavInteractionListener listener) {
        this(context, apps, listener, new FocusEngine());
    }

    public FavAppsAdapter(Context context, List<AppModel> apps,
                          OnFavInteractionListener listener, FocusEngine focusEngine) {
        this.context = context;
        this.apps = apps;
        this.listener = listener;
        this.focusEngine = focusEngine != null ? focusEngine : new FocusEngine();
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
            focusEngine.apply(v, hasFocus);
            if (hasFocus) {
                holder.tvTitle.setVisibility(View.VISIBLE);
                if (listener != null) listener.onFavFocused(app);
            } else {
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
