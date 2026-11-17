package com.example.coursemarket

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

private data class Course(
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

    private val bg = Color.rgb(18, 18, 25)
    private val surface = Color.rgb(29, 29, 40)
    private val surfaceAlt = Color.rgb(39, 37, 52)
    private val accent = Color.rgb(181, 156, 255)
    private val accentDark = Color.rgb(45, 37, 67)
    private val textPrimary = Color.rgb(247, 245, 252)
    private val textSecondary = Color.rgb(167, 164, 184)
    private val success = Color.rgb(132, 210, 170)
    private val gold = Color.rgb(244, 202, 129)

    private val courses = listOf(
        Course(
            1, "Kotlin: от основ до Android-приложения", "Разработка", "Алексей Морозов",
            "Последовательно изучи Kotlin и собери Android-приложение с интерфейсом, навигацией и локальным хранением данных. В конце курса у тебя будет законченный учебный проект.",
            2490, "4.9", "18 часов", "Начальный", "K", "#55427D", 24,
            listOf("Знакомство с Kotlin и настройка среды", "Переменные, типы, условия и циклы", "Функции, классы, коллекции и null safety", "Android Views, события и навигация", "Хранение данных и финальный проект"),
            listOf("Писать читаемый код на Kotlin", "Создавать экраны Android-приложения", "Сохранять данные локально и собирать APK")
        ),
        Course(
            2, "UI/UX дизайн: интерфейсы с нуля", "Дизайн", "Мария Волкова",
            "Научись превращать задачу пользователя в понятный интерфейс. Разберёшься с исследованиями, пользовательскими сценариями, сетками, типографикой и визуальной иерархией.",
            1990, "4.8", "12 часов", "Начальный", "✦", "#754B69", 18,
            listOf("Основы UX и анализ аудитории", "Пользовательские сценарии и wireframes", "Сетка, композиция, цвет и типографика", "UI-компоненты и дизайн-система", "Прототипирование и проверка решения"),
            listOf("Проектировать понятные пользовательские потоки", "Создавать цельные интерфейсы", "Обосновывать визуальные решения")
        ),
        Course(
            3, "Unity: разработка 2D-игр на C#", "Разработка", "Игорь Лебедев",
            "Создай основу собственной 2D-игры: контроллер персонажа, физику, боевую систему, анимации, врагов и игровые сцены. Практика построена вокруг небольшого игрового проекта.",
            3290, "5.0", "26 часов", "Начальный", "◈", "#365F70", 32,
            listOf("Структура проекта и основы Unity", "C#, компоненты и жизненный цикл", "Rigidbody2D, коллизии и контроллер игрока", "Анимации, атаки и система здоровья", "AI противников и организация уровней", "Сборка и полировка игрового прототипа"),
            listOf("Создавать управляемого 2D-персонажа", "Настраивать анимации и боевые столкновения", "Собирать игровой прототип от начала до конца")
        ),
        Course(
            4, "Python для практических задач", "Разработка", "Денис Ким",
            "Освой Python на примерах реальных задач: обработка файлов, автоматизация повторяющихся действий, работа с данными и внешними API.",
            1790, "4.7", "15 часов", "Начальный", "Py", "#42664F", 20,
            listOf("Синтаксис, переменные и базовые типы", "Условия, циклы и функции", "Списки, словари и работа с файлами", "Исключения и структура проекта", "HTTP-запросы и небольшой автоматизатор"),
            listOf("Писать скрипты для повседневных задач", "Обрабатывать файлы и коллекции данных", "Получать данные из простых API")
        ),
        Course(
            5, "Blender: 3D-моделирование", "Дизайн", "Елена Орлова",
            "Пройди путь от простых форм до законченной стилизованной сцены. Изучишь моделирование, материалы, свет, композицию и финальный рендер.",
            2890, "4.9", "21 час", "Средний", "⬡", "#755A3A", 22,
            listOf("Навигация и рабочее пространство Blender", "Базовое и hard-surface моделирование", "Модификаторы и чистая геометрия", "Материалы, текстуры и освещение", "Композиция, камера и финальный рендер"),
            listOf("Моделировать простые игровые ассеты", "Настраивать материалы и освещение", "Подготавливать сцену к финальному рендеру")
        ),
        Course(
            6, "Геймдизайн: от идеи до прототипа", "Геймдизайн", "Павел Соколов",
            "Разберись, почему игровые механики работают. Сформируй core loop, опиши правила, спроектируй уровни и проверь идею на простом прототипе.",
            2190, "4.8", "14 часов", "Любой", "⚔", "#70464F", 16,
            listOf("Роль геймдизайнера и анализ референсов", "Core loop, механики и игровые системы", "Баланс, прогрессия и обратная связь", "Дизайн уровней и кривая сложности", "Документация и проверка прототипа"),
            listOf("Формулировать игровую концепцию", "Описывать механики и игровой цикл", "Собирать понятный дизайн-документ")
        ),
        Course(
            7, "Figma: профессиональный workflow", "Дизайн", "Анна Белова",
            "Освой Figma для повседневной продуктовой работы: компоненты, варианты, Auto Layout, переменные, интерактивные прототипы и подготовка макетов к передаче.",
            1490, "4.8", "9 часов", "Начальный", "F", "#514B83", 14,
            listOf("Рабочее пространство и организация файлов", "Auto Layout и адаптивные компоненты", "Variants, variables и библиотеки", "Интерактивные прототипы", "Подготовка макетов к handoff"),
            listOf("Создавать переиспользуемые компоненты", "Быстро собирать адаптивные экраны", "Передавать макеты разработчикам")
        ),
        Course(
            8, "C# для разработчиков игр", "Разработка", "Максим Романов",
            "Изучи фундамент C# с акцентом на игровые проекты: типы, ООП, коллекции, события, интерфейсы и асинхронность. Примеры ориентированы на Unity.",
            2290, "4.9", "17 часов", "Начальный", "C#", "#3E5B79", 21,
            listOf("Синтаксис, типы и управление потоком", "Методы, классы и объектная модель", "Наследование, интерфейсы и композиция", "Коллекции, LINQ и события", "Практика: архитектура игровой системы"),
            listOf("Понимать базовые принципы ООП", "Разделять ответственность классов", "Использовать события и коллекции в проекте")
        ),
        Course(
            9, "Веб-разработка: HTML, CSS и JavaScript", "Разработка", "Никита Орлов",
            "Собери адаптивную веб-страницу и добавь ей интерактивность. Курс охватывает семантическую разметку, стили, основы JavaScript и публикацию проекта.",
            1890, "4.8", "20 часов", "Начальный", "</>", "#3D6470", 28,
            listOf("HTML и семантическая структура", "CSS, Flexbox и Grid", "Адаптивная верстка и доступность", "JavaScript и обработка событий", "Финальная страница и публикация"),
            listOf("Создавать адаптивные страницы", "Добавлять интерактивность на JavaScript", "Публиковать готовый сайт")
        ),
        Course(
            10, "Motion design: анимация интерфейсов", "Дизайн", "София Миронова",
            "Научись оживлять статичный дизайн: принципы движения, тайминг, easing, микровзаимодействия и экспорт анимаций для цифровых продуктов.",
            2390, "4.9", "16 часов", "Средний", "▶", "#76513F", 19,
            listOf("Принципы анимации и визуальный ритм", "Ключевые кадры и кривые движения", "Переходы и микровзаимодействия", "Анимация интерфейсных состояний", "Экспорт и подготовка материалов"),
            listOf("Понимать тайминг и easing", "Создавать короткие UI-анимации", "Подготавливать анимации для продукта")
        ),
        Course(
            11, "Pixel Art: графика для 2D-игр", "Геймдизайн", "Артём Власов",
            "Создавай выразительную пиксельную графику для игр. Поработаешь с палитрами, силуэтами, тайлами, персонажами и простыми циклами анимации.",
            1690, "4.9", "11 часов", "Начальный", "▦", "#536C59", 15,
            listOf("Пиксельная сетка и рабочий процесс", "Ограниченная палитра и светотень", "Силуэт и дизайн персонажа", "Тайлы и бесшовные окружения", "Idle, walk и простая атака"),
            listOf("Создавать читаемые игровые спрайты", "Работать с палитрой и силуэтом", "Рисовать простые циклы анимации")
        ),
        Course(
            12, "Product Design: проектирование продукта", "Дизайн", "Кирилл Андреев",
            "Посмотри на дизайн шире отдельных экранов: исследуй проблему, сформулируй гипотезу, спроектируй решение и оцени его по пользовательскому сценарию.",
            2690, "4.8", "19 часов", "Средний", "◎", "#62517B", 23,
            listOf("Проблема, аудитория и продуктовый контекст", "Интервью и карта пользовательского пути", "Гипотезы и приоритизация решений", "Прототип и проверка сценария", "Кейс и презентация результата"),
            listOf("Формулировать продуктовую проблему", "Проводить базовое исследование", "Собирать и презентовать дизайн-кейс")
        )
    )

    private lateinit var root: LinearLayout
    private lateinit var contentBox: LinearLayout
    private lateinit var navItems: Map<String, LinearLayout>
    private var currentTab = "Главная"
    private var selectedCategory = "Все"
    private var selectedSort = "Популярные"
    private var searchQuery = ""
    private val favorites = mutableSetOf<Int>()
    private val cart = mutableSetOf<Int>()
    private val purchased = mutableSetOf<Int>()
    private val prefs by lazy { getSharedPreferences("course_market", MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadState()
        buildShell()
        showHome()
    }

    private fun loadState() {
        favorites.addAll(readIds("favorites"))
        cart.addAll(readIds("cart"))
        purchased.addAll(readIds("purchased"))
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

        val tabs = listOf(
            "Главная" to "⌂",
            "Каталог" to "▦",
            "Избранное" to "♡",
            "Корзина" to "▣",
            "Профиль" to "●"
        )
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = rounded(surface, 18)
            setPadding(dp(4), dp(5), dp(4), dp(5))
        }
        val map = linkedMapOf<String, LinearLayout>()
        tabs.forEach { (tab, symbol) ->
            val item = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                background = rounded(Color.TRANSPARENT, 12)
                isClickable = true
                isFocusable = true
                setPadding(dp(2), dp(5), dp(2), dp(5))
                setOnClickListener { navigate(tab) }
            }
            item.addView(text(symbol, 19, textSecondary, true).apply { gravity = Gravity.CENTER })
            val caption = if (tab == "Корзина") "Корзина " + cart.size else tab
            item.addView(text(caption, 9, textSecondary, true).apply {
                gravity = Gravity.CENTER
                maxLines = 1
            })
            nav.addView(item, LinearLayout.LayoutParams(0, dp(52), 1f))
            map[tab] = item
        }
        navItems = map
        root.addView(nav, LinearLayout.LayoutParams(-1, dp(62)))
        setContentView(root)
        ViewCompat.requestApplyInsets(root)
        refreshHeaderAndNav()
    }

    private fun navigate(tab: String) {
        currentTab = tab
        when (tab) {
            "Главная" -> showHome()
            "Каталог" -> showCatalog()
            "Избранное" -> showFavorites()
            "Корзина" -> showCart()
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
        if (::navItems.isInitialized) {
            navItems.forEach { (tab, item) ->
                val active = currentTab == tab
                item.background = rounded(if (active) accentDark else Color.TRANSPARENT, 12)
                (item.getChildAt(0) as TextView).setTextColor(if (active) accent else textSecondary)
                (item.getChildAt(1) as TextView).apply {
                    text = if (tab == "Корзина") "Корзина " + cart.size else tab
                    setTextColor(if (active) textPrimary else textSecondary)
                }
            }
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
                results.addView(emptyState("Ничего не найдено", "Измени запрос или выбери другую категорию."))
            } else {
                filtered.forEach { addCourseCard(it, results) }
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
        AlertDialog.Builder(this)
            .setTitle("Подтверждение заказа")
            .setMessage(
                "Сумма заказа: " + money(items.sumOf { it.price }) +
                    ". Это демонстрационный сценарий без реального списания средств. Перенести курсы в раздел «Мои курсы»?"
            )
            .setNegativeButton("Назад", null)
            .setPositiveButton("Подтвердить") { _, _ ->
                items.forEach {
                    purchased.add(it.id)
                    cart.remove(it.id)
                }
                saveState()
                Toast.makeText(this, "Заказ оформлен. Курсы доступны в профиле.", Toast.LENGTH_LONG).show()
                showProfile()
            }
            .show()
    }

    private fun showProfile() {
        currentTab = "Профиль"
        render("Твой профиль", "Личный кабинет CourseMarket.")
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = gradient("#302747", "#42365E", 19)
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }
        panel.addView(badge("УЧЕНИК COURSEMARKET", accentDark, accent))
        panel.addView(vSpace(12))
        panel.addView(text("Добро пожаловать!", 22, textPrimary, true))
        panel.addView(vSpace(5))
        panel.addView(text("Твой прогресс начинается с нового знания.", 12, Color.rgb(222, 216, 238)))
        contentBox.addView(panel)
        sectionTitle("Моя статистика", "Твои действия в приложении")
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

        sectionTitle("Мои курсы", "Доступ к оформленным покупкам")
        val owned = courses.filter { purchased.contains(it.id) }
        if (owned.isEmpty()) {
            contentBox.addView(emptyState("Здесь появятся твои курсы", "Оформи демонстрационный заказ — покупки будут показаны в этом разделе."))
            contentBox.addView(vSpace(10))
            contentBox.addView(button("Выбрать курс", true) { navigate("Каталог") })
        } else {
            owned.forEach { course ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    background = rounded(surface, 15)
                    setPadding(dp(13), dp(12), dp(13), dp(12))
                    isClickable = true
                    isFocusable = true
                    setOnClickListener { showDetails(course) }
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
                    setPadding(dp(11), 0, 0, 0)
                }
                labels.addView(text(course.title, 13, textPrimary, true).apply { maxLines = 2 })
                labels.addView(vSpace(4))
                labels.addView(text("Доступ открыт  •  " + course.duration, 11, success))
                row.addView(labels, LinearLayout.LayoutParams(0, -2, 1f))
                contentBox.addView(row, matchWrap())
                contentBox.addView(vSpace(8))
            }
        }

        sectionTitle("О приложении")
        val about = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 15)
            setPadding(dp(15), dp(14), dp(15), dp(14))
        }
        about.addView(text("CourseMarket", 15, textPrimary, true))
        about.addView(vSpace(5))
        about.addView(text(
            "Демонстрационный магазин образовательных курсов. Каталог, поиск, избранное, корзина и список покупок работают локально и сохраняются на устройстве. Реальные платежи, сервер и регистрация пользователей не подключены.",
            12, textSecondary
        ))
        contentBox.addView(about)
        contentBox.addView(vSpace(12))
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
        card.addView(text(course.description, 12, Color.rgb(194, 191, 207)).apply {
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
            purchased.contains(course.id) -> "Куплен"
            cart.contains(course.id) -> "В корзине"
            else -> "В корзину"
        }
        priceRow.addView(button(cartLabel, false, compact = true) {
            when {
                purchased.contains(course.id) -> Toast.makeText(this, "Этот курс уже куплен.", Toast.LENGTH_SHORT).show()
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
        body.addView(text(course.description, 13, Color.rgb(210, 207, 221)))
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
                purchased.contains(course.id) -> "Уже куплен"
                cart.contains(course.id) -> "В корзине"
                else -> "В корзину"
            },
            true,
            compact = true
        ) {
            when {
                purchased.contains(course.id) -> Toast.makeText(this, "Этот курс уже куплен.", Toast.LENGTH_SHORT).show()
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
}
