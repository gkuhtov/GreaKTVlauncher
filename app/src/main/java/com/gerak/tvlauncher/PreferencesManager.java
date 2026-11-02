package com.gerak.tvlauncher;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class PreferencesManager {

    private static final String PREF_NAME = "aero_launcher_prefs";
    private static final String KEY_FAVORITES_ORDERED = "favorite_packages_ordered";
    private static final String KEY_HIDDEN = "hidden_packages";
    private static final String KEY_COLUMNS = "grid_columns";
    private static final String KEY_THEME_ID = "theme_id";
    private static final String KEY_FOCUS_GLOW_ENABLED = "focus_glow_enabled";
    private static final String KEY_FOCUS_SCALE = "focus_scale";
    private static final String KEY_WEATHER_CITY = "weather_city";
    private static final String KEY_WEATHER_LAT = "weather_lat";
    private static final String KEY_WEATHER_LON = "weather_lon";
    private static final String KEY_LAST_UPDATE_CHECK = "last_update_check";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<String> getFavoritesOrdered() {
        String raw = prefs.getString(KEY_FAVORITES_ORDERED, "");
        if (TextUtils.isEmpty(raw)) return new ArrayList<>();
        return new ArrayList<>(Arrays.asList(raw.split(",")));
    }

    public void setFavoritesOrdered(List<String> packages) {
        if (packages == null || packages.isEmpty()) {
            prefs.edit().remove(KEY_FAVORITES_ORDERED).apply();
            return;
        }
        LinkedHashSet<String> ordered = new LinkedHashSet<>(packages);
        prefs.edit().putString(KEY_FAVORITES_ORDERED, TextUtils.join(",", ordered)).apply();
    }

    public boolean isFavorite(String packageName) {
        return getFavoritesOrdered().contains(packageName);
    }

    public void addFavorite(String packageName) {
        List<String> list = getFavoritesOrdered();
        if (!list.contains(packageName)) {
            list.add(packageName);
            setFavoritesOrdered(list);
        }
    }

    public void removeFavorite(String packageName) {
        List<String> list = getFavoritesOrdered();
        if (list.remove(packageName)) setFavoritesOrdered(list);
    }

    public void toggleFavorite(String packageName) {
        if (isFavorite(packageName)) removeFavorite(packageName);
        else addFavorite(packageName);
    }

    public Set<String> getHidden() {
        return new HashSet<>(prefs.getStringSet(KEY_HIDDEN, new HashSet<>()));
    }

    public boolean isHidden(String packageName) {
        return getHidden().contains(packageName);
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
        return prefs.getInt(KEY_COLUMNS, 5);
    }

    public void setColumnsCount(int count) {
        if (count < 3) count = 3;
        if (count > 8) count = 8;
        prefs.edit().putInt(KEY_COLUMNS, count).apply();
    }

    public String getThemeId() {
        return prefs.getString(KEY_THEME_ID, "default");
    }

    public void setThemeId(String themeId) {
        prefs.edit().putString(KEY_THEME_ID, themeId != null ? themeId : "default").apply();
    }

    public boolean isFocusGlowEnabled() {
        return prefs.getBoolean(KEY_FOCUS_GLOW_ENABLED, true);
    }

    public void setFocusGlowEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_FOCUS_GLOW_ENABLED, enabled).apply();
    }

    public float getFocusScale() {
        return prefs.getFloat(KEY_FOCUS_SCALE, 1.08f);
    }

    public void setFocusScale(float scale) {
        if (scale < 1.0f) scale = 1.0f;
        if (scale > 1.20f) scale = 1.20f;
        prefs.edit().putFloat(KEY_FOCUS_SCALE, scale).apply();
    }

    public String getWeatherCity() {
        return prefs.getString(KEY_WEATHER_CITY, "");
    }

    public void setWeatherCity(String city) {
        prefs.edit().putString(KEY_WEATHER_CITY, city != null ? city : "").apply();
    }

    public float getWeatherLat() {
        return prefs.getFloat(KEY_WEATHER_LAT, Float.NaN);
    }

    public float getWeatherLon() {
        return prefs.getFloat(KEY_WEATHER_LON, Float.NaN);
    }

    public void setWeatherLocation(String city, float lat, float lon) {
        prefs.edit()
                .putString(KEY_WEATHER_CITY, city != null ? city : "")
                .putFloat(KEY_WEATHER_LAT, lat)
                .putFloat(KEY_WEATHER_LON, lon)
                .apply();
    }

    public long getLastUpdateCheck() {
        return prefs.getLong(KEY_LAST_UPDATE_CHECK, 0L);
    }

    public void setLastUpdateCheck(long timestamp) {
        prefs.edit().putLong(KEY_LAST_UPDATE_CHECK, timestamp).apply();
    }
}
