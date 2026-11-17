package com.example.coursemarket

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray

class CourseMarketDatabase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        createSchema(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Additive migration strategy: create any newly introduced tables and indexes.
        // Existing rows are preserved.
        createSchema(db)
    }

    private fun createSchema(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT NOT NULL UNIQUE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL COLLATE NOCASE UNIQUE,
                password_hash TEXT NOT NULL,
                password_salt TEXT NOT NULL,
                role TEXT NOT NULL DEFAULT 'CLIENT',
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS courses (
                id INTEGER PRIMARY KEY,
                category_id INTEGER NOT NULL,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                description TEXT NOT NULL,
                price INTEGER NOT NULL CHECK(price >= 0),
                rating TEXT NOT NULL DEFAULT '0.0',
                duration TEXT NOT NULL,
                level TEXT NOT NULL,
                icon TEXT NOT NULL,
                tint TEXT NOT NULL,
                lessons_count INTEGER NOT NULL DEFAULT 0,
                outcomes_json TEXT NOT NULL DEFAULT '[]',
                is_active INTEGER NOT NULL DEFAULT 1,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(category_id) REFERENCES categories(id)
                    ON UPDATE CASCADE ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS lessons (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                course_id INTEGER NOT NULL,
                lesson_index INTEGER NOT NULL,
                title TEXT NOT NULL,
                introduction TEXT NOT NULL,
                key_points_json TEXT NOT NULL,
                example TEXT NOT NULL,
                practice TEXT NOT NULL,
                question TEXT NOT NULL,
                answers_json TEXT NOT NULL,
                correct_answer INTEGER NOT NULL,
                explanation TEXT NOT NULL,
                UNIQUE(course_id, lesson_index),
                FOREIGN KEY(course_id) REFERENCES courses(id)
                    ON UPDATE CASCADE ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favorites (
                user_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(user_id, course_id),
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS cart_items (
                user_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL DEFAULT 1 CHECK(quantity > 0),
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(user_id, course_id),
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS orders (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_number TEXT NOT NULL UNIQUE,
                user_id INTEGER NOT NULL,
                customer_name TEXT NOT NULL,
                customer_email TEXT NOT NULL,
                payment_method TEXT NOT NULL,
                status TEXT NOT NULL DEFAULT 'CREATED_DEMO',
                total INTEGER NOT NULL CHECK(total >= 0),
                created_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS order_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                order_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                quantity INTEGER NOT NULL DEFAULT 1 CHECK(quantity > 0),
                unit_price INTEGER NOT NULL CHECK(unit_price >= 0),
                FOREIGN KEY(order_id) REFERENCES orders(id) ON DELETE CASCADE,
                FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE RESTRICT
            )
            """.trimIndent()
        )
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS lesson_progress (
                user_id INTEGER NOT NULL,
                course_id INTEGER NOT NULL,
                lesson_id INTEGER NOT NULL,
                completed_at TEXT NOT NULL DEFAULT CURRENT_TIMESTAMP,
                PRIMARY KEY(user_id, lesson_id),
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(course_id) REFERENCES courses(id) ON DELETE CASCADE,
                FOREIGN KEY(lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_courses_category ON courses(category_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_lessons_course ON lessons(course_id, lesson_index)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_orders_user ON orders(user_id, created_at)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_progress_user_course ON lesson_progress(user_id, course_id)")
    }

    fun seedCatalog(courses: List<Course>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val categoryIds = mutableMapOf<String, Long>()
            courses.map { it.category }.distinct().forEach { category ->
                val values = ContentValues().apply { put("name", category) }
                db.insertWithOnConflict("categories", null, values, SQLiteDatabase.CONFLICT_IGNORE)
                db.rawQuery("SELECT id FROM categories WHERE name = ?", arrayOf(category)).use { cursor ->
                    if (cursor.moveToFirst()) categoryIds[category] = cursor.getLong(0)
                }
            }

            courses.forEach { course ->
                val categoryId = categoryIds[course.category] ?: return@forEach
                val courseValues = ContentValues().apply {
                    put("id", course.id)
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
                val updated = db.update("courses", courseValues, "id = ?", arrayOf(course.id.toString()))
                if (updated == 0) db.insertOrThrow("courses", null, courseValues)

                course.modules.forEachIndexed { index, moduleTitle ->
                    val lesson = CourseLearningContent.get(course.id, index, moduleTitle)
                    val lessonValues = ContentValues().apply {
                        put("course_id", course.id)
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
                    }
                    db.insertWithOnConflict(
                        "lessons", null, lessonValues, SQLiteDatabase.CONFLICT_REPLACE
                    )
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun ensureAdminAccount() {
        val db = writableDatabase
        db.rawQuery(
            "SELECT id FROM users WHERE username = ? COLLATE NOCASE",
            arrayOf(DEFAULT_ADMIN_USERNAME)
        ).use { cursor ->
            if (cursor.moveToFirst()) return
        }
        val salt = PasswordSecurity.newSalt()
        val values = ContentValues().apply {
            put("username", DEFAULT_ADMIN_USERNAME)
            put("password_salt", salt)
            put("password_hash", PasswordSecurity.hash(DEFAULT_ADMIN_PASSWORD, salt))
            put("role", "ADMIN")
        }
        db.insertWithOnConflict("users", null, values, SQLiteDatabase.CONFLICT_IGNORE)
    }

    companion object {
        const val DATABASE_NAME = "coursemarket.db"
        const val DATABASE_VERSION = 1
        const val DEFAULT_ADMIN_USERNAME = "admin"
        const val DEFAULT_ADMIN_PASSWORD = "admin123"
    }
}

internal object PasswordSecurity {
    fun newSalt(): String {
        val bytes = ByteArray(16)
        java.security.SecureRandom().nextBytes(bytes)
        return android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
    }

    fun hash(password: String, saltBase64: String): String {
        val salt = android.util.Base64.decode(saltBase64, android.util.Base64.NO_WRAP)
        val spec = javax.crypto.spec.PBEKeySpec(password.toCharArray(), salt, 120_000, 256)
        return try {
            val bytes = javax.crypto.SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
            android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)
        } finally {
            spec.clearPassword()
        }
    }
}
