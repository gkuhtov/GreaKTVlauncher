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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
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

        // Верхняя горизонтальная лента
        rvHomeFavs.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        favAdapter = new FavAppsAdapter(this, favApps, new FavAppsAdapter.OnFavInteractionListener() {
            @Override
            public void onFavFocused(AppModel app) {
                showAppPreview(app);
            }

            @Override
            public void onFavClicked(AppModel app) {
                launchApp(app.getPackageName());
            }
        });
        rvHomeFavs.setAdapter(favAdapter);

        // Сетка «Все приложения» (5 колонок)
        rvAllApps.setLayoutManager(new GridLayoutManager(this, 5));
        gridAdapter = new GridAppsAdapter(this, allApps, new GridAppsAdapter.OnAppInteractionListener() {
            @Override
            public void onAppClick(AppModel app) {
                launchApp(app.getPackageName());
            }

            @Override
            public void onAppLongClick(AppModel app) {
                showAppOptions(app);
            }
        });
        rvAllApps.setAdapter(gridAdapter);

        // Реакция центрального баннера на нижние плитки
        tileBottomApps.setOnFocusChangeListener((v, hasFocus) -> {
            applyCardScale(v, hasFocus);
            if (hasFocus) showLibraryPreview();
        });
        tileBottomApps.setOnClickListener(v -> switchTab(false));

        tileBottomSettings.setOnFocusChangeListener((v, hasFocus) -> {
            applyCardScale(v, hasFocus);
            if (hasFocus) showSettingsPreview();
        });
        tileBottomSettings.setOnClickListener(v -> openQuickSettings());

        btnPreviewOpen.setOnClickListener(v -> {
            if (currentState == PreviewState.LIBRARY) {
                switchTab(false);
            } else if (currentState == PreviewState.SETTINGS) {
                openQuickSettings();
            } else if (currentSelectedApp != null) {
                launchApp(currentSelectedApp.getPackageName());
            }
        });
    }

    private void applyCardScale(View v, boolean hasFocus) {
        if (hasFocus) {
            v.animate().scaleX(1.05f).scaleY(1.05f).translationZ(8f).setDuration(120).start();
        } else {
            v.animate().scaleX(1.0f).scaleY(1.0f).translationZ(0f).setDuration(120).start();
        }
    }

    private void setupTabs() {
        tabHome.setSelected(true);

        tabHome.setOnClickListener(v -> switchTab(true));
        tabApps.setOnClickListener(v -> switchTab(false));
        tabSettings.setOnClickListener(v -> openQuickSettings());

        View.OnFocusChangeListener focus = (v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.06f).scaleY(1.06f).setDuration(120).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start();
            }
        };

        tabHome.setOnFocusChangeListener(focus);
        tabApps.setOnFocusChangeListener(focus);
        tabSettings.setOnFocusChangeListener(focus);
        btnPreviewOpen.setOnFocusChangeListener(focus);
    }

    private void switchTab(boolean isHome) {
        if (isHome) {
            tabHome.setSelected(true);
            tabHome.setTextColor(0xFFFFFFFF);
            tabApps.setSelected(false);
            tabApps.setTextColor(0xFF8F9CAE);
            viewHome.setVisibility(View.VISIBLE);
            viewApps.setVisibility(View.GONE);
        } else {
            tabHome.setSelected(false);
            tabHome.setTextColor(0xFF8F9CAE);
            tabApps.setSelected(true);
            tabApps.setTextColor(0xFFFFFFFF);
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
        CharSequence[] items = new CharSequence[] {
                "Wi-Fi",
                "Bluetooth",
                "Звук",
                "Все настройки ТВ",
                "Выбрать лаунчер по умолчанию"
        };
        new AlertDialog.Builder(this)
                .setTitle("Быстрые настройки")
                .setItems(items, (dialog, which) -> {
                    if (which == 0) startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    else if (which == 1) startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                    else if (which == 2) startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS));
                    else if (which == 3) startActivity(new Intent(Settings.ACTION_SETTINGS));
                    else if (which == 4) startActivity(new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
                })
                .show();
    }

    private void showAppOptions(AppModel app) {
        CharSequence[] options = new CharSequence[] { "Открыть", "О приложении", "Удалить" };
        new AlertDialog.Builder(this)
                .setTitle(app.getLabel())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) launchApp(app.getPackageName());
                    else if (which == 1) {
                        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.setData(Uri.parse("package:" + app.getPackageName()));
                        startActivity(i);
                    } else if (which == 2) {
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

        allApps.addAll(map.values());
        Collections.sort(allApps, (a, b) -> a.getLabel().compareToIgnoreCase(b.getLabel()));

        for (int i = 0; i < Math.min(10, allApps.size()); i++) {
            favApps.add(allApps.get(i));
        }

        tvAppsSubheader.setText(allApps.size() + " приложения · только то, что установлено");

        if (!favApps.isEmpty()) {
            showAppPreview(favApps.get(0));
        }

        favAdapter.notifyDataSetChanged();
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadApps();
    }
}
