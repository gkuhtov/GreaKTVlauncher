package com.gerak.tvlauncher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
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

    private TextView tvClock, tvDate, tvSidebarCounter;
    private View navAllApps, navSettings;
    private RecyclerView rvApps;
    private AppsAdapter adapter;
    private final List<AppModel> appList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupClock();
        setupNavigation();
        setupAppsGrid();
    }

    private void initViews() {
        tvClock = findViewById(R.id.tv_clock);
        tvDate = findViewById(R.id.tv_date);
        tvSidebarCounter = findViewById(R.id.tv_sidebar_counter);
        navAllApps = findViewById(R.id.nav_all_apps);
        navSettings = findViewById(R.id.nav_settings);
        rvApps = findViewById(R.id.rv_apps);
    }

    private void setupNavigation() {
        navAllApps.setSelected(true);

        navSettings.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            } catch (Exception ignored) {}
        });

        // Анимация фокуса на кнопках сайдбара
        View.OnFocusChangeListener navFocus = (v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.04f).scaleY(1.04f).setDuration(120).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start();
            }
        };
        navAllApps.setOnFocusChangeListener(navFocus);
        navSettings.setOnFocusChangeListener(navFocus);
    }

    private void setupClock() {
        updateClock();
        new CountDownTimer(Long.MAX_VALUE, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                updateClock();
            }

            @Override
            public void onFinish() {}
        }.start();
    }

    private void updateClock() {
        if (tvClock != null) {
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            tvClock.setText(timeFormat.format(new Date()));
        }
        if (tvDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM", new Locale("ru"));
            String dateStr = dateFormat.format(new Date());
            tvDate.setText(dateStr.substring(0, 1).toUpperCase(Locale.getDefault()) + dateStr.substring(1));
        }
    }

    private void setupAppsGrid() {
        // Сетка 4 колонки с вертикальным скроллом, как в Arc Launcher
        GridLayoutManager layoutManager = new GridLayoutManager(this, 4);
        rvApps.setLayoutManager(layoutManager);

        adapter = new AppsAdapter(this, appList, app -> {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        });
        rvApps.setAdapter(adapter);
        loadInstalledApps();
    }

    private void loadInstalledApps() {
        appList.clear();
        PackageManager pm = getPackageManager();
        Map<String, AppModel> map = new HashMap<>();

        // 1. Android TV приложения (Leanback)
        Intent leanbackIntent = new Intent(Intent.ACTION_MAIN, null);
        leanbackIntent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);
        List<ResolveInfo> leanbackApps = pm.queryIntentActivities(leanbackIntent, 0);
        if (leanbackApps != null) {
            for (ResolveInfo ri : leanbackApps) {
                String pkg = ri.activityInfo.packageName;
                if (!pkg.equals(getPackageName())) {
                    String label = ri.loadLabel(pm).toString();
                    map.put(pkg, new AppModel(label, pkg, ri.loadIcon(pm)));
                }
            }
        }

        // 2. Все обычные (Sideload / планшетные / телефонные) приложения
        Intent standardIntent = new Intent(Intent.ACTION_MAIN, null);
        standardIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> standardApps = pm.queryIntentActivities(standardIntent, 0);
        if (standardApps != null) {
            for (ResolveInfo ri : standardApps) {
                String pkg = ri.activityInfo.packageName;
                if (!pkg.equals(getPackageName()) && !map.containsKey(pkg)) {
                    String label = ri.loadLabel(pm).toString();
                    map.put(pkg, new AppModel(label, pkg, ri.loadIcon(pm)));
                }
            }
        }

        appList.addAll(map.values());
        Collections.sort(appList, (a, b) -> a.getLabel().compareToIgnoreCase(b.getLabel()));

        if (tvSidebarCounter != null) {
            tvSidebarCounter.setText(appList.size() + " APPS");
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadInstalledApps();
    }
}
