package com.gerak.tvlauncher;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class PreferencesManager {
    private static final String PREF_NAME = "aero_launcher_prefs";
    private static final String KEY_FAVORITES = "favorite_packages";
    private static final String KEY_HIDDEN = "hidden_packages";
    private static final String KEY_COLUMNS = "grid_columns";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public Set<String> getFavorites() {
        return new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>()));
    }

    public boolean isFavorite(String packageName) {
        return getFavorites().contains(packageName);
    }

    public void toggleFavorite(String packageName) {
        Set<String> set = getFavorites();
        if (set.contains(packageName)) {
            set.remove(packageName);
        } else {
            set.add(packageName);
        }
        prefs.edit().putStringSet(KEY_FAVORITES, set).apply();
    }

    public Set<String> getHidden() {
        return new HashSet<>(prefs.getStringSet(KEY_HIDDEN, new HashSet<>()));
    }

    public void hideApp(String packageName) {
        Set<String> set = getHidden();
        set.add(packageName);
        prefs.edit().putStringSet(KEY_HIDDEN, set).apply();
    }

    public void unhideApp(String packageName) {
        Set<String> set = getHidden();
        set.remove(packageName);
        prefs.edit().putStringSet(KEY_HIDDEN, set).apply();
    }

    public int getColumnsCount() {
        return prefs.getInt(KEY_COLUMNS, 4);
    }

    public void setColumnsCount(int count) {
        prefs.edit().putInt(KEY_COLUMNS, count).apply();
    }
}
