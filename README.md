# CourseMarket

CourseMarket — Android-приложение для каталога образовательных курсов, демонстрационного оформления заказов и прохождения учебных материалов.

## Возможности

### Для клиента
- Регистрация и вход по локальной учётной записи.
- Каталог 12 курсов по направлениям «Разработка», «Дизайн» и «Геймдизайн».
- Поиск по названию, преподавателю и описанию, фильтрация и сортировка.
- Карточки с описанием, программой и результатами обучения.
- Избранное и корзина.
- Демонстрационное оформление заказа с выбором метода оплаты.
- 61 учебный материал: теория, ключевые идеи, пример, практика и контрольный вопрос.
- Сохранение прогресса и продолжение обучения.
- Профиль, список курсов и история заказов.

### Для администратора
- Панель администратора с отдельной ролью ADMIN.
- Создание, изменение и удаление курсов.
- Просмотр всех заказов и изменение их статуса.
- Ограничение удаления курсов, присутствующих в истории заказов, внешним ключом SQLite.

## Технологии и архитектура

- Kotlin, Android Views, AndroidX AppCompat.
- Material Components и Material 3 DayNight theme.
- ConstraintLayout, Fragments, BottomNavigationView.
- RecyclerView с CourseAdapter и ViewHolder.
- SQLiteOpenHelper с 9 связанными таблицами и FOREIGN KEY.
- CourseMarketRepository изолирует SQL от UI.
- CRUD и загрузка данных выполняются через Coroutines / Dispatchers.IO.
- SharedPreferences хранит локальную сессию пользователя; основные данные хранятся в SQLite.
- Учебные материалы находятся в CourseLearningContent.kt и загружаются в таблицу lessons.

Схема БД, связи, руководства и тест-план расположены в папке `doc/`.

## Сборка в Android Studio

Требуется:
- JDK 17.
- Android SDK Platform API 37.
- Android SDK Build-Tools 37.
- Android Studio с поддержкой Gradle wrapper проекта.

1. Открой проект в Android Studio.
2. Выполни Gradle Sync.
3. Выбери конфигурацию `app` и Android Emulator или подключённое устройство.
4. Нажми Run.

Windows PowerShell, сборка debug APK:
```powershell
.\gradlew.bat :app:assembleDebug
```

APK создаётся по пути:
`app/build/outputs/apk/debug/app-debug.apk`

Запуск unit-тестов:
```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Запуск instrumented tests на подключённом эмуляторе/устройстве:
```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

## GitHub Actions

Workflow **Android build and tests** выполняет unit-тесты и собирает debug APK. После успешного запуска архив `CourseMarket-debug-apk` доступен в разделе **Actions → workflow run → Artifacts**.

## Демо-доступ администратора

- Логин: `admin`
- Пароль: `admin123`

Это демонстрационная учётная запись. Перед публичным или коммерческим использованием пароль нужно заменить и перенести управление доступом на сервер.

## Установка на другом ПК или Android-устройстве

Подробная инструкция находится в [doc/install_and_run.md](doc/install_and_run.md).

- Другой Windows-ПК: клонировать репозиторий, открыть его в Android Studio, установить SDK API 37 и запустить Android Emulator.
- Android-телефон: скачать `app-debug.apk` из успешного GitHub Actions artifact, передать APK на устройство и установить.
- APK предназначен для Android и не запускается напрямую в Windows без эмулятора.

## Оставшиеся действия перед полной сдачей

- [Создать GitHub Projects Kanban board](https://github.com/qwempty123/AndroidProject/issues/1).
- [Добавить реальные скриншоты в README](https://github.com/qwempty123/AndroidProject/issues/2).
- [Опубликовать APK как GitHub Release v1.0.0](https://github.com/qwempty123/AndroidProject/issues/3).

## Документация

- [Анализ предметной области](doc/domain_analysis.md)
- [Схема базы данных и ER-диаграмма](doc/database_schema.md)
- [Руководство пользователя](doc/user_guide.md)
- [Руководство администратора](doc/admin_guide.md)
- [План тестирования](doc/test_plan.md)
- [Этапы разработки](doc/project_stages.md)
- [План Kanban](doc/kanban.md)
- [Установка и запуск на другом ПК](doc/install_and_run.md)
- [Требования к скриншотам](doc/screenshots.md)
- [Сценарий защиты](PRESENTATION_GUIDE.md)

## Ограничения демонстрационного прототипа

- SQLite хранится локально на устройстве; между устройствами данные не синхронизируются.
- Оплата — только демонстрационный сценарий. Нет списания денег, реального платежа по карте/СБП или реального счёта.
- Нет серверного API, облачных пользователей и видеохостинга.
- Пароль администратора по умолчанию предназначен только для демонстрации.
- Перед сдачей необходимо проверить успешную сборку и ручные тест-кейсы на целевом Android-устройстве.
- Реальные скриншоты запущенного приложения следует добавить в `screenshots/`; в текущем репозитории они пока не приложены.
