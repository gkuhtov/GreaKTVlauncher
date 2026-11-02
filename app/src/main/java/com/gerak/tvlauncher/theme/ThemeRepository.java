package com.gerak.tvlauncher.theme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ThemeRepository {

    public static final String ID_DEFAULT = "default";
    public static final String ID_PLAYSTATION = "playstation";

    private static final List<Theme> THEMES;

    static {
        List<Theme> list = new ArrayList<>();
        list.add(new Theme(
                ID_DEFAULT, "Default",
                0xFF141B2D, 0xFF0B0E17, 0xFF06070B,
                0xFF161C28, 0xFF2A3854, 0xFF212A3D, 0xFFFFFFFF,
                0xFF1A56DB, 0xFF2D4373, 0xFFFFFFFF, 0xFF9DA8BA, 0xFF58A6FF,
                1.08f, 10f, true
        ));
        list.add(new Theme(
                ID_PLAYSTATION, "PlayStation",
                0xFF0A1628, 0xFF071018, 0xFF04080E,
                0xFF0F1C30, 0xFF1A3A5C, 0xFF1A2A40, 0xFF7EC8FF,
                0xFF0070D1, 0xFF1A4A7A, 0xFFFFFFFF, 0xFF8FA8C0, 0xFF00A0FF,
                1.10f, 12f, true
        ));
        THEMES = Collections.unmodifiableList(list);
    }

    private ThemeRepository() {}

    public static List<Theme> getAll() { return THEMES; }

    public static Theme getById(String id) {
        if (id == null) return THEMES.get(0);
        for (Theme t : THEMES) {
            if (t.id.equals(id)) return t;
        }
        return THEMES.get(0);
    }

    public static Theme getDefault() { return THEMES.get(0); }
}
