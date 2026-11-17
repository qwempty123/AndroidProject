package com.example.coursemarket

import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

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
    val tint: String
)

class MainActivity : AppCompatActivity() {
    private val bg = Color.rgb(18, 18, 24)
    private val surface = Color.rgb(31, 31, 42)
    private val surface2 = Color.rgb(42, 40, 55)
    private val accent = Color.rgb(167, 139, 250)
    private val muted = Color.rgb(166, 163, 180)
    private val white = Color.WHITE

    private val courses = listOf(
        Course(1, "Kotlin с нуля до приложения", "Разработка", "Алексей Морозов", "Освой Kotlin, основы Android и создай своё первое полноценное приложение. Практические задания и проект в конце курса.", 2490, "4.9", "18 часов", "Начальный", "⌘", "#493B70"),
        Course(2, "UI/UX дизайн интерфейсов", "Дизайн", "Мария Волкова", "Научись проектировать удобные интерфейсы, работать с сетками, типографикой, цветом и создавать дизайн-системы.", 1990, "4.8", "12 часов", "Любой", "✦", "#70445E"),
        Course(3, "Unity: разработка 2D-игр", "Разработка", "Игорь Лебедев", "От первой сцены до игрового прототипа: физика, анимации, контроллер персонажа, противники и архитектура проекта.", 3290, "5.0", "26 часов", "Начальный", "◈", "#365B66"),
        Course(4, "Python и автоматизация", "Разработка", "Денис Ким", "Изучи Python, научись автоматизировать рутину, работать с файлами, API и создавать полезные скрипты.", 1790, "4.7", "15 часов", "Начальный", "⌁", "#47634D"),
        Course(5, "3D-моделирование в Blender", "Дизайн", "Елена Орлова", "Создавай стилизованные 3D-модели, настраивай материалы, освещение и рендерь готовые сцены.", 2890, "4.9", "21 час", "Средний", "⬡", "#725238"),
        Course(6, "Геймдизайн: от идеи до прототипа", "Геймдизайн", "Павел Соколов", "Разбери игровые механики, core loop, баланс, уровни и документацию. Собери концепт собственной игры.", 2190, "4.8", "14 часов", "Любой", "⚔", "#69434A"),
        Course(7, "Figma: профессиональный workflow", "Дизайн", "Анна Белова", "Компоненты, auto layout, variables, прототипирование и передача макетов разработчикам.", 1490, "4.8", "9 часов", "Начальный", "◫", "#554A7A"),
        Course(8, "C# для разработчиков игр", "Разработка", "Максим Романов", "Изучи основы C#, ООП, коллекции, события и асинхронность на примерах из игровой разработки.", 2290, "4.9", "17 часов", "Начальный", "C#", "#3D5B75")
    )

    private lateinit var root: LinearLayout
    private lateinit var contentBox: LinearLayout
    private lateinit var scroll: ScrollView
    private var currentTab = "Главная"
    private var selectedCategory = "Все"
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
        prefs.getString(key, "").orEmpty().split(",").mapNotNull { it.toIntOrNull() }.toSet()

    private fun saveState() {
        prefs.edit()
            .putString("favorites", favorites.joinToString(","))
            .putString("cart", cart.joinToString(","))
            .putString("purchased", purchased.joinToString(","))
            .apply()
    }

    private fun buildShell() {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(bg)
            setPadding(dp(18), dp(10), dp(18), dp(8))
        }
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, dp(8), 0, dp(14))
        }
        val brand = text("CourseMarket", 23, white, true)
        header.addView(brand, LinearLayout.LayoutParams(0, dp(44), 1f))
        val cartButton = button("Корзина  •  " + cart.size, false) {
            currentTab = "Корзина"
            showCart()
        }
        header.addView(cartButton)
        root.addView(header)

        scroll = ScrollView(this).apply {
            isFillViewport = true
            clipToPadding = false
        }
        contentBox = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        scroll.addView(contentBox)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(0, dp(10), 0, dp(2))
            background = rounded(surface, 18)
        }
        listOf("Главная", "Каталог", "Избранное", "Корзина", "Профиль").forEach { tab ->
            val label = when (tab) {
                "Главная" -> "⌂\nГлавная"
                "Каталог" -> "▦\nКаталог"
                "Избранное" -> "♡\nИзбранное"
                "Корзина" -> "▣\nКорзина"
                else -> "●\nПрофиль"
            }
            val item = text(label, 10, if (currentTab == tab) accent else muted, currentTab == tab).apply {
                gravity = Gravity.CENTER
                setPadding(dp(2), dp(4), dp(2), dp(6))
                isClickable = true
                setOnClickListener { navigate(tab) }
            }
            nav.addView(item, LinearLayout.LayoutParams(0, dp(54), 1f))
        }
        root.addView(nav, LinearLayout.LayoutParams(-1, dp(62)))
        setContentView(root)
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
        val heading = text(title, 26, white, true)
        heading.setPadding(0, dp(8), 0, dp(4))
        contentBox.addView(heading)
        if (subtitle != null) {
            val sub = text(subtitle, 13, muted)
            sub.setPadding(0, 0, 0, dp(16))
            contentBox.addView(sub)
        } else {
            contentBox.addView(space(12))
        }
        refreshHeaderAndNav()
    }

    private fun refreshHeaderAndNav() {
        val header = root.getChildAt(0) as LinearLayout
        (header.getChildAt(1) as TextView).text = "Корзина  •  " + cart.size
        val nav = root.getChildAt(2) as LinearLayout
        for (i in 0 until nav.childCount) {
            val item = nav.getChildAt(i) as TextView
            val tab = listOf("Главная", "Каталог", "Избранное", "Корзина", "Профиль")[i]
            item.setTextColor(if (currentTab == tab) accent else muted)
            item.setTypeface(null, if (currentTab == tab) Typeface.BOLD else Typeface.NORMAL)
        }
    }

    private fun showHome() {
        currentTab = "Главная"
        render("Учись новому.", "Курсы, которые помогают двигаться вперёд.")
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded("#302747", 22)
            setPadding(dp(20), dp(20), dp(20), dp(20))
        }
        hero.addView(text("ТВОЁ СЛЕДУЮЩЕЕ НАЧИНАНИЕ", 10, accent, true))
        hero.addView(space(10))
        hero.addView(text("Прокачай навыки.\nСоздай больше.", 25, white, true))
        hero.addView(space(8))
        hero.addView(text("Практические курсы от людей, которые делают.", 13, Color.rgb(218, 211, 235)))
        hero.addView(space(16))
        hero.addView(button("Смотреть каталог  →", true) { navigate("Каталог") })
        contentBox.addView(hero)
        sectionTitle("Популярные курсы")
        courses.take(4).forEach { addCourseCard(it) }
        sectionTitle("Выбирай направление")
        listOf("Разработка", "Дизайн", "Геймдизайн").forEach { category ->
            contentBox.addView(button("✦   $category   →", false) {
                selectedCategory = category
                navigate("Каталог")
            }, matchWrap())
            contentBox.addView(space(8))
        }
    }

    private fun showCatalog() {
        currentTab = "Каталог"
        render("Каталог курсов", "Найди курс под свою следующую цель.")
        val search = EditText(this).apply {
            hint = "Поиск по названию или автору"
            setHintTextColor(muted)
            setTextColor(white)
            textSize = 14f
            setSingleLine(true)
            inputType = InputType.TYPE_CLASS_TEXT
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(surface, 14)
            setText(searchQuery)
        }
        contentBox.addView(search, LinearLayout.LayoutParams(-1, dp(50)))
        contentBox.addView(space(12))
        val results = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        val categories = listOf("Все", "Разработка", "Дизайн", "Геймдизайн")
        val chips = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val chipRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        categories.forEach { category ->
            val chip = button(category, selectedCategory == category) {
                selectedCategory = category
                showCatalog()
            }
            val lp = LinearLayout.LayoutParams(-2, dp(40))
            lp.marginEnd = dp(8)
            chipRow.addView(chip, lp)
        }
        chips.addView(chipRow)
        contentBox.addView(chips)
        contentBox.addView(space(14))
        contentBox.addView(results)
        fun updateResults(query: String) {
            results.removeAllViews()
            val filtered = courses.filter {
                (selectedCategory == "Все" || it.category == selectedCategory) &&
                    (it.title.contains(query, true) || it.author.contains(query, true) || it.category.contains(query, true))
            }
            if (filtered.isEmpty()) {
                results.addView(emptyState("Ничего не найдено", "Попробуй изменить запрос или категорию."))
            } else {
                filtered.forEach { addCourseCard(it, results) }
            }
        }
        updateResults(searchQuery)
        search.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchQuery = s?.toString().orEmpty()
                updateResults(searchQuery)
            }
            override fun afterTextChanged(s: android.text.Editable?) {}
        })
    }

    private fun showFavorites() {
        currentTab = "Избранное"
        render("Избранное", "Курсы, которые ты сохранил.")
        val list = courses.filter { favorites.contains(it.id) }
        if (list.isEmpty()) contentBox.addView(emptyState("Пока пусто", "Нажимай ♡ на карточках, чтобы сохранить курсы."))
        else list.forEach { addCourseCard(it) }
    }

    private fun showCart() {
        currentTab = "Корзина"
        render("Корзина", "Проверь выбранные курсы перед заказом.")
        val items = courses.filter { cart.contains(it.id) }
        if (items.isEmpty()) {
            contentBox.addView(emptyState("Корзина пуста", "Добавь интересные курсы из каталога."))
            contentBox.addView(button("Перейти в каталог", true) { navigate("Каталог") })
            return
        }
        items.forEach { course ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                background = rounded(surface, 16)
                setPadding(dp(14), dp(12), dp(14), dp(12))
            }
            row.addView(text(course.title, 15, white, true))
            row.addView(space(4))
            row.addView(text(money(course.price), 14, accent, true))
            row.addView(space(8))
            row.addView(button("Убрать из корзины", false) {
                cart.remove(course.id)
                saveState()
                showCart()
            })
            contentBox.addView(row, matchWrap())
            contentBox.addView(space(10))
        }
        val total = items.sumOf { it.price }
        val summary = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface2, 16)
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }
        summary.addView(text("Итого", 13, muted))
        summary.addView(text(money(total), 26, white, true))
        summary.addView(space(12))
        summary.addView(button("Оформить заказ", true) { checkout(items) })
        contentBox.addView(space(8))
        contentBox.addView(summary)
    }

    private fun checkout(items: List<Course>) {
        AlertDialog.Builder(this)
            .setTitle("Подтверждение заказа")
            .setMessage("Демо-оформление заказа на " + money(items.sumOf { it.price }) + ". Реальная оплата пока не подключена. Добавить курсы в покупки?")
            .setNegativeButton("Отмена", null)
            .setPositiveButton("Подтвердить") { _, _ ->
                items.forEach { purchased.add(it.id); cart.remove(it.id) }
                saveState()
                Toast.makeText(this, "Заказ оформлен! Курсы доступны в профиле.", Toast.LENGTH_LONG).show()
                showProfile()
            }
            .show()
    }

    private fun showProfile() {
        currentTab = "Профиль"
        render("Твой профиль", "Личный кабинет CourseMarket.")
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded("#302747", 18)
            setPadding(dp(18), dp(18), dp(18), dp(18))
        }
        panel.addView(text("УЧЕНИК COURSEMARKET", 10, accent, true))
        panel.addView(space(8))
        panel.addView(text("Добро пожаловать!", 22, white, true))
        panel.addView(text("Твой прогресс начинается с одного курса.", 13, Color.LTGRAY))
        contentBox.addView(panel)
        sectionTitle("Моя статистика")
        val stats = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        stats.addView(statCard("Куплено", purchased.size.toString()), LinearLayout.LayoutParams(0, dp(86), 1f))
        stats.addView(space(8))
        stats.addView(statCard("Избранное", favorites.size.toString()), LinearLayout.LayoutParams(0, dp(86), 1f))
        stats.addView(space(8))
        stats.addView(statCard("В корзине", cart.size.toString()), LinearLayout.LayoutParams(0, dp(86), 1f))
        contentBox.addView(stats)
        sectionTitle("Мои курсы")
        val owned = courses.filter { purchased.contains(it.id) }
        if (owned.isEmpty()) {
            contentBox.addView(emptyState("Здесь появятся твои курсы", "Оформи заказ в корзине — покупки будут доступны здесь."))
        } else {
            owned.forEach { course ->
                val row = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    background = rounded(surface, 14)
                    setPadding(dp(14), dp(12), dp(14), dp(12))
                }
                row.addView(text(course.title, 15, white, true))
                row.addView(text("Доступ открыт  •  " + course.duration, 12, muted))
                contentBox.addView(row, matchWrap())
                contentBox.addView(space(8))
            }
        }
        sectionTitle("О приложении")
        contentBox.addView(text("CourseMarket — демонстрационный магазин образовательных курсов. Заказы и избранное сохраняются на этом устройстве. Реальные платежи и серверная учётная запись не подключены.", 13, muted))
    }

    private fun addCourseCard(course: Course, target: LinearLayout = contentBox) {
        val card = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = rounded(surface, 18)
            setPadding(dp(14), dp(14), dp(14), dp(14))
        }
        val art = LinearLayout(this).apply {
            gravity = Gravity.CENTER
            background = rounded(course.tint, 14)
        }
        art.addView(text(course.icon, 34, white, true))
        card.addView(art, LinearLayout.LayoutParams(-1, dp(104)))
        card.addView(space(12))
        card.addView(text(course.category.uppercase() + "  •  " + course.level, 10, accent, true))
        card.addView(space(5))
        card.addView(text(course.title, 17, white, true))
        card.addView(space(4))
        card.addView(text("Автор: " + course.author, 12, muted))
        card.addView(space(8))
        card.addView(text("★ " + course.rating + "     ◷ " + course.duration, 12, Color.rgb(225, 194, 125)))
        card.addView(space(10))
        val actions = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        actions.addView(text(money(course.price), 19, white, true), LinearLayout.LayoutParams(0, dp(42), 1f))
        actions.addView(button(if (favorites.contains(course.id)) "♥" else "♡", false) {
            if (!favorites.add(course.id)) favorites.remove(course.id)
            saveState()
            when (currentTab) {
                "Избранное" -> showFavorites()
                "Каталог" -> showCatalog()
                else -> showHome()
            }
        })
        val detail = button("Подробнее", true) { showDetails(course) }
        val detailLp = LinearLayout.LayoutParams(-2, dp(42))
        detailLp.marginStart = dp(8)
        actions.addView(detail, detailLp)
        card.addView(actions)
        target.addView(card, matchWrap())
        target.addView(space(12))
    }

    private fun showDetails(course: Course) {
        val message = course.description + "\n\nАвтор: " + course.author +
            "\nУровень: " + course.level + "\nДлительность: " + course.duration +
            "\nРейтинг: ★ " + course.rating + "\nЦена: " + money(course.price)
        AlertDialog.Builder(this)
            .setTitle(course.title)
            .setMessage(message)
            .setNeutralButton(if (favorites.contains(course.id)) "Убрать из избранного" else "В избранное") { _, _ ->
                if (!favorites.add(course.id)) favorites.remove(course.id)
                saveState()
                Toast.makeText(this, "Избранное обновлено", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Закрыть", null)
            .setPositiveButton(if (cart.contains(course.id)) "Уже в корзине" else "В корзину") { _, _ ->
                if (cart.add(course.id)) {
                    saveState()
                    Toast.makeText(this, "Курс добавлен в корзину", Toast.LENGTH_SHORT).show()
                } else {
                    navigate("Корзина")
                }
            }
            .show()
    }

    private fun sectionTitle(value: String) {
        val title = text(value, 19, white, true)
        title.setPadding(0, dp(20), 0, dp(12))
        contentBox.addView(title)
    }

    private fun emptyState(title: String, description: String): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = rounded(surface, 18)
            setPadding(dp(20), dp(28), dp(20), dp(28))
        }
        box.addView(text("✦", 30, accent, true))
        box.addView(space(8))
        box.addView(text(title, 17, white, true))
        box.addView(space(5))
        box.addView(text(description, 12, muted).apply { gravity = Gravity.CENTER })
        return box
    }

    private fun statCard(label: String, value: String): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = rounded(surface, 14)
        }
        box.addView(text(value, 22, accent, true))
        box.addView(text(label, 10, muted))
        return box
    }

    private fun button(label: String, primary: Boolean, action: () -> Unit): TextView {
        return TextView(this).apply {
            text = label
            textSize = 13f
            gravity = Gravity.CENTER
            setTypeface(null, Typeface.BOLD)
            setTextColor(if (primary) Color.rgb(25, 20, 38) else white)
            setPadding(dp(14), 0, dp(14), 0)
            background = rounded(if (primary) accent else surface2, 12)
            isClickable = true
            isFocusable = true
            setOnClickListener { action() }
        }
    }

    private fun text(value: String, size: Int, color: Int, bold: Boolean = false): TextView =
        TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(null, Typeface.BOLD)
        }

    private fun rounded(color: Int, radius: Int): GradientDrawable =
        GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(radius).toFloat()
        }

    private fun rounded(color: String, radius: Int): GradientDrawable =
        rounded(Color.parseColor(color), radius)

    private fun space(height: Int): View = View(this).apply {
        layoutParams = LinearLayout.LayoutParams(1, dp(height))
    }

    private fun matchWrap() = LinearLayout.LayoutParams(-1, -2)

    private fun money(value: Int): String = String.format("%,d ₽", value).replace(',', ' ')

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
