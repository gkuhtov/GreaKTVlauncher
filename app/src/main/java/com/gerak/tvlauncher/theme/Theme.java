package com.gerak.tvlauncher.theme;

public final class Theme {

    public final String id;
    public final String displayName;
    public final int backgroundStart;
    public final int backgroundCenter;
    public final int backgroundEnd;
    public final int surface;
    public final int surfaceFocused;
    public final int strokeSubtle;
    public final int strokeFocused;
    public final int tabActive;
    public final int tabFocused;
    public final int textPrimary;
    public final int textSecondary;
    public final int accent;
    public final float focusScale;
    public final float focusElevation;
    public final boolean focusGlowDefault;

    public Theme(
            String id, String displayName,
            int backgroundStart, int backgroundCenter, int backgroundEnd,
            int surface, int surfaceFocused, int strokeSubtle, int strokeFocused,
            int tabActive, int tabFocused, int textPrimary, int textSecondary, int accent,
            float focusScale, float focusElevation, boolean focusGlowDefault
    ) {
        this.id = id;
        this.displayName = displayName;
        this.backgroundStart = backgroundStart;
        this.backgroundCenter = backgroundCenter;
        this.backgroundEnd = backgroundEnd;
        this.surface = surface;
        this.surfaceFocused = surfaceFocused;
        this.strokeSubtle = strokeSubtle;
        this.strokeFocused = strokeFocused;
        this.tabActive = tabActive;
        this.tabFocused = tabFocused;
        this.textPrimary = textPrimary;
        this.textSecondary = textSecondary;
        this.accent = accent;
        this.focusScale = focusScale;
        this.focusElevation = focusElevation;
        this.focusGlowDefault = focusGlowDefault;
    }
}
