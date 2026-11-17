package com.example.coursemarket

import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CourseMarketDatabaseTest {

    @Test
    fun repositoryPersistsAccountsCartOrdersAndProgress() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(CourseMarketDatabase.DATABASE_NAME)

        val sample = Course(
            id = 1,
            title = "Тестовый курс",
            category = "Разработка",
            author = "Преподаватель",
            description = "Описание тестового курса для проверки локального хранилища.",
            price = 1200,
            rating = "5.0",
            duration = "5 часов",
            level = "Начальный",
            icon = "T",
            tint = "#514B83",
            lessons = 2,
            modules = listOf("Введение", "Практика"),
            outcomes = listOf("Проверить базу данных")
        )

        val repository = CourseMarketRepository(context)
        repository.initialize(listOf(sample))

        val courses = repository.getCourses()
        assertEquals(1, courses.size)
        assertEquals("Тестовый курс", courses.first().title)

        val account = repository.register("course_test", "secret123")
        assertNotNull(account)
        assertEquals("CLIENT", account.role)
        assertNotNull(repository.login("course_test", "secret123"))
        assertEquals(null, repository.login("course_test", "wrong-password"))

        repository.setFavorite(account.id, sample.id, true)
        repository.addToCart(account.id, sample.id)
        assertTrue(sample.id in repository.favoriteCourseIds(account.id))
        assertTrue(sample.id in repository.cartCourseIds(account.id))

        repository.replaceFavoritesAndCart(account.id, emptySet(), setOf(sample.id))
        assertTrue(repository.favoriteCourseIds(account.id).isEmpty())
        assertTrue(sample.id in repository.cartCourseIds(account.id))

        repository.setCartQuantity(account.id, sample.id, 3)
        assertEquals(3, repository.cartQuantities(account.id)[sample.id])

        val orderNumber = repository.createDemoOrder(
            account.id,
            "Тестовый Покупатель",
            "test@example.com",
            "СБП (демо)",
            listOf(sample),
            mapOf(sample.id to 3)
        )
        assertTrue(orderNumber.startsWith("CM-"))
        assertTrue(sample.id in repository.purchasedCourseIds(account.id))
        assertFalse(sample.id in repository.cartCourseIds(account.id))

        repository.completeLesson(account.id, sample.id, 0)
        assertEquals(setOf(0), repository.completedLessonIndexes(account.id)[sample.id])

        val order = repository.ordersForUser(account.id).single()
        assertEquals(orderNumber, order.number)
        assertEquals("CREATED_DEMO", order.status)
        assertEquals(sample.price * 3, order.total)
        assertEquals(3, order.itemCount)
        repository.updateOrderStatus(order.id, "CANCELLED")
        assertTrue(repository.purchasedCourseIds(account.id).isEmpty())
    }

    @Test
    fun registrationRejectsEmptyCredentials() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(CourseMarketDatabase.DATABASE_NAME)
        val repository = CourseMarketRepository(context)
        repository.initialize(emptyList())

        val failure = try {
            repository.register("", "")
            null
        } catch (error: IllegalArgumentException) {
            error
        }
        assertNotNull(failure)
    }

    @Test
    fun databaseHasRelatedTablesAndEnforcedForeignKeys() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        context.deleteDatabase(CourseMarketDatabase.DATABASE_NAME)
        val helper = CourseMarketDatabase(context)
        val db = helper.writableDatabase

        val requiredTables = setOf(
            "categories",
            "users",
            "courses",
            "lessons",
            "favorites",
            "cart_items",
            "orders",
            "order_items",
            "lesson_progress"
        )
        val actualTables = mutableSetOf<String>()
        db.rawQuery(
            "SELECT name FROM sqlite_master WHERE type = 'table'",
            null
        ).use { cursor ->
            while (cursor.moveToNext()) actualTables.add(cursor.getString(0))
        }
        assertTrue(actualTables.containsAll(requiredTables))

        val foreignKeysEnabled = db.rawQuery("PRAGMA foreign_keys", null).use {
            it.moveToFirst() && it.getInt(0) == 1
        }
        assertTrue(foreignKeysEnabled)
        helper.close()
    }
}
