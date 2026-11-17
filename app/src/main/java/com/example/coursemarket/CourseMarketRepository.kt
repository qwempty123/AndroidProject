package com.example.coursemarket

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.Locale

data class UserAccount(
    val id: Long,
    val username: String,
    val role: String
) {
    val isAdmin: Boolean get() = role == "ADMIN"
}

data class AdminOrder(
    val id: Long,
    val number: String,
    val username: String,
    val customerName: String,
    val email: String,
    val paymentMethod: String,
    val status: String,
    val total: Int,
    val createdAt: String,
    val itemCount: Int
)

class CourseMarketRepository(context: Context) {
    private val database = CourseMarketDatabase(context.applicationContext)

    suspend fun initialize(seedCourses: List<Course>) = withContext(Dispatchers.IO) {
        database.seedCatalog(seedCourses)
        database.ensureAdminAccount()
    }

    suspend fun getCourses(): List<Course> = withContext(Dispatchers.IO) {
        val db = database.readableDatabase
        val result = mutableListOf<Course>()
        db.rawQuery(
            """
            SELECT c.id, cat.name AS category, c.title, c.author, c.description,
                   c.price, c.rating, c.duration, c.level, c.icon, c.tint,
                   c.outcomes_json
            FROM courses c
            INNER JOIN categories cat ON cat.id = c.category_id
            WHERE c.is_active = 1
            ORDER BY c.id
            """.trimIndent(),
            null
        ).use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val moduleTitles = mutableListOf<String>()
                db.rawQuery(
                    "SELECT title FROM lessons WHERE course_id = ? ORDER BY lesson_index",
                    arrayOf(id.toString())
                ).use { lessons ->
                    while (lessons.moveToNext()) moduleTitles.add(lessons.getString(0))
                }
                val outcomesJson = cursor.getString(cursor.getColumnIndexOrThrow("outcomes_json"))
                val outcomes = JSONArray(outcomesJson).toStringList()
                result.add(
                    Course(
                        id = id,
                        title = cursor.getString(cursor.getColumnIndexOrThrow("title")),
                        category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                        author = cursor.getString(cursor.getColumnIndexOrThrow("author")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        price = cursor.getInt(cursor.getColumnIndexOrThrow("price")),
                        rating = cursor.getString(cursor.getColumnIndexOrThrow("rating")),
                        duration = cursor.getString(cursor.getColumnIndexOrThrow("duration")),
                        level = cursor.getString(cursor.getColumnIndexOrThrow("level")),
                        icon = cursor.getString(cursor.getColumnIndexOrThrow("icon")),
                        tint = cursor.getString(cursor.getColumnIndexOrThrow("tint")),
                        lessons = moduleTitles.size,
                        modules = moduleTitles,
                        outcomes = outcomes
                    )
                )
            }
        }
        result
    }

    suspend fun getCategories(): List<String> = withContext(Dispatchers.IO) {
        val names = mutableListOf<String>()
        database.readableDatabase.rawQuery(
            "SELECT name FROM categories ORDER BY name", null
        ).use { cursor ->
            while (cursor.moveToNext()) names.add(cursor.getString(0))
        }
        names
    }

    suspend fun register(usernameRaw: String, password: String): UserAccount =
        withContext(Dispatchers.IO) {
            val username = usernameRaw.trim()
            require(username.length in 3..24) { "Логин должен содержать от 3 до 24 символов." }
            require(username.matches(Regex("[A-Za-zА-Яа-я0-9_.-]+"))) {
                "В логине разрешены буквы, цифры, точка, дефис и подчёркивание."
            }
            require(password.length >= 6) { "Пароль должен содержать не менее 6 символов." }

            val salt = PasswordSecurity.newSalt()
            val values = ContentValues().apply {
                put("username", username)
                put("password_salt", salt)
                put("password_hash", PasswordSecurity.hash(password, salt))
                put("role", "CLIENT")
            }
            try {
                val id = database.writableDatabase.insertOrThrow("users", null, values)
                UserAccount(id, username, "CLIENT")
            } catch (_: SQLiteConstraintException) {
                throw IllegalArgumentException("Пользователь с таким логином уже существует.")
            }
        }

    suspend fun login(usernameRaw: String, password: String): UserAccount? =
        withContext(Dispatchers.IO) {
            val username = usernameRaw.trim()
            database.readableDatabase.rawQuery(
                """
                SELECT id, username, password_hash, password_salt, role
                FROM users WHERE username = ? COLLATE NOCASE LIMIT 1
                """.trimIndent(),
                arrayOf(username)
            ).use { cursor ->
                if (!cursor.moveToFirst()) return@withContext null
                val expected = cursor.getString(2)
                val salt = cursor.getString(3)
                val actual = PasswordSecurity.hash(password, salt)
                if (!java.security.MessageDigest.isEqual(
                        expected.toByteArray(Charsets.UTF_8),
                        actual.toByteArray(Charsets.UTF_8)
                    )
                ) return@withContext null
                UserAccount(cursor.getLong(0), cursor.getString(1), cursor.getString(4))
            }
        }

    suspend fun userById(userId: Long): UserAccount? = withContext(Dispatchers.IO) {
        database.readableDatabase.rawQuery(
            "SELECT id, username, role FROM users WHERE id = ?",
            arrayOf(userId.toString())
        ).use { cursor ->
            if (!cursor.moveToFirst()) null
            else UserAccount(cursor.getLong(0), cursor.getString(1), cursor.getString(2))
        }
    }

    suspend fun userCount(): Int = withContext(Dispatchers.IO) {
        database.readableDatabase.rawQuery("SELECT COUNT(*) FROM users", null).use {
            if (it.moveToFirst()) it.getInt(0) else 0
        }
    }

    suspend fun replaceFavoritesAndCart(
        userId: Long,
        favoriteIds: Set<Int>,
        cartIds: Set<Int>
    ) = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        db.beginTransaction()
        try {
            db.delete("favorites", "user_id = ?", arrayOf(userId.toString()))
            favoriteIds.forEach { courseId ->
                db.insertWithOnConflict(
                    "favorites", null,
                    ContentValues().apply {
                        put("user_id", userId)
                        put("course_id", courseId)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }

            db.delete("cart_items", "user_id = ?", arrayOf(userId.toString()))
            cartIds.forEach { courseId ->
                db.insertWithOnConflict(
                    "cart_items", null,
                    ContentValues().apply {
                        put("user_id", userId)
                        put("course_id", courseId)
                        put("quantity", 1)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    suspend fun favoriteCourseIds(userId: Long): Set<Int> = withContext(Dispatchers.IO) {
        val ids = mutableSetOf<Int>()
        database.readableDatabase.rawQuery(
            "SELECT course_id FROM favorites WHERE user_id = ?",
            arrayOf(userId.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) ids.add(cursor.getInt(0))
        }
        ids
    }

    suspend fun setFavorite(userId: Long, courseId: Int, favorite: Boolean) =
        withContext(Dispatchers.IO) {
            val db = database.writableDatabase
            if (favorite) {
                db.insertWithOnConflict(
                    "favorites", null,
                    ContentValues().apply {
                        put("user_id", userId)
                        put("course_id", courseId)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            } else {
                db.delete(
                    "favorites", "user_id = ? AND course_id = ?",
                    arrayOf(userId.toString(), courseId.toString())
                )
            }
        }

    suspend fun cartCourseIds(userId: Long): Set<Int> = withContext(Dispatchers.IO) {
        val ids = mutableSetOf<Int>()
        database.readableDatabase.rawQuery(
            "SELECT course_id FROM cart_items WHERE user_id = ?",
            arrayOf(userId.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) ids.add(cursor.getInt(0))
        }
        ids
    }

    suspend fun addToCart(userId: Long, courseId: Int) = withContext(Dispatchers.IO) {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("course_id", courseId)
            put("quantity", 1)
        }
        database.writableDatabase.insertWithOnConflict(
            "cart_items", null, values, SQLiteDatabase.CONFLICT_IGNORE
        )
    }

    suspend fun removeFromCart(userId: Long, courseId: Int) = withContext(Dispatchers.IO) {
        database.writableDatabase.delete(
            "cart_items", "user_id = ? AND course_id = ?",
            arrayOf(userId.toString(), courseId.toString())
        )
    }

    suspend fun purchasedCourseIds(userId: Long): Set<Int> = withContext(Dispatchers.IO) {
        val ids = mutableSetOf<Int>()
        database.readableDatabase.rawQuery(
            """
            SELECT DISTINCT oi.course_id
            FROM order_items oi
            INNER JOIN orders o ON o.id = oi.order_id
            WHERE o.user_id = ? AND o.status NOT IN ('CANCELLED', 'REJECTED')
            """.trimIndent(),
            arrayOf(userId.toString())
        ).use { cursor ->
            while (cursor.moveToNext()) ids.add(cursor.getInt(0))
        }
        ids
    }

    suspend fun completedLessonIndexes(userId: Long): Map<Int, MutableSet<Int>> =
        withContext(Dispatchers.IO) {
            val result = mutableMapOf<Int, MutableSet<Int>>()
            database.readableDatabase.rawQuery(
                """
                SELECT lp.course_id, l.lesson_index
                FROM lesson_progress lp
                INNER JOIN lessons l ON l.id = lp.lesson_id
                WHERE lp.user_id = ?
                """.trimIndent(),
                arrayOf(userId.toString())
            ).use { cursor ->
                while (cursor.moveToNext()) {
                    result.getOrPut(cursor.getInt(0)) { mutableSetOf() }.add(cursor.getInt(1))
                }
            }
            result
        }

    suspend fun completeLesson(userId: Long, courseId: Int, lessonIndex: Int) =
        withContext(Dispatchers.IO) {
            val db = database.writableDatabase
            val lessonId = db.rawQuery(
                "SELECT id FROM lessons WHERE course_id = ? AND lesson_index = ?",
                arrayOf(courseId.toString(), lessonIndex.toString())
            ).use { cursor ->
                if (cursor.moveToFirst()) cursor.getLong(0) else -1L
            }
            if (lessonId > 0) {
                db.insertWithOnConflict(
                    "lesson_progress",
                    null,
                    ContentValues().apply {
                        put("user_id", userId)
                        put("course_id", courseId)
                        put("lesson_id", lessonId)
                    },
                    SQLiteDatabase.CONFLICT_IGNORE
                )
            }
        }

    suspend fun createDemoOrder(
        userId: Long,
        customerName: String,
        customerEmail: String,
        paymentMethod: String,
        items: List<Course>
    ): String = withContext(Dispatchers.IO) {
        require(items.isNotEmpty()) { "Корзина пуста." }
        require(customerName.trim().length >= 2) { "Укажи имя покупателя." }
        require(android.util.Patterns.EMAIL_ADDRESS.matcher(customerEmail).matches()) {
            "Укажи корректный email."
        }

        val db = database.writableDatabase
        val orderNumber = "CM-" + System.currentTimeMillis().toString().takeLast(9)
        db.beginTransaction()
        try {
            val orderValues = ContentValues().apply {
                put("order_number", orderNumber)
                put("user_id", userId)
                put("customer_name", customerName.trim())
                put("customer_email", customerEmail.trim())
                put("payment_method", paymentMethod)
                put("status", "CREATED_DEMO")
                put("total", items.sumOf { it.price })
            }
            val orderId = db.insertOrThrow("orders", null, orderValues)
            items.forEach { course ->
                db.insertOrThrow(
                    "order_items",
                    null,
                    ContentValues().apply {
                        put("order_id", orderId)
                        put("course_id", course.id)
                        put("quantity", 1)
                        put("unit_price", course.price)
                    }
                )
                db.delete(
                    "cart_items", "user_id = ? AND course_id = ?",
                    arrayOf(userId.toString(), course.id.toString())
                )
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
        orderNumber
    }

    suspend fun ordersForUser(userId: Long): List<AdminOrder> =
        withContext(Dispatchers.IO) {
            queryOrders("WHERE o.user_id = ?", arrayOf(userId.toString()))
        }

    suspend fun allOrders(): List<AdminOrder> = withContext(Dispatchers.IO) {
        queryOrders("", emptyArray())
    }

    private fun queryOrders(where: String, args: Array<String>): List<AdminOrder> {
        val orders = mutableListOf<AdminOrder>()
        database.readableDatabase.rawQuery(
            """
            SELECT o.id, o.order_number, u.username, o.customer_name, o.customer_email,
                   o.payment_method, o.status, o.total, o.created_at,
                   (SELECT COUNT(*) FROM order_items oi WHERE oi.order_id = o.id) AS item_count
            FROM orders o
            INNER JOIN users u ON u.id = o.user_id
            $where
            ORDER BY o.id DESC
            """.trimIndent(),
            args
        ).use { cursor ->
            while (cursor.moveToNext()) {
                orders.add(
                    AdminOrder(
                        id = cursor.getLong(0),
                        number = cursor.getString(1),
                        username = cursor.getString(2),
                        customerName = cursor.getString(3),
                        email = cursor.getString(4),
                        paymentMethod = cursor.getString(5),
                        status = cursor.getString(6),
                        total = cursor.getInt(7),
                        createdAt = cursor.getString(8),
                        itemCount = cursor.getInt(9)
                    )
                )
            }
        }
        return orders
    }

    suspend fun updateOrderStatus(orderId: Long, status: String) =
        withContext(Dispatchers.IO) {
            require(status in setOf("CONFIRMED_DEMO", "CANCELLED", "REJECTED")) {
                "Недопустимый статус заказа."
            }
            database.writableDatabase.update(
                "orders",
                ContentValues().apply { put("status", status) },
                "id = ?",
                arrayOf(orderId.toString())
            )
        }

    suspend fun addCourse(course: Course): Int = withContext(Dispatchers.IO) {
        val db = database.writableDatabase
        val categoryId = ensureCategory(db, course.category)
        val id = db.insertOrThrow(
            "courses",
            null,
            ContentValues().apply {
                put("category_id", categoryId)
                put("title", course.title)
                put("author", course.author)
                put("description", course.description)
                put("price", course.price)
                put("rating", course.rating)
                put("duration", course.duration)
                put("level", course.level)
                put("icon", course.icon)
                put("tint", course.tint)
                put("lessons_count", course.modules.size)
                put("outcomes_json", JSONArray(course.outcomes).toString())
                put("is_active", 1)
            }
        ).toInt()
        seedLessons(db, id, course.modules)
        id
    }

    suspend fun updateCourse(
        courseId: Int,
        title: String,
        author: String,
        description: String,
        price: Int
    ): Boolean = withContext(Dispatchers.IO) {
        require(title.isNotBlank()) { "Название не может быть пустым." }
        require(author.isNotBlank()) { "Укажи автора курса." }
        require(description.isNotBlank()) { "Описание не может быть пустым." }
        require(price >= 0) { "Цена не может быть отрицательной." }
        database.writableDatabase.update(
            "courses",
            ContentValues().apply {
                put("title", title.trim())
                put("author", author.trim())
                put("description", description.trim())
                put("price", price)
            },
            "id = ?",
            arrayOf(courseId.toString())
        ) > 0
    }

    suspend fun deleteCourse(courseId: Int): Boolean = withContext(Dispatchers.IO) {
        try {
            database.writableDatabase.delete(
                "courses", "id = ?", arrayOf(courseId.toString())
            ) > 0
        } catch (_: SQLiteConstraintException) {
            false
        }
    }

    private fun ensureCategory(db: SQLiteDatabase, name: String): Long {
        db.insertWithOnConflict(
            "categories", null,
            ContentValues().apply { put("name", name) },
            SQLiteDatabase.CONFLICT_IGNORE
        )
        db.rawQuery("SELECT id FROM categories WHERE name = ?", arrayOf(name)).use {
            return if (it.moveToFirst()) it.getLong(0) else -1L
        }
    }

    private fun seedLessons(db: SQLiteDatabase, courseId: Int, modules: List<String>) {
        modules.forEachIndexed { index, title ->
            val lesson = CourseLearningContent.get(courseId, index, title)
            db.insertWithOnConflict(
                "lessons",
                null,
                ContentValues().apply {
                    put("course_id", courseId)
                    put("lesson_index", index)
                    put("title", lesson.title)
                    put("introduction", lesson.introduction)
                    put("key_points_json", JSONArray(lesson.keyPoints).toString())
                    put("example", lesson.example)
                    put("practice", lesson.practice)
                    put("question", lesson.question)
                    put("answers_json", JSONArray(lesson.answers).toString())
                    put("correct_answer", lesson.correctAnswer)
                    put("explanation", lesson.explanation)
                },
                SQLiteDatabase.CONFLICT_REPLACE
            )
        }
    }

    companion object {
        private fun JSONArray.toStringList(): List<String> =
            (0 until length()).mapNotNull { optString(it, null) }
    }
}
