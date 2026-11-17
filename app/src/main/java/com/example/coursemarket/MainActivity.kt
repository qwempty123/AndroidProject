package com.example.coursemarket

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Course(
    val id: Int,
    val title: String,
    val category: String,
    val author: String,
    val description: String,
    val price: Int,
    val rating: String,
    val duration: String,
    val level: String,
    val icon: String,
    val tint: String,
    val lessons: Int,
    val modules: List<String>,
    val outcomes: List<String>
)

class MainActivity : AppCompatActivity() {

    private val bg: Int get() = getColor(R.color.background)
    private val surface: Int get() = getColor(R.color.surface)
    private val surfaceAlt: Int get() = getColor(R.color.surface_alt)
    private val accent: Int get() = getColor(R.color.accent)
    private val accentDark: Int get() = getColor(R.color.accent_dark)
    private val textPrimary: Int get() = getColor(R.color.text_primary)
    private val textSecondary: Int get() = getColor(R.color.text_secondary)
    private val success: Int get() = getColor(R.color.success)
    private val gold: Int get() = getColor(R.color.gold)

    private var courses: List<Course> = listOf(
        Course(
            1, "Kotlin: от основ до Android-приложения", "Разработка", "Алексей Морозов",
            "Последовательно изучи Kotlin и собери Android-приложение с интерфейсом, навигацией и локальным хранением данных. В конце курса у тебя будет законченный учебный проект.",
            2490, "4.9", "18 часов", "Начальный", "K", "#55427D", 5,
            listOf("Знакомство с Kotlin и настройка среды", "Переменные, типы, условия и циклы", "Функции, классы, коллекции и null safety", "Android Views, события и навигация", "Хранение данных и финальный проект"),
            listOf("Писать читаемый код на Kotlin", "Создавать экраны Android-приложения", "Сохранять данные локально и собирать APK")
        ),
        Course(
            2, "UI/UX дизайн: интерфейсы с нуля", "Дизайн", "Мария Волкова",
            "Научись превращать задачу пользователя в понятный интерфейс. Разберёшься с исследованиями, пользовательскими сценариями, сетками, типографикой и визуальной иерархией.",
            1990, "4.8", "12 часов", "Начальный", "✦", "#754B69", 5,
            listOf("Основы UX и анализ аудитории", "Пользовательские сценарии и wireframes", "Сетка, композиция, цвет и типографика", "UI-компоненты и дизайн-система", "Прототипирование и проверка решения"),
            listOf("Проектировать понятные пользовательские потоки", "Создавать цельные интерфейсы", "Обосновывать визуальные решения")
        ),
        Course(
            3, "Unity: разработка 2D-игр на C#", "Разработка", "Игорь Лебедев",
            "Создай основу собственной 2D-игры: контроллер персонажа, физику, боевую систему, анимации, врагов и игровые сцены. Практика построена вокруг небольшого игрового проекта.",
            3290, "5.0", "26 часов", "Начальный", "◈", "#365F70", 6,
            listOf("Структура проекта и основы Unity", "C#, компоненты и жизненный цикл", "Rigidbody2D, коллизии и контроллер игрока", "Анимации, атаки и система здоровья", "AI противников и организация уровней", "Сборка и полировка игрового прототипа"),
            listOf("Создавать управляемого 2D-персонажа", "Настраивать анимации и боевые столкновения", "Собирать игровой прототип от начала до конца")
        ),
        Course(
            4, "Python для практических задач", "Разработка", "Денис Ким",
            "Освой Python на примерах реальных задач: обработка файлов, автоматизация повторяющихся действий, работа с данными и внешними API.",
            1790, "4.7", "15 часов", "Начальный", "Py", "#42664F", 5,
            listOf("Синтаксис, переменные и базовые типы", "Условия, циклы и функции", "Списки, словари и работа с файлами", "Исключения и структура проекта", "HTTP-запросы и небольшой автоматизатор"),
            listOf("Писать скрипты для повседневных задач", "Обрабатывать файлы и коллекции данных", "Получать данные из простых API")
        ),
        Course(
            5, "Blender: 3D-моделирование", "Дизайн", "Елена Орлова",
            "Пройди путь от простых форм до законченной стилизованной сцены. Изучишь моделирование, материалы, свет, композицию и финальный рендер.",
            2890, "4.9", "21 час", "Средний", "⬡", "#755A3A", 5,
            listOf("Навигация и рабочее пространство Blender", "Базовое и hard-surface моделирование", "Модификаторы и чистая геометрия", "Материалы, текстуры и освещение", "Композиция, камера и финальный рендер"),
            listOf("Моделировать простые игровые ассеты", "Настраивать материалы и освещение", "Подготавливать сцену к финальному рендеру")
        ),
        Course(
            6, "Геймдизайн: от идеи до прототипа", "Геймдизайн", "Павел Соколов",
            "Разберись, почему игровые механики работают. Сформируй core loop, опиши правила, спроектируй уровни и проверь идею на простом прототипе.",
            2190, "4.8", "14 часов", "Любой", "⚔", "#70464F", 5,
            listOf("Роль геймдизайнера и анализ референсов", "Core loop, механики и игровые системы", "Баланс, прогрессия и обратная связь", "Дизайн уровней и кривая сложности", "Документация и проверка прототипа"),
            listOf("Формулировать игровую концепцию", "Описывать механики и игровой цикл", "Собирать понятный дизайн-документ")
        ),
        Course(
            7, "Figma: профессиональный workflow", "Дизайн", "Анна Белова",
            "Освой Figma для повседневной продуктовой работы: компоненты, варианты, Auto Layout, переменные, интерактивные прототипы и подготовка макетов к передаче.",
            1490, "4.8", "9 часов", "Начальный", "F", "#514B83", 5,
            listOf("Рабочее пространство и организация файлов", "Auto Layout и адаптивные компоненты", "Variants, variables и библиотеки", "Интерактивные прототипы", "Подготовка макетов к handoff"),
            listOf("Создавать переиспользуемые компоненты", "Быстро собирать адаптивные экраны", "Передавать макеты разработчикам")
        ),
        Course(
            8, "C# для разработчиков игр", "Разработка", "Максим Романов",
            "Изучи фундамент C# с акцентом на игровые проекты: типы, ООП, коллекции, события, интерфейсы и асинхронность. Примеры ориентированы на Unity.",
            2290, "4.9", "17 часов", "Начальный", "C#", "#3E5B79", 5,
            listOf("Синтаксис, типы и управление потоком", "Методы, классы и объектная модель", "Наследование, интерфейсы и композиция", "Коллекции, LINQ и события", "Практика: архитектура игровой системы"),
            listOf("Понимать базовые принципы ООП", "Разделять ответственность классов", "Использовать события и коллекции в проекте")
        ),
        Course(
            9, "Веб-разработка: HTML, CSS и JavaScript", "Разработка", "Никита Орлов",
            "Собери адаптивную веб-страницу и добавь ей интерактивность. Курс охватывает семантическую разметку, стили, основы JavaScript и публикацию проекта.",
            1890, "4.8", "20 часов", "Начальный", "</>", "#3D6470", 5,
            listOf("HTML и семантическая структура", "CSS, Flexbox и Grid", "Адаптивная верстка и доступность", "JavaScript и обработка событий", "Финальная страница и публикация"),
            listOf("Создавать адаптивные страницы", "Добавлять интерактивность на JavaScript", "Публиковать готовый сайт")
        ),
        Course(
            10, "Motion design: анимация интерфейсов", "Дизайн", "София Миронова",
            "Научись оживлять статичный дизайн: принципы движения, тайминг, easing, микровзаимодействия и экспорт анимаций для цифровых продуктов.",
            2390, "4.9", "16 часов", "Средний", "▶", "#76513F", 5,
            listOf("Принципы анимации и визуальный ритм", "Ключевые кадры и кривые движения", "Переходы и микровзаимодействия", "Анимация интерфейсных состояний", "Экспорт и подготовка материалов"),
            listOf("Понимать тайминг и easing", "Создавать короткие UI-анимации", "Подготавливать анимации для продукта")
        ),
        Course(
            11, "Pixel Art: графика для 2D-игр", "Геймдизайн", "Артём Власов",
            "Создавай выразительную пиксельную графику для игр. Поработаешь с палитрами, силуэтами, тайлами, персонажами и простыми циклами анимации.",
            1690, "4.9", "11 часов", "Начальный", "▦", "#536C59", 5,
            listOf("Пиксельная сетка и рабочий процесс", "Ограниченная палитра и светотень", "Силуэт и дизайн персонажа", "Тайлы и бесшовные окружения", "Idle, walk и простая атака"),
            listOf("Создавать читаемые игровые спрайты", "Работать с палитрой и силуэтом", "Рисовать простые циклы анимации")
        ),
        Course(
            12, "Product Design: проектирование продукта", "Дизайн", "Кирилл Андреев",
            "Посмотри на дизайн шире отдельных экранов: исследуй проблему, сформулируй гипотезу, спроектируй решение и оцени его по пользовательскому сценарию.",
            2690, "4.8", "19 часов", "Средний", "◎", "#62517B", 5,
            listOf("Проблема, аудитория и продуктовый контекст", "Интервью и карта пользовательского пути", "Гипотезы и приоритизация решений", "Прототип и проверка сценария", "Кейс и презентация результата"),
            listOf("Формулировать продуктовую проблему", "Проводить базовое исследование", "Собирать и презентовать дизайн-кейс")
        )
    )

    private lateinit var root: LinearLayout
    private lateinit var contentBox: LinearLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private val repository by lazy { CourseMarketRepository(applicationContext) }
    private val uiScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var currentUser: UserAccount? = null
    private var currentLearningCourseId = -1
    private var currentLearningLessonIndex = -1
    private var restoredTab = "Главная"
    private var restoredCategory = "Все"
    private var restoredSearch = ""
    private var restoredSort = "Популярные"
    private var currentTab = "Главная"
    private var selectedCategory = "Все"
    private var selectedSort = "Популярные"
    private var searchQuery = ""
    private val favorites = mutableSetOf<Int>()
    private val cart = mutableSetOf<Int>()
    private val purchased = mutableSetOf<Int>()
    private val completedLessons = mutableMapOf<Int, MutableSet<Int>>()
    private var selectedPaymentMethod = "card"
    private val prefs by lazy { getSharedPreferences("course_market", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        restoredTab = savedInstanceState?.getString("screen_tab") ?: "Главная"
        restoredCategory = savedInstanceState?.getString("screen_category") ?: "Все"
        restoredSearch = savedInstanceState?.getString("screen_search") ?: ""
        restoredSort = savedInstanceState?.getString("screen_sort") ?: "Популярные"
        currentLearningCourseId = savedInstanceState?.getInt("learning_course_id", -1) ?: -1
        currentLearningLessonIndex = savedInstanceState?.getInt("learning_lesson_index", -1) ?: -1

        uiScope.launch {
            try {
                repository.initialize(courses)
                val sessionId = prefs.getLong("session_user_id", -1L)
                val account = if (sessionId > 0) repository.userById(sessionId) else null
                if (account != null) {
                    enterAuthenticatedApp(account, restore = true)
                } else {
                    currentUser = null
                    showAuthScreen()
                }
            } catch (error: Exception) {
                showStartupError(error)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("screen_tab", currentTab)
        outState.putString("screen_category", selectedCategory)
        outState.putString("screen_search", searchQuery)
        outState.putString("screen_sort", selectedSort)
        outState.putInt("learning_course_id", currentLearningCourseId)
        outState.putInt("learning_lesson_index", currentLearningLessonIndex)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        uiScope.cancel()
        super.onDestroy()
    }

    private suspend fun enterAuthenticatedApp(account: UserAccount, restore: Boolean) {
        currentUser = account
        prefs.edit()
            .putLong("session_user_id", account.id)
            .putString("session_username", account.username)
            .apply()
        courses = repository.getCourses().ifEmpty { courses }
        loadState()
        buildShell()
        if (!restore) {
            currentTab = "Главная"
            showHome()
            return
        }
        selectedCategory = restoredCategory
        searchQuery = restoredSearch
        selectedSort = restoredSort
        when (restoredTab) {
            "Каталог" -> showCatalog()
            "Избранное" -> showFavorites()
            "Корзина" -> showCart()
            "Профиль" -> {
                val course = courses.firstOrNull { it.id == currentLearningCourseId }
                when {
                    course != null && currentLearningLessonIndex >= 0 ->
                        showLesson(course, currentLearningLessonIndex)
                    course != null -> showCourseLearning(course)
                    else -> showProfile()
                }
            }
            else -> showHome()
        }
    }

    private suspend fun loadState() {
        val userId = currentUser?.id ?: return
        favorites.clear()
        favorites.addAll(repository.favoriteCourseIds(userId))
        cart.clear()
        cart.addAll(repository.cartCourseIds(userId))
        purchased.clear()
        purchased.addAll(repository.purchasedCourseIds(userId))
        completedLessons.clear()
        completedLessons.putAll(repository.completedLessonIndexes(userId))
        courses.forEach { course ->
            completedLessons.putIfAbsent(course.id, mutableSetOf())
        }
    }

    private fun readIds(key: String): Set<Int> =
        prefs.getString(key, "").orEmpty()
            .split(",")
            .mapNotNull { it.toIntOrNull() }
            .filter { id -> courses.any { it.id == id } }
            .toSet()

    private fun saveState() {
        prefs.edit()
            .putString("favorites", favorites.joinToString(","))
            .putString("cart", cart.joinToString(","))
            .putString("purchased", purchased.joinToString(","))
            .apply()

        val userId = currentUser?.id ?: return
        val favoriteSnapshot = favorites.toSet()
        val cartSnapshot = cart.toSet()
        uiScope.launch {
            try {
                repository.replaceFavoritesAndCart(userId, favoriteSnapshot, cartSnapshot)
            } catch (error: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Не удалось сохранить корзину или избранное.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showStartupError(error: Exception) {
        Toast.makeText(
            this,
            getString(R.string.database_error) + " " + (error.message ?: ""),
            Toast.LENGTH_LONG
        ).show()
        showAuthScreen()
    }

    private fun showAuthScreen(registerMode: Boolean = false, message: String? = null) {
        val authRoot = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(bg)
            setPadding(dp(22), dp(18), dp(22), dp(18))
        }
        val scroll = ScrollView(this).apply {
            isFillViewport = true
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 22)
            setPadding(dp(20), dp(22), dp(20), dp(22))
        }
        card.addView(text("CourseMarket", 27, textPrimary, true))
        card.addView(vSpace(6))
        card.addView(text(getString(R.string.auth_subtitle), 12, textSecondary))
        card.addView(vSpace(18))
        card.addView(text(
            if (registerMode) getString(R.string.auth_register) else getString(R.string.auth_title),
            20, textPrimary, true
        ))
        card.addView(vSpace(13))

        val username = checkoutInput(getString(R.string.auth_username), InputType.TYPE_CLASS_TEXT, "")
        val password = checkoutInput(
            getString(R.string.auth_password),
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD,
            ""
        )
        card.addView(username, matchWrap())
        card.addView(vSpace(9))
        card.addView(password, matchWrap())
        card.addView(vSpace(12))

        val feedback = text(message.orEmpty(), 11, Color.rgb(255, 130, 140))
        card.addView(feedback)
        card.addView(vSpace(5))
        card.addView(button(
            if (registerMode) getString(R.string.auth_register) else getString(R.string.auth_login),
            true
        ) {
            val loginValue = username.text.toString().trim()
            val passwordValue = password.text.toString()
            feedback.text = ""
            uiScope.launch {
                try {
                    val account = if (registerMode) {
                        repository.register(loginValue, passwordValue)
                    } else {
                        repository.login(loginValue, passwordValue)
                    }
                    if (account == null) {
                        feedback.text = getString(R.string.auth_login_error)
                    } else {
                        enterAuthenticatedApp(account, restore = false)
                    }
                } catch (error: Exception) {
                    feedback.text = error.message ?: "Не удалось выполнить вход."
                }
            }
        }, LinearLayout.LayoutParams(-1, dp(48)))
        card.addView(vSpace(10))
        card.addView(button(
            if (registerMode) getString(R.string.auth_switch_to_login)
            else getString(R.string.auth_switch_to_register),
            false
        ) {
            showAuthScreen(!registerMode)
        }, LinearLayout.LayoutParams(-1, dp(44)))
        card.addView(vSpace(16))
        card.addView(text(getString(R.string.auth_demo_admin), 10, gold))
        card.addView(text("Демо-аккаунт администратора предназначен только для локальной защиты проекта.", 10, textSecondary))
        scroll.addView(card)
        authRoot.addView(scroll, LinearLayout.LayoutParams(-1, -1))
        setContentView(authRoot)
    }

    private fun logout() {
        prefs.edit().remove("session_user_id").remove("session_username").apply()
        currentUser = null
        favorites.clear()
        cart.clear()
        purchased.clear()
        completedLessons.clear()
        showAuthScreen()
    }

    private fun buildShell() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = bg
        window.navigationBarColor = bg

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(18), dp(8), dp(18), dp(8))
        }
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(dp(18), bars.top + dp(8), dp(18), bars.bottom + dp(8))
            insets
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(4), 0, dp(14))
        }
        val brandBlock = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }
        brandBlock.addView(text("CourseMarket", 22, textPrimary, true))
        brandBlock.addView(text("ОБУЧЕНИЕ • ПРАКТИКА • РОСТ", 9, textSecondary, true))
        header.addView(brandBlock, LinearLayout.LayoutParams(0, dp(48), 1f))
        header.addView(button("▣  " + cart.size, false) { navigate("Корзина") })
        root.addView(header)

        val divider = View(this).apply { setBackgroundColor(Color.rgb(49, 47, 62)) }
        root.addView(divider, LinearLayout.LayoutParams(-1, dp(1)))

        val scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
            overScrollMode = View.OVER_SCROLL_NEVER
            setPadding(0, dp(10), 0, dp(14))
        }
        contentBox = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            clipChildren = false
            clipToPadding = false
        }
        scroll.addView(contentBox)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        bottomNavigation = BottomNavigationView(this).apply {
            background = rounded(surface, 18)
            labelVisibilityMode = BottomNavigationView.LABEL_VISIBILITY_LABELED
            itemHorizontalTranslationEnabled = false
            menu.add(0, NAV_HOME, 0, getString(R.string.nav_home))
                .setIcon(android.R.drawable.ic_menu_view)
            menu.add(0, NAV_CATALOG, 1, getString(R.string.nav_catalog))
                .setIcon(android.R.drawable.ic_menu_search)
            menu.add(0, NAV_FAVORITES, 2, getString(R.string.nav_favorites))
                .setIcon(android.R.drawable.btn_star_big_off)
            menu.add(0, NAV_CART, 3, getString(R.string.nav_cart))
                .setIcon(android.R.drawable.ic_menu_agenda)
            menu.add(0, NAV_PROFILE, 4, getString(R.string.nav_profile))
                .setIcon(android.R.drawable.ic_menu_myplaces)
            setOnItemSelectedListener { item ->
                val tab = when (item.itemId) {
                    NAV_HOME -> "Главная"
                    NAV_CATALOG -> "Каталог"
                    NAV_FAVORITES -> "Избранное"
                    NAV_CART -> "Корзина"
                    else -> "Профиль"
                }
                navigate(tab)
                true
            }
        }
        root.addView(bottomNavigation, LinearLayout.LayoutParams(-1, dp(66)))
        setContentView(root)
        ViewCompat.requestApplyInsets(root)
        refreshHeaderAndNav()
    }

    private fun navigate(tab: String) {
        currentTab = tab
        if (::bottomNavigation.isInitialized) {
            val itemId = when (tab) {
                "Главная" -> NAV_HOME
                "Каталог" -> NAV_CATALOG
                "Избранное" -> NAV_FAVORITES
                "Корзина" -> NAV_CART
                else -> NAV_PROFILE
            }
            bottomNavigation.menu.findItem(itemId)?.isChecked = true
        }
        when (tab) {
            "Главная" -> showHome()
            "Каталог" -> showCatalog()
            "Избранное" -> showFavorites()
            "Корзина" -> showCart()
            "Админ" -> showAdmin()
            else -> showProfile()
        }
    }

    private fun render(title: String, subtitle: String? = null) {
        contentBox.removeAllViews()
        val heading = text(title, 27, textPrimary, true)
        heading.setPadding(0, dp(8), 0, dp(3))
        contentBox.addView(heading)
        if (subtitle != null) {
            val sub = text(subtitle, 13, textSecondary)
            sub.setPadding(0, 0, 0, dp(17))
            contentBox.addView(sub)
        } else {
            contentBox.addView(vSpace(14))
        }
        refreshHeaderAndNav()
    }

    private fun refreshHeaderAndNav() {
        if (!::root.isInitialized) return
        val header = root.getChildAt(0) as? LinearLayout ?: return
        val cartButton = header.getChildAt(1) as? TextView
        cartButton?.text = "▣  " + cart.size
        if (::bottomNavigation.isInitialized) {
            bottomNavigation.menu.findItem(
                when (currentTab) {
                    "Каталог" -> NAV_CATALOG
                    "Избранное" -> NAV_FAVORITES
                    "Корзина" -> NAV_CART
                    "Профиль", "Админ" -> NAV_PROFILE
                    else -> NAV_HOME
                }
            )?.isChecked = true
        }
    }

    private fun showHome() {
        currentTab = "Главная"
        render("Учись новому.", "Подборка практических курсов для твоего следующего шага.")
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = gradient("#302747", "#4B3C68", 22)
            setPadding(dp(20), dp(19), dp(20), dp(19))
        }
        hero.addView(badge("ТВОЁ СЛЕДУЮЩЕЕ НАЧИНАНИЕ", accentDark, accent))
        hero.addView(vSpace(13))
        hero.addView(text("Прокачай навыки.\nСоздай больше.", 27, textPrimary, true))
        hero.addView(vSpace(8))
        hero.addView(text("Курсы по разработке, дизайну и созданию игр — с понятной программой и практикой.", 13, Color.rgb(222, 216, 238)))
        hero.addView(vSpace(17))
        hero.addView(button("Открыть каталог  →", true) { navigate("Каталог") })
        contentBox.addView(hero, matchWrap())

        val resumeCourse = courses.firstOrNull {
            purchased.contains(it.id) && courseProgress(it) < 100
        }
        if (resumeCourse != null) {
            contentBox.addView(vSpace(10))
            val resumeCard = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = rounded(surface, 15)
                setPadding(dp(15), dp(13), dp(15), dp(13))
            }
            resumeCard.addView(text("ПРОДОЛЖИТЬ ОБУЧЕНИЕ", 9, accent, true))
            resumeCard.addView(vSpace(5))
            resumeCard.addView(text(resumeCourse.title, 14, textPrimary, true))
            resumeCard.addView(vSpace(9))
            resumeCard.addView(button("Вернуться к уроку  →", true) {
                showCourseLearning(resumeCourse)
            })
            contentBox.addView(resumeCard, matchWrap())
        }

        val quickStats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        quickStats.addView(quickInfo("12", "курсов"), weighted(1f))
        quickStats.addView(quickInfo("3", "направления"), weighted(1f))
        quickStats.addView(quickInfo("Офлайн", "каталог локально"), weighted(1f))
        contentBox.addView(quickStats, matchWrap())

        sectionTitle("Популярное сейчас", "Выбор редакции")
        listOf(3, 1, 2).forEach { id -> courses.first { it.id == id }.let { addCourseCard(it) } }

        sectionTitle("Выбери направление", "Начни с того, что интересно")
        listOf(
            "Разработка" to "Код, приложения и технологии",
            "Дизайн" to "Интерфейсы, графика и визуал",
            "Геймдизайн" to "Механики, уровни и игровые миры"
        ).forEach { (category, description) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = rounded(surface, 15)
                setPadding(dp(15), dp(13), dp(13), dp(13))
                isClickable = true
                isFocusable = true
                setOnClickListener {
                    selectedCategory = category
                    navigate("Каталог")
                }
            }
            val labels = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            labels.addView(text(category, 15, textPrimary, true))
            labels.addView(text(description, 11, textSecondary))
            row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(text("→", 21, accent, true))
            contentBox.addView(row, matchWrap())
            contentBox.addView(vSpace(8))
        }

        sectionTitle("Почему CourseMarket", "Учиться проще, когда есть структура")
        val benefits = listOf(
            "01" to ("Понятная программа" to "Каждый курс разбит на последовательные модули."),
            "02" to ("Практический результат" to "Знания закрепляются заданиями и проектами."),
            "03" to ("Всё в одном месте" to "Избранное, корзина и покупки сохраняются на устройстве.")
        )
        benefits.forEach { (number, content) ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                background = rounded(surface, 14)
                setPadding(dp(14), dp(13), dp(14), dp(13))
                gravity = Gravity.CENTER_VERTICAL
            }
            row.addView(text(number, 20, accent, true), LinearLayout.LayoutParams(dp(42), -2))
            val labels = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            labels.addView(text(content.first, 14, textPrimary, true))
            labels.addView(vSpace(3))
            labels.addView(text(content.second, 11, textSecondary))
            row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
            contentBox.addView(row, matchWrap())
            contentBox.addView(vSpace(8))
        }
    }

    private fun showCatalog() {
        currentTab = "Каталог"
        render("Каталог курсов", courses.size.toString() + " программ в трёх направлениях")
        val search = EditText(this).apply {
            hint = "Поиск по курсу, автору или теме"
            setHintTextColor(textSecondary)
            setTextColor(textPrimary)
            textSize = 14f
            includeFontPadding = false
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT
            imeOptions = EditorInfo.IME_ACTION_SEARCH
            setPadding(dp(15), 0, dp(15), 0)
            background = rounded(surface, 14)
            setText(searchQuery)
        }
        contentBox.addView(search, LinearLayout.LayoutParams(-1, dp(52)))
        contentBox.addView(vSpace(13))

        val chipScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }
        val chipRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf("Все", "Разработка", "Дизайн", "Геймдизайн").forEach { category ->
            val chip = button(category, selectedCategory == category) {
                selectedCategory = category
                showCatalog()
            }
            val lp = LinearLayout.LayoutParams(-2, dp(40))
            lp.marginEnd = dp(7)
            chipRow.addView(chip, lp)
        }
        chipScroll.addView(chipRow)
        contentBox.addView(chipScroll)
        contentBox.addView(vSpace(12))

        val sortRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        sortRow.addView(text("Сортировка", 12, textSecondary), LinearLayout.LayoutParams(0, -2, 1f))
        listOf("Популярные", "Дешевле", "Дороже").forEach { sort ->
            val active = selectedSort == sort
            val chip = button(sort, active, compact = true) {
                selectedSort = sort
                showCatalog()
            }
            val lp = LinearLayout.LayoutParams(-2, dp(34))
            lp.marginStart = dp(5)
            sortRow.addView(chip, lp)
        }
        contentBox.addView(sortRow)
        contentBox.addView(vSpace(14))

        val results = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        contentBox.addView(results)

        fun updateResults(query: String) {
            results.removeAllViews()
            val filtered = courses.filter {
                (selectedCategory == "Все" || it.category == selectedCategory) &&
                    (it.title.contains(query, true) ||
                        it.author.contains(query, true) ||
                        it.category.contains(query, true) ||
                        it.description.contains(query, true))
            }.let { list ->
                when (selectedSort) {
                    "Дешевле" -> list.sortedBy { it.price }
                    "Дороже" -> list.sortedByDescending { it.price }
                    else -> list.sortedByDescending { it.rating.toDoubleOrNull() ?: 0.0 }
                }
            }
            results.addView(text("Найдено: " + filtered.size, 11, textSecondary))
            results.addView(vSpace(9))
            if (filtered.isEmpty()) {
                results.addView(emptyState("Ничего не найдено", getString(R.string.empty_search)))
            } else {
                val recycler = RecyclerView(this).apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    isNestedScrollingEnabled = false
                    overScrollMode = View.OVER_SCROLL_NEVER
                    itemAnimator = null
                    adapter = CourseAdapter(
                        onFavorite = { toggleFavorite(it) },
                        onCart = { course ->
                            when {
                                purchased.contains(course.id) -> showCourseLearning(course)
                                cart.contains(course.id) -> navigate("Корзина")
                                else -> addToCart(course)
                            }
                        },
                        onDetails = { showDetails(it) }
                    ).also { it.submitItems(filtered, favorites, cart, purchased) }
                }
                results.addView(recycler, LinearLayout.LayoutParams(-1, -2))
            }
        }

        updateResults(searchQuery)
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                updateResults(searchQuery)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showFavorites() {
        currentTab = "Избранное"
        render("Избранное", "Сохранённые программы — всегда под рукой.")
        val list = courses.filter { favorites.contains(it.id) }
        if (list.isEmpty()) {
            contentBox.addView(emptyState("Пока ничего нет", "Нажми на ♡ в карточке курса, чтобы сохранить его здесь."))
            contentBox.addView(vSpace(12))
            contentBox.addView(button("Найти курс", true) { navigate("Каталог") })
        } else {
            contentBox.addView(text(list.size.toString() + " сохранено", 12, textSecondary))
            contentBox.addView(vSpace(10))
            list.forEach { addCourseCard(it) }
        }
    }

    private fun showCart() {
        currentTab = "Корзина"
        render("Корзина", "Проверь выбранные курсы перед оформлением.")
        val items = courses.filter { cart.contains(it.id) && !purchased.contains(it.id) }
        if (items.isEmpty()) {
            contentBox.addView(emptyState("Корзина пуста", "Выбери подходящий курс в каталоге — он появится здесь."))
            contentBox.addView(vSpace(12))
            contentBox.addView(button("Перейти в каталог", true) { navigate("Каталог") })
            return
        }

        contentBox.addView(text(items.size.toString() + " " + pluralCourses(items.size), 12, textSecondary))
        contentBox.addView(vSpace(9))
        items.forEach { course ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = rounded(surface, 16)
                setPadding(dp(13), dp(13), dp(11), dp(13))
            }
            val colorTile = TextView(this).apply {
                text = course.icon
                textSize = 20f
                includeFontPadding = false
                gravity = Gravity.CENTER
                setTextColor(textPrimary)
                background = rounded(Color.parseColor(course.tint), 12)
            }
            row.addView(colorTile, LinearLayout.LayoutParams(dp(48), dp(48)))
            val labels = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(11), 0, dp(6), 0)
            }
            labels.addView(text(course.title, 13, textPrimary, true).apply { maxLines = 2 })
            labels.addView(vSpace(4))
            labels.addView(text(course.duration + "  •  " + money(course.price), 11, accent, true))
            row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(button("×", false, compact = true) {
                cart.remove(course.id)
                saveState()
                showCart()
            }, LinearLayout.LayoutParams(dp(40), dp(40)))
            contentBox.addView(row, matchWrap())
            contentBox.addView(vSpace(9))
        }

        val total = items.sumOf { it.price }
        val summary = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surfaceAlt, 18)
            setPadding(dp(17), dp(16), dp(17), dp(17))
        }
        summary.addView(text("ИТОГО К ОПЛАТЕ", 10, textSecondary, true))
        summary.addView(vSpace(5))
        summary.addView(text(money(total), 28, textPrimary, true))
        summary.addView(vSpace(6))
        summary.addView(text("Оплата в приложении не подключена. Оформление демонстрационное.", 11, textSecondary))
        summary.addView(vSpace(13))
        summary.addView(button("Оформить заказ  →", true) { checkout(items) })
        contentBox.addView(vSpace(5))
        contentBox.addView(summary)
    }

    private fun checkout(items: List<Course>) {
        showCheckout(items)
    }

    private fun showCheckout(items: List<Course>) {
        currentTab = "Корзина"
        render("Оформление заказа", "Проверь состав заказа и выбери демонстрационный способ оплаты.")
        contentBox.addView(button("← Вернуться в корзину", false) { showCart() }, matchWrap())
        contentBox.addView(vSpace(16))

        sectionTitle("Данные покупателя", "Нужны для формирования демонстрационного чека")
        val nameInput = checkoutInput("Имя и фамилия", InputType.TYPE_CLASS_TEXT,
            prefs.getString("buyer_name", "").orEmpty())
        val emailInput = checkoutInput("Электронная почта", InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS,
            prefs.getString("buyer_email", "").orEmpty())
        contentBox.addView(nameInput, matchWrap())
        contentBox.addView(vSpace(9))
        contentBox.addView(emailInput, matchWrap())
        contentBox.addView(vSpace(5))

        sectionTitle("Способ оплаты", "Выбери, как должен выглядеть сценарий оплаты")
        contentBox.addView(text(
            "ДЕМО-РЕЖИМ: реальные деньги не списываются. Не вводи номер карты, CVC или банковские пароли.",
            11, gold
        ))
        contentBox.addView(vSpace(10))
        val paymentOptions = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        contentBox.addView(paymentOptions)
        renderPaymentOptions(paymentOptions)

        sectionTitle("Состав заказа")
        items.forEach { course ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, dp(5), 0, dp(5))
            }
            row.addView(text(course.title, 12, textPrimary).apply { maxLines = 2 },
                LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(text(money(course.price), 13, textPrimary, true))
            contentBox.addView(row)
        }

        val total = items.sumOf { it.price }
        val summary = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surfaceAlt, 16)
            setPadding(dp(16), dp(15), dp(16), dp(15))
        }
        summary.addView(text("ИТОГО", 10, textSecondary, true))
        summary.addView(vSpace(4))
        summary.addView(text(money(total), 27, textPrimary, true))
        contentBox.addView(vSpace(8))
        contentBox.addView(summary, matchWrap())
        contentBox.addView(vSpace(12))

        var termsAccepted = false
        val terms = text("□  Подтверждаю заказ и понимаю, что оплата работает в демонстрационном режиме.", 12, textSecondary)
        terms.setPadding(dp(4), dp(8), dp(4), dp(8))
        terms.isClickable = true
        terms.setOnClickListener {
            termsAccepted = !termsAccepted
            terms.text = if (termsAccepted) {
                "☑  Подтверждаю заказ и понимаю, что оплата работает в демонстрационном режиме."
            } else {
                "□  Подтверждаю заказ и понимаю, что оплата работает в демонстрационном режиме."
            }
            terms.setTextColor(if (termsAccepted) success else textSecondary)
        }
        contentBox.addView(terms)
        val validation = text("", 11, Color.rgb(255, 120, 130))
        validation.setPadding(dp(3), dp(3), dp(3), dp(8))
        contentBox.addView(validation)
        contentBox.addView(button("Подтвердить демонстрационный заказ  →", true) {
            val buyerName = nameInput.text.toString().trim()
            val buyerEmail = emailInput.text.toString().trim()
            when {
                buyerName.length < 2 -> validation.text = "Укажи имя покупателя (не менее двух символов)."
                !Patterns.EMAIL_ADDRESS.matcher(buyerEmail).matches() ->
                    validation.text = "Укажи корректный адрес электронной почты."
                !termsAccepted -> validation.text = "Подтверди, что понимаешь демонстрационный режим оплаты."
                items.isEmpty() -> validation.text = "Корзина пуста."
                else -> completeDemoOrder(items, buyerName, buyerEmail, selectedPaymentMethod)
            }
        }, LinearLayout.LayoutParams(-1, dp(50)))
        contentBox.addView(vSpace(18))
        contentBox.addView(text(
            "Способы оплаты здесь являются только вариантами демонстрации интерфейса. Для приёма денег потребуется подключить сервер и платёжного провайдера.",
            10, textSecondary
        ))
    }

    private fun checkoutInput(hintText: String, input: Int, initialValue: String): EditText =
        EditText(this).apply {
            hint = hintText
            setHintTextColor(textSecondary)
            setTextColor(textPrimary)
            textSize = 14f
            includeFontPadding = false
            setSingleLine(true)
            inputType = input
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(surface, 12)
            setText(initialValue)
        }

    private fun renderPaymentOptions(container: LinearLayout) {
        container.removeAllViews()
        val options = listOf(
            Triple("card", "Банковская карта", "Тестовый сценарий карты · без ввода реквизитов"),
            Triple("sbp", "СБП", "Демонстрация перевода по QR · без реального платежа"),
            Triple("invoice", "Счёт на оплату", "Формирование локального демонстрационного заказа")
        )
        options.forEach { (id, title, description) ->
            val chosen = selectedPaymentMethod == id
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = rounded(if (chosen) accentDark else surface, 13)
                setPadding(dp(13), dp(12), dp(13), dp(12))
                isClickable = true
                isFocusable = true
            }
            val marker = text(if (chosen) "●" else "○", 18, if (chosen) accent else textSecondary, true)
            row.addView(marker, LinearLayout.LayoutParams(dp(30), -2))
            val labels = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            labels.addView(text(title, 13, textPrimary, true))
            labels.addView(vSpace(3))
            labels.addView(text(description, 10, textSecondary))
            row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
            row.setOnClickListener {
                selectedPaymentMethod = id
                renderPaymentOptions(container)
            }
            container.addView(row, matchWrap())
            container.addView(vSpace(7))
        }
    }

    private fun paymentMethodName(method: String): String = when (method) {
        "sbp" -> "СБП (демо)"
        "invoice" -> "Счёт на оплату (демо)"
        else -> "Банковская карта (демо)"
    }

    private fun completeDemoOrder(
        items: List<Course>,
        buyerName: String,
        buyerEmail: String,
        paymentMethod: String
    ) {
        val account = currentUser ?: run {
            Toast.makeText(this, "Войди в аккаунт, чтобы оформить заказ.", Toast.LENGTH_SHORT).show()
            return
        }
        val total = items.sumOf { it.price }
        uiScope.launch {
            try {
                val orderNumber = repository.createDemoOrder(
                    account.id,
                    buyerName,
                    buyerEmail,
                    paymentMethodName(paymentMethod),
                    items
                )
                items.forEach { course ->
                    purchased.add(course.id)
                    cart.remove(course.id)
                }
                prefs.edit()
                    .putString("buyer_name", buyerName)
                    .putString("buyer_email", buyerEmail)
                    .apply()
                saveState()
                refreshHeaderAndNav()
                val savedOrder = repository.ordersForUser(account.id)
                    .firstOrNull { it.number == orderNumber }
                AlertDialog.Builder(this@MainActivity)
                    .setTitle(getString(R.string.order_created))
                    .setMessage(
                        "Заказ: " + orderNumber +
                            "\nДата: " + (savedOrder?.createdAt ?: "сейчас") +
                            "\nСумма: " + money(total) +
                            "\nСпособ: " + paymentMethodName(paymentMethod) +
                            "\nСтатус: " + (savedOrder?.status ?: "CREATED_DEMO") +
                            "\n\n" + getString(R.string.order_no_charge) +
                            " Курсы добавлены в раздел «Мои курсы»."
                    )
                    .setCancelable(false)
                    .setPositiveButton("Начать обучение") { _, _ ->
                        items.firstOrNull()?.let { showCourseLearning(it) } ?: showProfile()
                    }
                    .setNegativeButton("Мои курсы") { _, _ -> showProfile() }
                    .show()
            } catch (error: Exception) {
                Toast.makeText(
                    this@MainActivity,
                    "Не удалось создать заказ: " + (error.message ?: ""),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

        private fun showProfile() {
        currentTab = "Профиль"
        render("Твой профиль", "Личный кабинет CourseMarket.")
        val buyerName = prefs.getString("buyer_name", currentUser?.username.orEmpty()).orEmpty().trim()
        val greeting = if (buyerName.isBlank()) "Добро пожаловать!" else "Добро пожаловать, " + buyerName + "!"
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = gradient("#302747", "#42365E", 19)
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }
        panel.addView(badge(
            if (currentUser?.isAdmin == true) "АДМИНИСТРАТОР COURSEMARKET" else "УЧЕНИК COURSEMARKET",
            accentDark, accent
        ))
        panel.addView(vSpace(12))
        panel.addView(text(greeting, 21, textPrimary, true))
        panel.addView(vSpace(5))
        panel.addView(text(
            "Учись в своём темпе и возвращайся к урокам в любой момент.",
            12, Color.rgb(222, 216, 238)
        ))
        contentBox.addView(panel)

        if (currentUser?.isAdmin == true) {
            contentBox.addView(vSpace(10))
            contentBox.addView(button(
                getString(R.string.admin_panel) + "  →",
                true
            ) { navigate("Админ") }, matchWrap())
        }

        sectionTitle("Моя статистика", "Твои действия и учебный прогресс")
        val totalCompleted = completedLessons.values.sumOf { it.size }
        contentBox.addView(text("Завершено уроков: " + totalCompleted, 11, textSecondary))
        contentBox.addView(vSpace(8))
        val stats = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val values = listOf(
            "Куплено" to purchased.size.toString(),
            "Избранное" to favorites.size.toString(),
            "В корзине" to cart.size.toString()
        )
        values.forEachIndexed { index, (label, value) ->
            val cell = statCard(label, value)
            val lp = LinearLayout.LayoutParams(0, dp(86), 1f)
            if (index > 0) lp.marginStart = dp(7)
            stats.addView(cell, lp)
        }
        contentBox.addView(stats)

        sectionTitle("Мои курсы", "Нажми на программу, чтобы продолжить обучение")
        val owned = courses.filter { purchased.contains(it.id) }
        if (owned.isEmpty()) {
            contentBox.addView(emptyState(
                "Здесь появятся твои курсы",
                "Выбери программу в каталоге и оформи демонстрационный заказ."
            ))
            contentBox.addView(vSpace(10))
            contentBox.addView(button("Выбрать курс", true) { navigate("Каталог") })
        } else {
            owned.forEach { course ->
                val completed = progressFor(course).size
                val percent = courseProgress(course)
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    background = rounded(surface, 15)
                    setPadding(dp(13), dp(12), dp(13), dp(12))
                    isClickable = true
                    isFocusable = true
                    setOnClickListener { showCourseLearning(course) }
                }
                val icon = TextView(this).apply {
                    text = course.icon
                    textSize = 19f
                    includeFontPadding = false
                    gravity = Gravity.CENTER
                    setTextColor(textPrimary)
                    background = rounded(Color.parseColor(course.tint), 11)
                }
                row.addView(icon, LinearLayout.LayoutParams(dp(44), dp(44)))
                val labels = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(dp(11), 0, dp(5), 0)
                }
                labels.addView(text(course.title, 13, textPrimary, true).apply { maxLines = 2 })
                labels.addView(vSpace(5))
                labels.addView(text(
                    completed.toString() + " из " + course.modules.size + " уроков  •  " + percent + "%",
                    10, if (percent == 100) success else textSecondary
                ))
                row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
                row.addView(text(
                    if (percent == 100) "✓" else "›",
                    21, if (percent == 100) success else accent, true
                ))
                contentBox.addView(row, matchWrap())
                contentBox.addView(vSpace(8))
            }
        }

        sectionTitle("История заказов", "Оформления, сохранённые в локальной базе SQLite")
        val orderContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        contentBox.addView(orderContainer, matchWrap())
        loadProfileOrders(orderContainer)

        sectionTitle("О приложении")
        val about = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 15)
            setPadding(dp(15), dp(14), dp(15), dp(14))
        }
        about.addView(text("CourseMarket", 15, textPrimary, true))
        about.addView(vSpace(5))
        about.addView(text(
            "12 учебных программ с уроками, примерами, практическими заданиями и проверочными вопросами. Учётные записи, каталог, заказы и учебный прогресс хранятся в SQLite. Оплата демонстрационная.",
            12, textSecondary
        ))
        contentBox.addView(about)
        contentBox.addView(vSpace(12))
        contentBox.addView(button(getString(R.string.auth_logout), false) { logout() }, matchWrap())
    }

    private fun loadProfileOrders(container: LinearLayout) {
        val userId = currentUser?.id ?: return
        uiScope.launch {
            try {
                val orders = repository.ordersForUser(userId)
                container.removeAllViews()
                if (orders.isEmpty()) {
                    container.addView(text("Заказов пока нет.", 11, textSecondary))
                    return@launch
                }
                orders.forEach { order ->
                    val card = LinearLayout(this@MainActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        background = rounded(surface, 14)
                        setPadding(dp(14), dp(13), dp(14), dp(13))
                    }
                    val heading = LinearLayout(this@MainActivity).apply {
                        orientation = LinearLayout.HORIZONTAL
                        gravity = Gravity.CENTER_VERTICAL
                    }
                    heading.addView(text(order.number, 13, accent, true),
                        LinearLayout.LayoutParams(0, -2, 1f))
                    heading.addView(text(money(order.total), 14, textPrimary, true))
                    card.addView(heading)
                    card.addView(vSpace(5))
                    card.addView(text(
                        order.createdAt + "  •  " + order.itemCount + " курсов",
                        10, textSecondary
                    ))
                    card.addView(vSpace(4))
                    card.addView(text(order.paymentMethod, 10, textSecondary))
                    card.addView(vSpace(5))
                    card.addView(text(
                        "Статус: " + orderStatusLabel(order.status),
                        10, if (order.status == "CANCELLED" || order.status == "REJECTED") gold else success
                    ))
                    card.addView(vSpace(5))
                    card.addView(text(getString(R.string.order_no_charge), 10, gold))
                    container.addView(card, matchWrap())
                    container.addView(vSpace(8))
                }
            } catch (error: Exception) {
                container.addView(text("Не удалось загрузить историю заказов.", 11, gold))
            }
        }
    }

    private fun orderStatusLabel(status: String): String = when (status) {
        "CONFIRMED_DEMO" -> "Подтверждён (демо)"
        "CANCELLED" -> "Отменён"
        "REJECTED" -> "Отклонён"
        else -> "Создан (демо)"
    }

        private fun showAdmin() {
        if (currentUser?.isAdmin != true) {
            Toast.makeText(this, getString(R.string.admin_only), Toast.LENGTH_SHORT).show()
            showProfile()
            return
        }
        currentTab = "Админ"
        render(getString(R.string.admin_panel), "Управление каталогом, пользователями и заказами.")
        contentBox.addView(button("← В профиль", false) { showProfile() }, matchWrap())
        contentBox.addView(vSpace(12))

        val overview = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = rounded(surfaceAlt, 15)
            setPadding(dp(14), dp(13), dp(14), dp(13))
        }
        overview.addView(statCard("Курсов", courses.size.toString()), LinearLayout.LayoutParams(0, dp(78), 1f))
        overview.addView(hSpace(7))
        overview.addView(statCard("Заказов", "…"), LinearLayout.LayoutParams(0, dp(78), 1f))
        overview.addView(hSpace(7))
        overview.addView(statCard("Пользователей", "…"), LinearLayout.LayoutParams(0, dp(78), 1f))
        contentBox.addView(overview, matchWrap())
        uiScope.launch {
            try {
                val orders = repository.allOrders()
                val users = repository.userCount()
                (overview.getChildAt(2) as? LinearLayout)?.let {
                    it.removeAllViews()
                    it.addView(statCard("Заказов", orders.size.toString()))
                }
                (overview.getChildAt(4) as? LinearLayout)?.let {
                    it.removeAllViews()
                    it.addView(statCard("Пользователей", users.toString()))
                }
            } catch (_: Exception) { }
        }

        sectionTitle(getString(R.string.admin_courses), "Создание, редактирование и удаление записей SQLite")
        contentBox.addView(button("＋  " + getString(R.string.admin_add_course), true) {
            showCreateCourseDialog()
        }, matchWrap())
        contentBox.addView(vSpace(10))

        courses.forEach { course ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = rounded(surface, 14)
                setPadding(dp(13), dp(12), dp(13), dp(12))
            }
            card.addView(text(course.title, 14, textPrimary, true))
            card.addView(vSpace(4))
            card.addView(text(
                course.category + "  •  " + course.author + "  •  " + money(course.price),
                10, textSecondary
            ))
            card.addView(vSpace(9))
            val actions = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            actions.addView(button("Изменить", false, compact = true) {
                showEditCourseDialog(course)
            }, LinearLayout.LayoutParams(0, dp(40), 1f))
            actions.addView(hSpace(7))
            actions.addView(button("Удалить", false, compact = true) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Удалить курс?")
                    .setMessage(
                        "Курс «" + course.title + "» будет удалён из каталога. " +
                            "Если на него уже оформлялись заказы, удаление заблокирует ограничение внешнего ключа."
                    )
                    .setNegativeButton("Отмена", null)
                    .setPositiveButton("Удалить") { _, _ ->
                        uiScope.launch {
                            val deleted = repository.deleteCourse(course.id)
                            if (deleted) {
                                favorites.remove(course.id)
                                cart.remove(course.id)
                                purchased.remove(course.id)
                                completedLessons.remove(course.id)
                                courses = repository.getCourses()
                                saveState()
                                showAdmin()
                            } else {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Нельзя удалить курс: он присутствует в истории заказов.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                    .show()
            }, LinearLayout.LayoutParams(0, dp(40), 1f))
            card.addView(actions)
            contentBox.addView(card, matchWrap())
            contentBox.addView(vSpace(8))
        }

        sectionTitle(getString(R.string.admin_orders), "Изменяй статусы для демонстрации административного сценария")
        val ordersContainer = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        contentBox.addView(ordersContainer, matchWrap())
        loadAdminOrders(ordersContainer)
        contentBox.addView(vSpace(16))
        contentBox.addView(text(
            "Администрирование работает локально на этом устройстве. Роли и данные хранятся в SQLite.",
            10, textSecondary
        ))
    }

    private fun showCreateCourseDialog() {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(8), dp(18), dp(4))
        }
        val title = checkoutInput(getString(R.string.course_title_hint), InputType.TYPE_CLASS_TEXT, "")
        val author = checkoutInput(getString(R.string.course_author_hint), InputType.TYPE_CLASS_TEXT, "")
        val description = EditText(this).apply {
            hint = getString(R.string.course_description_hint)
            setHintTextColor(textSecondary)
            setTextColor(textPrimary)
            textSize = 13f
            minLines = 3
            maxLines = 5
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(dp(13), dp(10), dp(13), dp(10))
            background = rounded(surface, 12)
        }
        val price = checkoutInput(getString(R.string.course_price_hint), InputType.TYPE_CLASS_NUMBER, "1990")
        val categorySpinner = Spinner(this).apply {
            adapter = ArrayAdapter(
                this@MainActivity,
                android.R.layout.simple_spinner_dropdown_item,
                listOf("Разработка", "Дизайн", "Геймдизайн")
            )
            background = rounded(surface, 12)
        }
        listOf<View>(title, author, description, price, categorySpinner).forEachIndexed { index, view ->
            if (index > 0) form.addView(vSpace(8))
            form.addView(view, matchWrap())
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.admin_add_course))
            .setView(form)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Создать", null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val name = title.text.toString().trim()
                        val teacher = author.text.toString().trim()
                        val summary = description.text.toString().trim()
                        val cost = price.text.toString().toIntOrNull()
                        if (name.isBlank() || teacher.isBlank() || summary.isBlank() || cost == null || cost < 0) {
                            Toast.makeText(this, "Заполни все поля и укажи корректную цену.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        val modules = listOf(
                            "Введение и основные понятия",
                            "Базовые инструменты и рабочая среда",
                            "Практика на небольшом примере",
                            "Самостоятельная работа и проверка",
                            "Итоговый проект и следующие шаги"
                        )
                        val newCourse = Course(
                            id = 0,
                            title = name,
                            category = categorySpinner.selectedItem.toString(),
                            author = teacher,
                            description = summary,
                            price = cost,
                            rating = "5.0",
                            duration = "5 часов",
                            level = "Начальный",
                            icon = "＋",
                            tint = "#514B83",
                            lessons = modules.size,
                            modules = modules,
                            outcomes = listOf("Понимать основные принципы темы", "Применять знания на практике")
                        )
                        uiScope.launch {
                            try {
                                repository.addCourse(newCourse)
                                courses = repository.getCourses()
                                dialog.dismiss()
                                showAdmin()
                            } catch (error: Exception) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Не удалось добавить курс: " + (error.message ?: ""),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
                dialog.show()
            }
    }

    private fun showEditCourseDialog(course: Course) {
        val form = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(8), dp(18), dp(4))
        }
        val title = checkoutInput(getString(R.string.course_title_hint), InputType.TYPE_CLASS_TEXT, course.title)
        val author = checkoutInput(getString(R.string.course_author_hint), InputType.TYPE_CLASS_TEXT, course.author)
        val description = EditText(this).apply {
            hint = getString(R.string.course_description_hint)
            setHintTextColor(textSecondary)
            setTextColor(textPrimary)
            textSize = 13f
            minLines = 3
            maxLines = 5
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
            setPadding(dp(13), dp(10), dp(13), dp(10))
            background = rounded(surface, 12)
            setText(course.description)
        }
        val price = checkoutInput(
            getString(R.string.course_price_hint),
            InputType.TYPE_CLASS_NUMBER,
            course.price.toString()
        )
        listOf<View>(title, author, description, price).forEachIndexed { index, view ->
            if (index > 0) form.addView(vSpace(8))
            form.addView(view, matchWrap())
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.admin_edit_course))
            .setView(form)
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Сохранить", null)
            .create()
            .also { dialog ->
                dialog.setOnShowListener {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                        val priceValue = price.text.toString().toIntOrNull()
                        if (title.text.isNullOrBlank() || author.text.isNullOrBlank() ||
                            description.text.isNullOrBlank() || priceValue == null || priceValue < 0
                        ) {
                            Toast.makeText(this, "Проверь поля формы.", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }
                        uiScope.launch {
                            try {
                                repository.updateCourse(
                                    course.id,
                                    title.text.toString(),
                                    author.text.toString(),
                                    description.text.toString(),
                                    priceValue
                                )
                                courses = repository.getCourses()
                                dialog.dismiss()
                                showAdmin()
                            } catch (error: Exception) {
                                Toast.makeText(
                                    this@MainActivity,
                                    "Не удалось обновить курс: " + (error.message ?: ""),
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
                dialog.show()
            }
    }

    private fun loadAdminOrders(container: LinearLayout) {
        uiScope.launch {
            try {
                val orders = repository.allOrders()
                container.removeAllViews()
                if (orders.isEmpty()) {
                    container.addView(text("Заказов пока нет.", 11, textSecondary))
                    return@launch
                }
                orders.forEach { order ->
                    val row = LinearLayout(this@MainActivity).apply {
                        orientation = LinearLayout.VERTICAL
                        background = rounded(surface, 14)
                        setPadding(dp(13), dp(12), dp(13), dp(12))
                        isClickable = true
                        isFocusable = true
                        setOnClickListener { showOrderStatusDialog(order) }
                    }
                    row.addView(text(order.number + "  •  " + money(order.total), 13, textPrimary, true))
                    row.addView(vSpace(4))
                    row.addView(text(
                        order.customerName + " / @" + order.username + " / " + order.email,
                        10, textSecondary
                    ))
                    row.addView(vSpace(4))
                    row.addView(text(
                        orderStatusLabel(order.status) + "  •  " + order.paymentMethod,
                        10, if (order.status == "CANCELLED" || order.status == "REJECTED") gold else success
                    ))
                    container.addView(row, matchWrap())
                    container.addView(vSpace(7))
                }
            } catch (_: Exception) {
                container.addView(text("Не удалось загрузить заказы.", 11, gold))
            }
        }
    }

    private fun showOrderStatusDialog(order: AdminOrder) {
        val options = arrayOf("Подтвердить (демо)", "Отменить", "Отклонить")
        val statuses = arrayOf("CONFIRMED_DEMO", "CANCELLED", "REJECTED")
        AlertDialog.Builder(this)
            .setTitle("Статус заказа " + order.number)
            .setSingleChoiceItems(options, -1) { dialog, which ->
                uiScope.launch {
                    try {
                        repository.updateOrderStatus(order.id, statuses[which])
                        dialog.dismiss()
                        showAdmin()
                    } catch (error: Exception) {
                        Toast.makeText(this@MainActivity, error.message ?: "Ошибка", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Закрыть", null)
            .show()
    }

    private fun addCourseCard(course: Course, target: LinearLayout = contentBox) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 18)
            setPadding(dp(13), dp(13), dp(13), dp(13))
        }

        val artwork = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            background = gradient(course.tint, "#282538", 14)
            setPadding(dp(14), dp(12), dp(14), dp(12))
            minimumHeight = dp(92)
        }
        val artLabels = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }
        artLabels.addView(badge(course.category.uppercase(), Color.argb(45, 255, 255, 255), textPrimary))
        artLabels.addView(vSpace(7))
        artLabels.addView(text("ПРАКТИЧЕСКИЙ КУРС", 9, Color.rgb(235, 231, 243), true))
        artwork.addView(artLabels, LinearLayout.LayoutParams(0, -2, 1f))
        artwork.addView(text(course.icon, 36, textPrimary, true).apply {
            gravity = Gravity.CENTER
            minWidth = dp(58)
        })
        card.addView(artwork, LinearLayout.LayoutParams(-1, dp(94)))
        card.addView(vSpace(12))

        val title = text(course.title, 17, textPrimary, true).apply {
            maxLines = 2
        }
        card.addView(title)
        card.addView(vSpace(5))
        card.addView(text("Автор: " + course.author, 11, textSecondary))
        card.addView(vSpace(8))
        card.addView(text(course.description, 12, textSecondary).apply {
            maxLines = 3
            setLineSpacing(dp(1).toFloat(), 1.0f)
        })
        card.addView(vSpace(10))

        val meta = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        meta.addView(text("★ " + course.rating, 11, gold, true))
        meta.addView(hSpace(13))
        meta.addView(text("◷ " + course.duration, 11, textSecondary))
        meta.addView(hSpace(13))
        meta.addView(text(course.lessons.toString() + " уроков", 11, textSecondary))
        card.addView(meta)
        card.addView(vSpace(12))

        val priceRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        priceRow.addView(text(money(course.price), 21, textPrimary, true), LinearLayout.LayoutParams(0, dp(42), 1f))
        priceRow.addView(button(if (favorites.contains(course.id)) "♥" else "♡", false, compact = true) {
            toggleFavorite(course)
        }, LinearLayout.LayoutParams(dp(42), dp(42)))
        priceRow.addView(hSpace(7))
        val cartLabel = when {
            purchased.contains(course.id) -> "Учиться"
            cart.contains(course.id) -> "В корзине"
            else -> "В корзину"
        }
        priceRow.addView(button(cartLabel, false, compact = true) {
            when {
                purchased.contains(course.id) -> showCourseLearning(course)
                cart.contains(course.id) -> navigate("Корзина")
                else -> addToCart(course)
            }
        }, LinearLayout.LayoutParams(-2, dp(42)))
        card.addView(priceRow)
        card.addView(vSpace(8))
        card.addView(button("Программа курса  →", true) { showDetails(course) }, LinearLayout.LayoutParams(-1, dp(43)))

        target.addView(card, matchWrap())
        target.addView(vSpace(11))
    }

    private fun toggleFavorite(course: Course) {
        if (!favorites.add(course.id)) favorites.remove(course.id)
        saveState()
        when (currentTab) {
            "Избранное" -> showFavorites()
            "Каталог" -> showCatalog()
            "Корзина" -> showCart()
            "Профиль" -> showProfile()
            else -> showHome()
        }
    }

    private fun addToCart(course: Course) {
        if (purchased.contains(course.id)) {
            Toast.makeText(this, "Этот курс уже находится в твоих покупках.", Toast.LENGTH_SHORT).show()
            return
        }
        if (cart.add(course.id)) {
            saveState()
            refreshHeaderAndNav()
            Toast.makeText(this, "Курс добавлен в корзину.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Этот курс уже в корзине.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDetails(course: Course) {
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 20)
            setPadding(dp(19), dp(18), dp(19), dp(18))
        }
        body.addView(badge(course.category.uppercase() + "  •  " + course.level.uppercase(), accentDark, accent))
        body.addView(vSpace(13))
        body.addView(text(course.title, 22, textPrimary, true))
        body.addView(vSpace(8))
        body.addView(text("Автор: " + course.author, 12, textSecondary))
        body.addView(vSpace(11))
        body.addView(text(course.description, 13, textSecondary))
        body.addView(vSpace(15))

        val facts = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = rounded(surfaceAlt, 13)
            setPadding(dp(10), dp(12), dp(10), dp(12))
        }
        facts.addView(factCell("РЕЙТИНГ", "★ " + course.rating), weighted(1f))
        facts.addView(factCell("ДЛИТЕЛЬНОСТЬ", course.duration), weighted(1f))
        facts.addView(factCell("УРОКИ", course.lessons.toString()), weighted(1f))
        body.addView(facts)

        sectionIn(body, "Программа обучения")
        course.modules.forEachIndexed { index, module ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.TOP
                setPadding(0, dp(5), 0, dp(5))
            }
            row.addView(text((index + 1).toString().padStart(2, '0'), 12, accent, true), LinearLayout.LayoutParams(dp(32), -2))
            row.addView(text(module, 12, textPrimary), LinearLayout.LayoutParams(0, -2, 1f))
            body.addView(row)
        }

        sectionIn(body, "После курса ты сможешь")
        course.outcomes.forEach { outcome ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.TOP
                setPadding(0, dp(4), 0, dp(4))
            }
            row.addView(text("✓", 14, success, true), LinearLayout.LayoutParams(dp(25), -2))
            row.addView(text(outcome, 12, textPrimary), LinearLayout.LayoutParams(0, -2, 1f))
            body.addView(row)
        }

        body.addView(vSpace(14))
        body.addView(text("Стоимость курса", 11, textSecondary))
        body.addView(vSpace(3))
        body.addView(text(money(course.price), 26, textPrimary, true))
        body.addView(vSpace(12))

        lateinit var dialog: AlertDialog
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        actions.addView(button(
            if (favorites.contains(course.id)) "♥ Сохранено" else "♡ В избранное",
            false,
            compact = true
        ) {
            toggleFavorite(course)
            dialog.dismiss()
        }, LinearLayout.LayoutParams(0, dp(46), 1f))
        actions.addView(hSpace(8))
        actions.addView(button(
            when {
                purchased.contains(course.id) -> "Учиться"
                cart.contains(course.id) -> "В корзине"
                else -> "В корзину"
            },
            true,
            compact = true
        ) {
            when {
                purchased.contains(course.id) -> showCourseLearning(course)
                cart.contains(course.id) -> navigate("Корзина")
                else -> addToCart(course)
            }
            dialog.dismiss()
        }, LinearLayout.LayoutParams(0, dp(46), 1f))
        body.addView(actions)

        val scroll = ScrollView(this).apply {
            isFillViewport = false
            overScrollMode = View.OVER_SCROLL_NEVER
            addView(body)
        }
        dialog = AlertDialog.Builder(this).setView(scroll).create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels - dp(30)).coerceAtMost(dp(480)),
            (resources.displayMetrics.heightPixels * 0.86f).toInt()
        )
    }

    private fun progressFor(course: Course): MutableSet<Int> =
        completedLessons.getOrPut(course.id) { mutableSetOf() }

    private fun saveLessonProgress(course: Course) {
        prefs.edit()
            .putString("progress_" + course.id, progressFor(course).sorted().joinToString(","))
            .apply()
        val userId = currentUser?.id ?: return
        val completed = progressFor(course).toSet()
        uiScope.launch {
            try {
                completed.forEach { index ->
                    repository.completeLesson(userId, course.id, index)
                }
            } catch (error: Exception) {
                Toast.makeText(this@MainActivity, "Не удалось сохранить прогресс.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun courseProgress(course: Course): Int {
        val count = course.modules.size
        if (count == 0) return 0
        return (progressFor(course).size * 100 / count).coerceIn(0, 100)
    }

    private fun showCourseLearning(course: Course) {
        currentLearningCourseId = course.id
        currentLearningLessonIndex = -1
        currentTab = "Профиль"
        val completed = progressFor(course)
        val totalLessons = course.modules.size
        val progress = courseProgress(course)
        render("Моё обучение", course.title)
        contentBox.addView(button("← Назад в профиль", false) { showProfile() }, matchWrap())
        contentBox.addView(vSpace(13))

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = gradient(course.tint, "#29263A", 18)
            setPadding(dp(17), dp(17), dp(17), dp(17))
        }
        hero.addView(badge(course.category.uppercase(), Color.argb(45, 255, 255, 255), textPrimary))
        hero.addView(vSpace(11))
        hero.addView(text(course.title, 20, textPrimary, true))
        hero.addView(vSpace(5))
        hero.addView(text("Преподаватель: " + course.author, 11, Color.rgb(226, 222, 236)))
        hero.addView(vSpace(13))
        hero.addView(text("ПРОГРЕСС  " + completed.size + " ИЗ " + totalLessons + " УРОКОВ  •  " + progress + "%", 10, textPrimary, true))
        hero.addView(vSpace(8))
        val bar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal).apply {
            max = 100
            this.progress = progress
            progressTintList = ColorStateList.valueOf(accent)
            progressBackgroundTintList = ColorStateList.valueOf(Color.argb(90, 255, 255, 255))
        }
        hero.addView(bar, LinearLayout.LayoutParams(-1, dp(7)))
        hero.addView(vSpace(13))
        hero.addView(button(
            if (completed.size == totalLessons) "Повторить уроки" else "Продолжить обучение  →",
            true
        ) {
            val next = course.modules.indices.firstOrNull { it !in completed } ?: 0
            showLesson(course, next)
        })
        contentBox.addView(hero, matchWrap())

        sectionTitle("Программа обучения", "Открывай уроки по порядку или возвращайся к пройденным")
        course.modules.forEachIndexed { index, module ->
            val done = index in completed
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                background = rounded(surface, 14)
                setPadding(dp(13), dp(12), dp(13), dp(12))
                isClickable = true
                isFocusable = true
                setOnClickListener { showLesson(course, index) }
            }
            row.addView(text(
                if (done) "✓" else (index + 1).toString().padStart(2, '0'),
                15, if (done) success else accent, true
            ), LinearLayout.LayoutParams(dp(35), -2))
            val labels = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            labels.addView(text(module, 13, textPrimary, true))
            labels.addView(vSpace(4))
            labels.addView(text(
                if (done) "Урок завершён" else "Урок " + (index + 1) + "  •  материал и тест",
                10, if (done) success else textSecondary
            ))
            row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
            row.addView(text("›", 22, textSecondary, true))
            contentBox.addView(row, matchWrap())
            contentBox.addView(vSpace(7))
        }

        if (completed.size == totalLessons && totalLessons > 0) {
            contentBox.addView(vSpace(8))
            contentBox.addView(emptyState(
                "Курс завершён!",
                "Ты прошёл все уроки и проверочные вопросы. Можешь повторить материал в любое время."
            ))
        }
    }

    private fun showLesson(course: Course, lessonIndex: Int) {
        if (lessonIndex !in course.modules.indices) {
            showCourseLearning(course)
            return
        }
        currentTab = "Профиль"
        currentLearningCourseId = course.id
        currentLearningLessonIndex = lessonIndex
        val lesson = CourseLearningContent.get(course.id, lessonIndex, course.modules[lessonIndex])
        val completed = progressFor(course)
        val wasCompleted = lessonIndex in completed
        render(lesson.title, course.title + "  •  Урок " + (lessonIndex + 1) + " из " + course.modules.size)

        contentBox.addView(button("← К программе курса", false) { showCourseLearning(course) }, matchWrap())
        contentBox.addView(vSpace(13))

        val intro = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 16)
            setPadding(dp(16), dp(15), dp(16), dp(15))
        }
        intro.addView(badge("ТЕОРИЯ", accentDark, accent))
        intro.addView(vSpace(10))
        intro.addView(text(lesson.introduction, 13, textPrimary).apply {
            setLineSpacing(dp(3).toFloat(), 1.03f)
        })
        contentBox.addView(intro, matchWrap())

        sectionTitle("Ключевые идеи")
        lesson.keyPoints.forEachIndexed { index, point ->
            val line = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.TOP
                background = rounded(surface, 12)
                setPadding(dp(12), dp(11), dp(12), dp(11))
            }
            line.addView(text((index + 1).toString(), 13, accent, true), LinearLayout.LayoutParams(dp(25), -2))
            line.addView(text(point, 12, textPrimary), LinearLayout.LayoutParams(0, -2, 1f))
            contentBox.addView(line, matchWrap())
            contentBox.addView(vSpace(7))
        }

        sectionTitle("Разбор примера")
        val example = TextView(this).apply {
            text = lesson.example
            textSize = 12f
            typeface = Typeface.MONOSPACE
            setTextColor(Color.rgb(220, 213, 244))
            includeFontPadding = false
            setPadding(dp(14), dp(14), dp(14), dp(14))
            background = rounded(Color.rgb(24, 23, 33), 13)
            setLineSpacing(dp(3).toFloat(), 1.0f)
        }
        contentBox.addView(example, matchWrap())

        sectionTitle("Практическое задание")
        val practice = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 14)
            setPadding(dp(14), dp(13), dp(14), dp(13))
        }
        practice.addView(badge("ПОПРОБУЙ САМ", accentDark, accent))
        practice.addView(vSpace(9))
        practice.addView(text(lesson.practice, 12, textPrimary))
        contentBox.addView(practice, matchWrap())

        sectionTitle("Проверь себя", "Выбери один правильный ответ")
        val quiz = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 15)
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }
        quiz.addView(text(lesson.question, 14, textPrimary, true))
        quiz.addView(vSpace(10))
        var selectedAnswer = -1
        val optionViews = mutableListOf<TextView>()
        lesson.answers.forEachIndexed { index, answer ->
            val option = text(
                (index + 1).toString() + ".  " + answer,
                12, textPrimary
            ).apply {
                background = rounded(surfaceAlt, 11)
                setPadding(dp(12), dp(11), dp(12), dp(11))
                isClickable = true
                isFocusable = true
            }
            option.setOnClickListener {
                selectedAnswer = index
                optionViews.forEachIndexed { optionIndex, view ->
                    view.background = rounded(if (optionIndex == selectedAnswer) accentDark else surfaceAlt, 11)
                    view.setTextColor(if (optionIndex == selectedAnswer) accent else textPrimary)
                }
            }
            optionViews.add(option)
            quiz.addView(option, matchWrap())
            quiz.addView(vSpace(6))
        }

        val result = text(
            if (wasCompleted) "Этот урок уже отмечен как завершённый. Можешь пройти проверку ещё раз." else "",
            11, success
        )
        result.setPadding(0, dp(5), 0, dp(8))
        quiz.addView(result)

        lateinit var finishButton: TextView
        val checkButton = button("Проверить ответ", true) {
            when {
                selectedAnswer < 0 -> result.text = "Сначала выбери один из вариантов ответа."
                selectedAnswer == lesson.correctAnswer -> {
                    result.text = "Верно! " + lesson.explanation
                    result.setTextColor(success)
                    finishButton.visibility = View.VISIBLE
                }
                else -> {
                    result.text = "Пока неверно. Перечитай ключевые идеи и попробуй ещё раз."
                    result.setTextColor(Color.rgb(255, 150, 145))
                }
            }
        }
        quiz.addView(checkButton, LinearLayout.LayoutParams(-1, dp(44)))

        finishButton = button(
            if (wasCompleted) "Урок уже пройден  •  Далее" else "Отметить урок завершён  →",
            true
        ) {
            completed.add(lessonIndex)
            saveLessonProgress(course)
            if (lessonIndex + 1 < course.modules.size) {
                showLesson(course, lessonIndex + 1)
            } else {
                showCourseLearning(course)
            }
        }.apply { visibility = if (wasCompleted) View.VISIBLE else View.GONE }
        quiz.addView(vSpace(8))
        quiz.addView(finishButton, LinearLayout.LayoutParams(-1, dp(46)))
        contentBox.addView(quiz, matchWrap())

        contentBox.addView(vSpace(18))
        contentBox.addView(text(
            "Твой прогресс сохраняется на этом устройстве. Можно вернуться к любому уроку позже.",
            10, textSecondary
        ))
    }

    private fun sectionIn(parent: LinearLayout, title: String) {
        parent.addView(vSpace(16))
        parent.addView(text(title, 15, textPrimary, true))
        parent.addView(vSpace(7))
    }

    private fun factCell(label: String, value: String): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
        }
        box.addView(text(label, 8, textSecondary, true))
        box.addView(vSpace(5))
        box.addView(text(value, 12, textPrimary, true))
        return box
    }

    private fun quickInfo(value: String, label: String): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(4), dp(14), dp(4), dp(7))
        }
        box.addView(text(value, 18, accent, true))
        box.addView(vSpace(3))
        box.addView(text(label, 9, textSecondary))
        return box
    }

    private fun sectionTitle(value: String, caption: String? = null) {
        val block = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(0, dp(19), 0, dp(10))
        }
        block.addView(text(value, 18, textPrimary, true))
        if (caption != null) {
            block.addView(vSpace(3))
            block.addView(text(caption, 10, textSecondary))
        }
        contentBox.addView(block)
    }

    private fun emptyState(title: String, description: String): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = rounded(surface, 18)
            setPadding(dp(20), dp(27), dp(20), dp(27))
        }
        box.addView(text("✦", 28, accent, true))
        box.addView(vSpace(9))
        box.addView(text(title, 17, textPrimary, true).apply { gravity = Gravity.CENTER })
        box.addView(vSpace(6))
        box.addView(text(description, 12, textSecondary).apply {
            gravity = Gravity.CENTER
            setLineSpacing(dp(2).toFloat(), 1.0f)
        })
        return box
    }

    private fun statCard(label: String, value: String): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = rounded(surface, 14)
            setPadding(dp(5), dp(10), dp(5), dp(10))
        }
        box.addView(text(value, 23, accent, true).apply { gravity = Gravity.CENTER })
        box.addView(vSpace(4))
        box.addView(text(label, 10, textSecondary, true).apply { gravity = Gravity.CENTER })
        return box
    }

    private fun badge(label: String, backgroundColor: Int, foreground: Int): TextView =
        text(label, 9, foreground, true).apply {
            gravity = Gravity.CENTER
            setPadding(dp(9), dp(6), dp(9), dp(6))
            background = rounded(backgroundColor, 30)
        }

    private fun button(
        label: String,
        primary: Boolean,
        compact: Boolean = false,
        action: () -> Unit
    ): TextView = text(label, if (compact) 11 else 13, if (primary) Color.rgb(29, 23, 43) else textPrimary, true).apply {
        gravity = Gravity.CENTER
        minHeight = dp(42)
        setPadding(dp(if (compact) 10 else 14), dp(1), dp(if (compact) 10 else 14), dp(1))
        background = rounded(if (primary) accent else surfaceAlt, 12)
        isClickable = true
        isFocusable = true
        setOnClickListener { action() }
    }

    private fun text(value: String, size: Int, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            includeFontPadding = false
            gravity = Gravity.CENTER_VERTICAL
            if (bold) setTypeface(null, Typeface.BOLD)
            setLineSpacing(dp(1).toFloat(), 1.0f)
        }

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }

    private fun gradient(start: String, end: String, radius: Int): GradientDrawable =
        GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(Color.parseColor(start), Color.parseColor(end))
        ).apply {
            cornerRadius = dp(radius).toFloat()
        }

    private fun vSpace(height: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }

    private fun hSpace(width: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(dp(width), 1)
    }

    private fun weighted(weight: Float) = LinearLayout.LayoutParams(0, -2, weight)

    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)

    private fun money(value: Int): String =
        String.format(Locale.US, "%,d ₽", value).replace(',', ' ')

    private fun pluralCourses(count: Int): String = when {
        count % 10 == 1 && count % 100 != 11 -> "курс"
        count % 10 in 2..4 && count % 100 !in 12..14 -> "курса"
        else -> "курсов"
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private companion object {
        const val NAV_HOME = 101
        const val NAV_CATALOG = 102
        const val NAV_FAVORITES = 103
        const val NAV_CART = 104
        const val NAV_PROFILE = 105
    }
}
