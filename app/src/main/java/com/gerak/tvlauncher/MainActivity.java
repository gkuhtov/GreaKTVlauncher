package com.gerak.tvlauncher;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private VideoView bgVideoView;
    private final Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable timerRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initVideo();
        initCountdown();
        initApps();
    }

    private void initVideo() {
        bgVideoView = findViewById(R.id.bgVideoView);
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bg_gtavi_video);
        bgVideoView.setVideoURI(uri);
        bgVideoView.setOnPreparedListener(mp -> {
            mp.setLooping(true);
            mp.setVolume(1.0f, 1.0f);
            bgVideoView.start();
        });
    }

    private void initCountdown() {
        TextView tv = findViewById(R.id.tvCountdown);
        Calendar target = Calendar.getInstance();
        target.set(2026, Calendar.NOVEMBER, 17, 0, 0, 0);
        long targetMillis = target.getTimeInMillis();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long diff = targetMillis - System.currentTimeMillis();
                if (diff > 0) {
                    long days = TimeUnit.MILLISECONDS.toDays(diff);
                    long h = TimeUnit.MILLISECONDS.toHours(diff) % 24;
                    long m = TimeUnit.MILLISECONDS.toMinutes(diff) % 60;
                    long s = TimeUnit.MILLISECONDS.toSeconds(diff) % 60;
                    tv.setText(String.format(Locale.getDefault(), "%d дн. %02d:%02d:%02d", days, h, m, s));
                    timerHandler.postDelayed(this, 1000);
                } else {
                    tv.setText("ВЫШЛА");
                }
            }
        };
        timerHandler.post(timerRunnable);
    }

    private void initApps() {
        RecyclerView rv = findViewById(R.id.appsList);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        PackageManager pm = getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER);

        List<ResolveInfo> activities = pm.queryIntentActivities(intent, 0);
        if (activities.isEmpty()) {
            intent = new Intent(Intent.ACTION_MAIN, null);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            activities = pm.queryIntentActivities(intent, 0);
        }

        List<AppModel> apps = new ArrayList<>();
        for (ResolveInfo ri : activities) {
            if (ri.activityInfo.packageName.equals(getPackageName())) continue;
            apps.add(new AppModel(
                ri.loadLabel(pm).toString(),
                ri.activityInfo.packageName,
                ri.loadIcon(pm)
            ));
        }

        Collections.sort(apps, (a, b) -> a.label.compareToIgnoreCase(b.label));
        rv.setAdapter(new AppsAdapter(this, apps));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bgVideoView != null && !bgVideoView.isPlaying()) {
            bgVideoView.start();
        }
    }
}
