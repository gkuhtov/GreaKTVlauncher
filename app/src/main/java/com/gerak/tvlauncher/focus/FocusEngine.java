package com.gerak.tvlauncher.focus;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public final class FocusEngine {

    public static final float DEFAULT_SCALE = 1.08f;
    public static final float DEFAULT_ELEVATION = 10f;
    public static final long DEFAULT_DURATION_MS = 160L;

    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private float scale = DEFAULT_SCALE;
    private float elevation = DEFAULT_ELEVATION;
    private long durationMs = DEFAULT_DURATION_MS;
    private boolean enabled = true;

    public FocusEngine() {}

    public FocusEngine setScale(float scale) {
        this.scale = Math.max(1.0f, Math.min(1.20f, scale));
        return this;
    }

    public FocusEngine setElevation(float elevation) {
        this.elevation = Math.max(0f, elevation);
        return this;
    }

    public FocusEngine setDuration(long durationMs) {
        this.durationMs = Math.max(80L, Math.min(300L, durationMs));
        return this;
    }

    public FocusEngine setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public float getScale() {
        return scale;
    }

    public void apply(View view, boolean hasFocus) {
        if (view == null || !enabled) return;
        view.animate().cancel();
        if (hasFocus) {
            view.animate()
                    .scaleX(scale).scaleY(scale).translationZ(elevation)
                    .setDuration(durationMs).setInterpolator(INTERPOLATOR).start();
        } else {
            view.animate()
                    .scaleX(1.0f).scaleY(1.0f).translationZ(0f)
                    .setDuration(durationMs).setInterpolator(INTERPOLATOR).start();
        }
    }

    public static FocusEngine fromPreferences(com.gerak.tvlauncher.PreferencesManager prefs) {
        FocusEngine engine = new FocusEngine();
        if (prefs != null) engine.setScale(prefs.getFocusScale());
        return engine;
    }
}
