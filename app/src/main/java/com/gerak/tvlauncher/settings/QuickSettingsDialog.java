package com.gerak.tvlauncher.settings;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.gerak.tvlauncher.theme.ThemeManager;

public class QuickSettingsDialog extends Dialog {

    public interface Callback {
        void onThemeChanged(String themeId);
        void onRequestAppRefresh();
    }

    private final ThemeManager themeManager;
    private final Callback callback;

    private static final String[] TITLES = {
            "Wi-Fi", "Bluetooth", "Звук", "Оформление",
            "Лаунчер по умолчанию", "Все настройки ТВ"
    };

    private static final String[] SUBTITLES = {
            "Подключение к сети",
            "Пульт, наушники или геймпад",
            "Громкость и вывод",
            "Тема и эффекты фокуса",
            "Выбрать Aero для кнопки Home",
            "Изображение, устройства и система"
    };

    public QuickSettingsDialog(@NonNull Context context, ThemeManager themeManager, Callback callback) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.themeManager = themeManager;
        this.callback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        Context ctx = getContext();
        float density = ctx.getResources().getDisplayMetrics().density;

        LinearLayout root = new LinearLayout(ctx);
        root.setOrientation(LinearLayout.HORIZONTAL);
        root.setBackgroundColor(0xCC000000);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View spacer = new View(ctx);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f));
        spacer.setOnClickListener(v -> dismiss());
        root.addView(spacer);

        LinearLayout panel = new LinearLayout(ctx);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setBackgroundColor(0xE6121828);
        int pad = (int) (28 * density);
        panel.setPadding(pad, (int) (36 * density), pad, (int) (24 * density));
        panel.setLayoutParams(new LinearLayout.LayoutParams((int) (420 * density), ViewGroup.LayoutParams.MATCH_PARENT));

        TextView title = new TextView(ctx);
        title.setText("Быстрые настройки");
        title.setTextColor(Color.WHITE);
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        panel.addView(title);

        TextView subtitle = new TextView(ctx);
        subtitle.setText("Всё нужное — под рукой");
        subtitle.setTextColor(0xFF8FA0B5);
        subtitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        subLp.topMargin = (int) (4 * density);
        subLp.bottomMargin = (int) (20 * density);
        subtitle.setLayoutParams(subLp);
        panel.addView(subtitle);

        ScrollView scroll = new ScrollView(ctx);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f));
        LinearLayout list = new LinearLayout(ctx);
        list.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(list);

        View firstRow = null;
        for (int i = 0; i < TITLES.length; i++) {
            final int index = i;
            View row = createRow(ctx, density, i);
            row.setOnClickListener(v -> onItemSelected(index));
            row.setOnKeyListener((v, keyCode, event) -> {
                if (event.getAction() == KeyEvent.ACTION_UP
                        && (keyCode == KeyEvent.KEYCODE_DPAD_CENTER
                        || keyCode == KeyEvent.KEYCODE_ENTER
                        || keyCode == KeyEvent.KEYCODE_BUTTON_A)) {
                    onItemSelected(index);
                    return true;
                }
                return false;
            });
            list.addView(row);
            if (firstRow == null) firstRow = row;
        }
        panel.addView(scroll);

        TextView hints = new TextView(ctx);
        hints.setText("⭘ Выбрать    ◀ Закрыть");
        hints.setTextColor(0xFF78879E);
        hints.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        LinearLayout.LayoutParams hintsLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        hintsLp.topMargin = (int) (12 * density);
        hints.setLayoutParams(hintsLp);
        panel.addView(hints);

        root.addView(panel);
        setContentView(root);

        Window window = getWindow();
        if (window != null) {
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        if (firstRow != null) {
            final View focusTarget = firstRow;
            focusTarget.post(focusTarget::requestFocus);
        }
    }

    private View createRow(Context ctx, float density, int index) {
        LinearLayout row = new LinearLayout(ctx);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setFocusable(true);
        row.setFocusableInTouchMode(true);
        row.setClickable(true);
        int vPad = (int) (14 * density);
        int hPad = (int) (16 * density);
        row.setPadding(hPad, vPad, hPad, vPad);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.bottomMargin = (int) (6 * density);
        row.setLayoutParams(lp);
        row.setBackgroundColor(0xFF161C28);

        row.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.setBackgroundColor(0xFF2A3854);
                v.setScaleX(1.02f);
                v.setScaleY(1.02f);
            } else {
                v.setBackgroundColor(0xFF161C28);
                v.setScaleX(1f);
                v.setScaleY(1f);
            }
        });

        TextView t = new TextView(ctx);
        t.setText(TITLES[index]);
        t.setTextColor(Color.WHITE);
        t.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        t.setFocusable(false);
        row.addView(t);

        TextView s = new TextView(ctx);
        String sub = SUBTITLES[index];
        if (index == 3) sub = themeManager.getCurrent().displayName + " · обои и эффекты";
        s.setText(sub);
        s.setTextColor(0xFF8FA0B5);
        s.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        s.setFocusable(false);
        LinearLayout.LayoutParams sLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        sLp.topMargin = (int) (2 * density);
        s.setLayoutParams(sLp);
        row.addView(s);

        return row;
    }

    private void onItemSelected(int position) {
        Context ctx = getContext();
        try {
            switch (position) {
                case 0: ctx.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); break;
                case 1: ctx.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS)); break;
                case 2: ctx.startActivity(new Intent(Settings.ACTION_SOUND_SETTINGS)); break;
                case 3: showThemePicker(); return;
                case 4: ctx.startActivity(new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)); break;
                case 5: ctx.startActivity(new Intent(Settings.ACTION_SETTINGS)); break;
            }
        } catch (Exception ignored) {}
        dismiss();
    }

    private void showThemePicker() {
        final String[] names = ThemeManager.themeDisplayNames();
        final String[] ids = ThemeManager.themeIds();
        int checked = 0;
        String currentId = themeManager.getCurrent().id;
        for (int i = 0; i < ids.length; i++) {
            if (ids[i].equals(currentId)) { checked = i; break; }
        }
        new android.app.AlertDialog.Builder(getContext())
                .setTitle("Оформление — тема")
                .setSingleChoiceItems(names, checked, (d, which) -> {
                    themeManager.setTheme(ids[which]);
                    if (callback != null) callback.onThemeChanged(ids[which]);
                    d.dismiss();
                    dismiss();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { dismiss(); return true; }
        return super.onKeyDown(keyCode, event);
    }
}
