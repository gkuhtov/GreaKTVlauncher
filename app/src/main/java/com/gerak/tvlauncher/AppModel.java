package com.gerak.tvlauncher;

import android.graphics.drawable.Drawable;

public class AppModel {
    private final String label;
    private final String packageName;
    private final Drawable icon;

    public AppModel(String label, String packageName, Drawable icon) {
        this.label = label;
        this.packageName = packageName;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
