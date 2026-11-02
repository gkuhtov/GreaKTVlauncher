package com.gerak.tvlauncher;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gerak.tvlauncher.focus.FocusEngine;
import com.gerak.tvlauncher.settings.QuickSettingsDialog;
import com.gerak.tvlauncher.theme.ThemeManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class MainActivity extends Activity {

    private enum PreviewState { APP, LIBRARY, SETTINGS }

    private PreviewState currentState = PreviewState.APP;
    private TextView tabHome, tabApps, tabSettings;
    private View viewHome, viewApps;
    private TextView tvClock, tvAppsSubheader, tvPreviewCategory, tvPreviewTitle, tvPreviewDesc;
    private ImageView ivPreviewBigLogo;
    private Button btnPreviewOpen;
    private View tileBottomApps, tileBottomSettings;

    private RecyclerView rvHomeFavs, rvAllApps;
    private FavAppsAdapter favAdapter;
    private GridAppsAdapter gridAdapter;

    private final List<AppModel> allApps = new ArrayList<>();
    private final List<AppModel> favApps = new ArrayList<>();
    private AppModel currentSelectedApp = null;

    private PreferencesManager preferencesManager;
    private ThemeManager themeManager;
    private FocusEngine focusEngine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferencesManager = new PreferencesManager(this);
        themeManager = new ThemeManager(preferencesManager);
        focusEngine = themeManager.createFocusEngine();

        initViews();
        themeManager.applyToActivity(this, findViewById(android.R.id.content));
        setupClock();
        setupTabs();
        loadApps();
    }

    private void initViews() {
        tabHome = findViewById(R.id.tab_home);
        tabApps = findViewById(R.id.tab_apps);
        tabSettings = findViewById(R.id.tab_settings);

        viewHome = findViewById(R.id.view_home);
        viewApps = findViewById(R.id.view_apps);

        tvClock = findViewById(R.id.tv_clock);
        tvAppsSubheader = findViewById(R.id.tv_apps_subheader);
        tvPreviewCategory = findViewById(R.id.tv_preview_category);
        tvPreviewTitle = findViewById(R.id.tv_preview_title);
        tvPreviewDesc = findViewById(R.id.tv_preview_desc);
        ivPreviewBigLogo = findViewById(R.id.iv_preview_big_logo);
        btnPreviewOpen = findViewById(R.id.btn_preview_open);

        tileBottomApps = findViewById(R.id.tile_bottom_apps);
        tileBottomSettings = findViewById(R.id.tile_bottom_settings);

        rvHomeFavs = findViewById(R.id.rv_home_favs);
        rvAllApps = findViewById(R.id.rv_all_apps);

        rvHomeFavs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        favAdapter = new FavAppsAdapter(this, favApps, new FavAppsAdapter.OnFavInteractionListener() {
            @Override
            public void onFavFocused(AppModel app) { showAppPreview(app); }

            @Override
            public void onFavClicked(AppModel app) { launchApp(app.getPackageName()); }
        }, focusEngine);
        rvHomeFavs.setAdapter(favAdapter);

        int columns = preferencesManager.getColumnsCount();
        rvAllApps.setLayoutManager(new GridLayoutManager(this, columns));
        gridAdapter = new GridAppsAdapter(this, allApps, new GridAppsAdapter.OnAppInteractionListener() {
            @Override
            public void onAppClick(AppModel app) { launchApp(app.getPackageName()); }

            @Override
            public void onAppLongClick(AppModel app) { showAppOptions(app); }
        }, focusEngine);
        rvAllApps.setAdapter(gridAdapter);

        tileBottomApps.setOnFocusChangeListener((v, hasFocus) -> {
            focusEngine.apply(v, hasFocus);
            if (hasFocus) showLibraryPreview();
        });
        tileBottomApps.setOnClickListener(v -> switchTab(false));

        tileBottomSettings.setOnFocusChangeListener((v, hasFocus) -> {
            focusEngine.apply(v, hasFocus);
            if (hasFocus) showSettingsPreview();
        });
        tileBottomSettings.setOnClickListener(v -> openQuickSettings());

        btnPreviewOpen.setOnClickListener(v -> {
            if (currentState == PreviewState.LIBRARY) switchTab(false);
            else if (currentState == PreviewState.SETTINGS) openQuickSettings();
            else if (currentSelectedApp != null) launchApp(currentSelectedApp.getPackageName());
        });
    }

    private void setupTabs() {
        tabHome.setSelected(true);
        tabHome.setOnClickListener(v -> switchTab(true));
        tabApps.setOnClickListener(v -> switchTab(false));
        tabSettings.setOnClickListener(v -> openQuickSettings());

        View.OnFocusChangeListener tabFocus = (v, hasFocus) -> focusEngine.apply(v, hasFocus);
        tabHome.setOnFocusChangeListener(tabFocus);
        tabApps.setOnFocusChangeListener(tabFocus);
        tabSettings.setOnFocusChangeListener(tabFocus);
        btnPreviewOpen.setOnFocusChangeListener(tabFocus);
    }

    private void switchTab(boolean isHome) {
        if (isHome) {
            tabHome.setSelected(true);
            tabHome.setTextColor(0xFFFFFFFF);
            tabApps.setSelected(false);
            tabApps.setTextColor(0xFF9DA8BA);
            tabSettings.setSelected(false);
            tabSettings.setTextColor(0xFF9DA8BA);
            viewHome.setVisibility(View.VISIBLE);
            viewApps.setVisibility(View.GONE);
        } else {
            tabHome.setSelected(false);
            tabHome.setTextColor(0xFF9DA8BA);
            tabApps.setSelected(true);
            tabApps.setTextColor(0xFFFFFFFF);
            tabSettings.setSelected(false);
            tabSettings.setTextColor(0xFF9DA8BA);
            viewHome.setVisibility(View.GONE);
            viewApps.setVisibility(View.VISIBLE);
        }
    }

    private void showAppPreview(AppModel app) {
        currentState = PreviewState.APP;
        currentSelectedApp = app;
        tvPreviewCategory.setText("ПРИЛОЖЕНИЕ");
        tvPreviewTitle.setText(app.getLabel());
        tvPreviewDesc.setText("Всё, что ты любишь в " + app.getLabel() + ", — на большом экране");
        ivPreviewBigLogo.setImageDrawable(app.getIcon());
        btnPreviewOpen.setText("[O]  Открыть");
    }

    private void showLibraryPreview() {
        currentState = PreviewState.LIBRARY;
        tvPreviewCategory.setText("БИБЛИОТЕКА");
        tvPreviewTitle.setText("Приложения");
        tvPreviewDesc.setText(allApps.size() + " приложения · всё установленное на телевизоре");
        ivPreviewBigLogo.setImageResource(R.drawable.ic_aero_grid);
        btnPreviewOpen.setText("[O]  Все приложения");
    }

    private void showSettingsPreview() {
        currentState = PreviewState.SETTINGS;
        tvPreviewCategory.setText("AERO TV");
        tvPreviewTitle.setText("Настройки");
        tvPreviewDesc.setText("Wi-Fi, Bluetooth, звук и оформление");
        ivPreviewBigLogo.setImageResource(R.drawable.ic_aero_settings);
        btnPreviewOpen.setText("[O]  Открыть настройки");
    }

    private void openQuickSettings() {
        QuickSettingsDialog dialog = new QuickSettingsDialog(this, themeManager, new QuickSettingsDialog.Callback() {
            @Override
            public void onThemeChanged(String themeId) {
                focusEngine = themeManager.createFocusEngine();
                themeManager.applyToActivity(MainActivity.this, findViewById(android.R.id.content));
                if (favAdapter != null) {
                    favAdapter = new FavAppsAdapter(MainActivity.this, favApps,
                            new FavAppsAdapter.OnFavInteractionListener() {
                                @Override public void onFavFocused(AppModel app) { showAppPreview(app); }
                                @Override public void onFavClicked(AppModel app) { launchApp(app.getPackageName()); }
                            }, focusEngine);
                    rvHomeFavs.setAdapter(favAdapter);
                }
                if (gridAdapter != null) {
                    gridAdapter = new GridAppsAdapter(MainActivity.this, allApps,
                            new GridAppsAdapter.OnAppInteractionListener() {
                                @Override public void onAppClick(AppModel app) { launchApp(app.getPackageName()); }
                                @Override public void onAppLongClick(AppModel app) { showAppOptions(app); }
                            }, focusEngine);
                    rvAllApps.setAdapter(gridAdapter);
                }
            }

            @Override
            public void onRequestAppRefresh() { loadApps(); }
        });
        dialog.show();
    }

    private void showAppOptions(AppModel app) {
        boolean isFav = preferencesManager.isFavorite(app.getPackageName());
        CharSequence[] options = new CharSequence[] {
                "Открыть",
                isFav ? "Убрать из избранного" : "Добавить в избранное",
                "Скрыть",
                "О приложении",
                "Удалить"
        };
        new AlertDialog.Builder(this)
                .setTitle(app.getLabel())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) launchApp(app.getPackageName());
                    else if (which == 1) {
                        preferencesManager.toggleFavorite(app.getPackageName());
                        loadApps();
                    } else if (which == 2) {
                        preferencesManager.hideApp(app.getPackageName());
                        loadApps();
                    } else if (which == 3) {
                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.setData(Uri.parse("package:" + app.getPackageName()));
                        startActivity(i);
                    } else if (which == 4) {
                        Intent i = new Intent(Intent.ACTION_DELETE);
                        i.setData(Uri.parse("package:" + app.getPackageName()));
                        startActivity(i);
                    }
                })
                .show();
    }

    private void launchApp(String pkg) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(pkg);
        if (intent != null) startActivity(intent);
    }

    private void setupClock() {
        new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                tvClock.setText(sdf.format(new Date()));
            }
            @Override
            public void onFinish() {}
        }.start();
    }

    private void loadApps() {
        allApps.clear();
        favApps.clear();
        PackageManager pm = getPackageManager();
        Map<String, AppModel> map = new HashMap<>();

        Intent leanback = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> tvList = pm.queryIntentActivities(leanback, 0);
        if (tvList != null) {
            for (ResolveInfo ri : tvList) {
                String pkg = ri.activityInfo.packageName;
                if (!pkg.equals(getPackageName())) {
                    map.put(pkg, new AppModel(ri.loadLabel(pm).toString(), pkg, ri.loadIcon(pm)));
                }
            }
        }

        Intent standard = new Intent(Intent.ACTION_MAIN, null).addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> stdList = pm.queryIntentActivities(standard, 0);
        if (stdList != null) {
            for (ResolveInfo ri : stdList) {
                String pkg = ri.activityInfo.packageName;
                if (!pkg.equals(getPackageName()) && !map.containsKey(pkg)) {
                    map.put(pkg, new AppModel(ri.loadLabel(pm).toString(), pkg, ri.loadIcon(pm)));
                }
            }
        }

        Set<String> hidden = preferencesManager.getHidden();
        List<AppModel> visible = new ArrayList<>();
        for (AppModel app : map.values()) {
            if (!hidden.contains(app.getPackageName())) visible.add(app);
        }
        Collections.sort(visible, (a, b) -> a.getLabel().compareToIgnoreCase(b.getLabel()));
        allApps.addAll(visible);

        List<String> favOrder = preferencesManager.getFavoritesOrdered();
        if (favOrder.isEmpty()) {
            int limit = Math.min(8, allApps.size());
            for (int i = 0; i < limit; i++) favApps.add(allApps.get(i));
        } else {
            Map<String, AppModel> byPkg = new HashMap<>();
            for (AppModel app : allApps) byPkg.put(app.getPackageName(), app);
            for (String pkg : favOrder) {
                AppModel app = byPkg.get(pkg);
                if (app != null) favApps.add(app);
            }
            if (favApps.isEmpty() && !allApps.isEmpty()) {
                int limit = Math.min(8, allApps.size());
                for (int i = 0; i < limit; i++) favApps.add(allApps.get(i));
            }
        }

        tvAppsSubheader.setText(allApps.size() + " приложения · только то, что установлено");
        if (!favApps.isEmpty()) showAppPreview(favApps.get(0));

        favAdapter.notifyDataSetChanged();
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();
    }
}
