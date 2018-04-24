package org.zanata.validators

import org.zanata.validators.impl.NewlineLeadTrailValidation
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/*
 * Copyright 2011, Red Hat, Inc. and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

/**
 *
 * @author Alex Eng [aeng@redhat.com](mailto:aeng@redhat.com)
 */
class NewlineLeadTrailValidationTest {
    private lateinit var messages: ValidationMessages
    private lateinit var newlineLeadTrailValidation: NewlineLeadTrailValidation

    @BeforeTest
    fun init() {
        messages = fakeValidationMessages
        newlineLeadTrailValidation = NewlineLeadTrailValidation(ValidationId.NEW_LINE, messages).apply {
            rules.isEnabled = true
        }
    }

    @Test
    fun idIsSet() {
        assertEquals(ValidationId.NEW_LINE, newlineLeadTrailValidation.id)
    }

    @Test
    fun noNewlinesBothMatch() {
        val source = "String without newlines"
        val target = "Different newline-devoid string"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(emptyList(), errorList)
    }

    @Test
    fun bothNewlinesBothMatch() {
        val source = "\nString with both newlines\n"
        val target = "\nDifferent newline-infested string\n"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(emptyList(), errorList)
    }

    @Test
    fun missingLeadingNewline() {
        val source = "\nTesting string with leading new line"
        val target = "Different string with the newline removed"
        val errorList = newlineLeadTrailValidation.validate(source, target)
        println(errorList)

        assertEquals(listOf(messages.leadingNewlineMissing()), errorList)
    }

    @Test
    fun addedLeadingNewline() {
        val source = "Testing string without a leading new line"
        val target = "\nDifferent string with a leading newline added"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(listOf(messages.leadingNewlineAdded()), errorList)
    }

    @Test
    fun missingTrailingNewline() {
        val source = "Testing string with trailing new line\n"
        val target = "Different string with the newline removed"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(listOf(messages.trailingNewlineMissing()), errorList)
    }

    @Test
    fun addedTrailingNewline() {
        val source = "Testing string without a trailing new line"
        val target = "Different string with a trailing newline added\n"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(listOf(messages.trailingNewlineAdded()), errorList)
    }

    @Test
    fun addedBothNewlines() {
        val source = "Testing string with no newlines"
        val target = "\nDifferent string with both added\n"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(listOf(messages.leadingNewlineAdded(), messages.trailingNewlineAdded()), errorList)
    }

    @Test
    fun missingBothNewlines() {
        val source = "\nString with both newlines\n"
        val target = "Other string with no newlines"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(listOf(messages.leadingNewlineMissing(), messages.trailingNewlineMissing()), errorList)
    }

    @Test
    fun addedAndMissing1() {
        val source = "\nString with only leading newline"
        val target = "Other string with newline trailing\n"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(listOf(messages.leadingNewlineMissing(), messages.trailingNewlineAdded()), errorList)
    }

    @Test
    fun addedAndMissing2() {
        val source = "String with trailing newline\n"
        val target = "\nOther string with newline leading"
        val errorList = newlineLeadTrailValidation.validate(source, target)

        assertEquals(listOf(messages.leadingNewlineAdded(), messages.trailingNewlineMissing()), errorList)
    }
}
