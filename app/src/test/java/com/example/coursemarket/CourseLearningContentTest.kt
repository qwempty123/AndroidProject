package com.example.coursemarket

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CourseLearningContentTest {

    @Test
    fun everyCatalogCourseHasAuthoredLessons() {
        val expectedLessonCounts = mapOf(
            1 to 5,
            2 to 5,
            3 to 6,
            4 to 5,
            5 to 5,
            6 to 5,
            7 to 5,
            8 to 5,
            9 to 5,
            10 to 5,
            11 to 5,
            12 to 5
        )

        expectedLessonCounts.forEach { (courseId, expectedCount) ->
            assertEquals(
                "Unexpected authored lesson count for course $courseId",
                expectedCount,
                CourseLearningContent.lessonCount(courseId)
            )
        }
    }

    @Test
    fun everyLessonHasTheoryExampleTaskAndKnowledgeCheck() {
        val expectedLessonCounts = mapOf(
            1 to 5, 2 to 5, 3 to 6, 4 to 5,
            5 to 5, 6 to 5, 7 to 5, 8 to 5,
            9 to 5, 10 to 5, 11 to 5, 12 to 5
        )

        expectedLessonCounts.forEach { (courseId, count) ->
            repeat(count) { lessonIndex ->
                val lesson = CourseLearningContent.get(
                    courseId,
                    lessonIndex,
                    "Fallback should not be used"
                )

                assertTrue(lesson.title.isNotBlank())
                assertTrue(lesson.introduction.length > 80)
                assertTrue(lesson.keyPoints.size >= 2)
                assertTrue(lesson.example.isNotBlank())
                assertTrue(lesson.practice.length > 30)
                assertTrue(lesson.question.isNotBlank())
                assertEquals(3, lesson.answers.size)
                assertTrue(lesson.correctAnswer in lesson.answers.indices)
                assertTrue(lesson.explanation.isNotBlank())
                assertFalse(lesson.title.contains("Fallback"))
            }
        }
    }
}
