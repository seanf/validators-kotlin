package org.zanata.validators

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ValidationMessagesTest {
    private lateinit var messages: ValidationMessages

    @BeforeTest
    fun init() {
        messages = fakeValidationMessages
    }

    @Test
    fun ab() {
        assertEquals("ab: [1,2]", messages.ab(1, 2))
    }

    @Test
    fun differentApostropheCount() {
        assertEquals("differentApostropheCount", messages.differentApostropheCount())
    }

    @Test
    fun differentVarCount() {
        val msg = messages.differentVarCount(listOf("foo"))
//        assertTrue(msg.startsWith("differentVarCount"))
//        assertTrue(msg.endsWith("[foo]"))
        assertEquals("differentVarCount: [[foo]]", msg)
    }
}
