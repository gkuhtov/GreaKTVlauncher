package com.gerak.tvlauncher.theme;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.widget.TextView;

import com.gerak.tvlauncher.PreferencesManager;
import com.gerak.tvlauncher.focus.FocusEngine;

public final class ThemeManager {

    private final PreferencesManager prefs;
    private Theme current;

    public ThemeManager(PreferencesManager prefs) {
        this.prefs = prefs;
        this.current = ThemeRepository.getById(prefs.getThemeId());
    }

    public Theme getCurrent() { return current; }

    public void setTheme(String themeId) {
        current = ThemeRepository.getById(themeId);
        prefs.setThemeId(current.id);
        prefs.setFocusScale(current.focusScale);
    }

    public FocusEngine createFocusEngine() {
        return new FocusEngine()
                .setScale(prefs.getFocusScale())
                .setElevation(current.focusElevation);
    }

    public void applyToActivity(Activity activity, View root) {
        if (root == null) return;
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{current.backgroundStart, current.backgroundCenter, current.backgroundEnd}
        );
        root.setBackground(bg);
        applyTextColor(activity, "tv_clock", current.textPrimary);
        applyTextColor(activity, "tv_preview_title", current.textPrimary);
        applyTextColor(activity, "tv_preview_category", current.textSecondary);
        applyTextColor(activity, "tv_preview_desc", current.textSecondary);
        applyTextColor(activity, "tv_apps_header", current.textPrimary);
        applyTextColor(activity, "tv_apps_subheader", current.textSecondary);
    }

    private void applyTextColor(Activity activity, String idName, int color) {
        int id = activity.getResources().getIdentifier(idName, "id", activity.getPackageName());
        if (id == 0) return;
        View v = activity.findViewById(id);
        if (v instanceof TextView) ((TextView) v).setTextColor(color);
    }

    public static String[] themeDisplayNames() {
        java.util.List<Theme> all = ThemeRepository.getAll();
        String[] names = new String[all.size()];
        for (int i = 0; i < all.size(); i++) names[i] = all.get(i).displayName;
        return names;
    }

    public static String[] themeIds() {
        java.util.List<Theme> all = ThemeRepository.getAll();
        String[] ids = new String[all.size()];
        for (int i = 0; i < all.size(); i++) ids[i] = all.get(i).id;
        return ids;
    }
}
