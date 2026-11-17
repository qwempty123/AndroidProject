# Установка и запуск CourseMarket на другом компьютере

## Вариант A. Запуск на другом Windows-ПК через Android Studio

1. Установить Android Studio и JDK 17.
2. Установить Android SDK Platform API 37 и Android SDK Build-Tools 37 через SDK Manager.
3. Установить Git и GitHub Desktop (необязательно, можно использовать Git CLI).
4. Клонировать публичный репозиторий:
   `https://github.com/qwempty123/AndroidProject.git`
5. В Android Studio выбрать **Open** и открыть папку проекта.
6. Дождаться Gradle Sync. При первом запуске Gradle загрузит необходимые зависимости.
7. Открыть **Tools → Device Manager**, создать Android Emulator (например, Pixel), запустить его.
8. Выбрать конфигурацию `app` и нажать **Run**.

PowerShell из корня проекта:
```powershell
.\gradlew.bat :app:assembleDebug
```

## Вариант B. Установка APK на Android-телефон

1. Собрать debug APK командой выше или скачать артефакт GitHub Actions из успешного workflow.
2. В GitHub открыть **Actions → Android build and tests → успешный запуск → Artifacts → CourseMarket-debug-apk**.
3. Скачать ZIP-архив и извлечь `app-debug.apk`.
4. Передать APK на Android-телефон (USB, локальная сеть или облачное хранилище).
5. Открыть APK на телефоне и подтвердить установку из этого источника, если Android запросит разрешение.
6. Запустить CourseMarket.

APK — Android-приложение: напрямую в Windows без Android Emulator оно не запускается.

## Вариант C. Запуск через Android Emulator без Android Studio

На другом ПК можно установить Android SDK Platform Tools и Android Emulator, создать виртуальное устройство и установить APK через ADB:
```powershell
adb install -r app-debug.apk
```
Эмулятор должен быть запущен, а команда `adb devices` должна показывать подключённое устройство.

## Системные требования

- Android-устройство: Android 7.0 (API 24) или новее.
- Для сборки: Windows/macOS/Linux, JDK 17, Android SDK Platform 37, интернет при первой загрузке зависимостей.
- Для показа на ПК: Android Studio Emulator или отдельный Android-телефон.

## Важное о данных

SQLite-база создаётся отдельно на каждом устройстве. Пользователи, заказы и прогресс не синхронизируются между компьютерами и телефонами. Для общей системы обучения нужен удалённый backend и серверная БД.
