# GeraK TV Launcher

[![Android CI](https://github.com/gkuhtov/GreaKTVlauncher/actions/workflows/android-ci.yml/badge.svg)](https://github.com/gkuhtov/GreaKTVlauncher/actions/workflows/android-ci.yml)
[![Platform](https://img.shields.io/badge/platform-Android%20TV-3DDC84?logo=android&logoColor=white)](https://developer.android.com/tv)
[![Language](https://img.shields.io/badge/language-Java-orange?logo=openjdk&logoColor=white)](https://www.java.com/)

**GeraK TV Launcher** — компактный лаунчер для Android TV и устройств с Android, ориентированный на быстрый доступ к установленным приложениям и управление с пульта.

> Одна экосистема. Разные проекты. Общий характер.

## Возможности

- Единая сетка приложений Android TV и обычных Android-приложений, включая sideload.
- Навигация с помощью D-pad и визуальная подсветка выбранных элементов.
- Часы и дата на главном экране.
- Быстрый переход к системным настройкам.
- Тёмный интерфейс с полупрозрачными карточками и боковой панелью.

## Совместимость

| Параметр | Значение |
|---|---|
| Платформа | Android / Android TV |
| Минимальная версия | Android 5.0 (API 21) |
| Compile SDK | 34 |
| Target SDK | 34 |
| Язык | Java |
| Application ID | `com.gerak.tvlauncher` |

Поддержка отдельных устройств и особенностей прошивок зависит от реализации Android TV на конкретном устройстве.

## Сборка

Требуется JDK 17 и Android SDK с платформой API 34.

**Windows (PowerShell):**

```powershell
.\gradlew.bat assembleDebug
```

**Linux / macOS:**

```bash
chmod +x gradlew
./gradlew assembleDebug
```

Готовый APK:

```text
app/build/outputs/apk/debug/app-debug.apk
```

## Установка

Установите собранный APK привычным для вашего устройства способом. Чтобы назначить приложение лаунчером по умолчанию, откройте системный выбор приложения HOME и выберите **Arc TV / GeraK TV Launcher** (название может зависеть от установленной сборки и прошивки).

## Разрешения и приватность

Приложение использует `QUERY_ALL_PACKAGES`, чтобы отображать установленные приложения, включая приложения без Android TV Leanback-интерфейса. Это основная функция лаунчера.

В текущей реализации сетевые разрешения не объявлены. Список приложений обрабатывается локально на устройстве.

## Структура проекта

```text
.
├── app/
│   ├── src/main/
│   │   ├── java/com/gerak/tvlauncher/
│   │   │   ├── MainActivity.java
│   │   │   ├── AppModel.java
│   │   │   └── AppsAdapter.java
│   │   ├── res/
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/wrapper/
├── build.gradle
├── settings.gradle
└── gradlew
```

## Разработка

- Сохраняйте совместимость с Android TV и управлением с D-pad.
- Не меняйте `applicationId` и `minSdk` без отдельного обоснования.
- Проверяйте сборку через Gradle Wrapper.
- Не добавляйте сетевые запросы, сбор аналитики или передачу списка приложений без явной необходимости и документирования.

## Статус

Проект развивается. Автоматическая CI-сборка проверяет компиляцию debug-варианта; успешная сборка не заменяет проверку на реальном Android TV-устройстве.

---

**GeraK** · Самостоятельные проекты. Общие инженерные стандарты.
