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
package org.zanata.validators.impl

import org.zanata.validators.AbstractValidationAction
import org.zanata.validators.ValidationId
import org.zanata.validators.ValidationMessages

/**
 *
 * @author Alex Eng [aeng@redhat.com](mailto:aeng@redhat.com)
 */
class NewlineLeadTrailValidation(id: ValidationId,
                                 messages: ValidationMessages) : AbstractValidationAction(id, messages.newLineValidatorDesc(), messages) {

    override val sourceExample: String
        get() = "\\n hello world with lead new line"

    override val targetExample: String
        get() = "<span class='js-example__target txt--warning'>missing \\n</span> hello world with lead new line"

    public override fun doValidate(source: String, target: String): List<String> {
        val errors = mutableListOf<String>()

        if (notShareLeading(source, target)) {
            errors.add(messages.leadingNewlineMissing())
        }

        if (notShareLeading(target, source)) {
            errors.add(messages.leadingNewlineAdded())
        }

        if (notShareTrailing(source, target)) {
            errors.add(messages.trailingNewlineMissing())
        }

        if (notShareTrailing(target, source)) {
            errors.add(messages.trailingNewlineAdded())
        }

        return errors
    }

    private fun notShareTrailing(source: String, target: String): Boolean {
        return !shareTrailing(source, target)
    }

    private fun notShareLeading(source: String, target: String): Boolean {
        return !shareLeading(source, target)
    }

    /**
     * @return false if base has a leading newline and test does not, true
     * otherwise
     */
    private fun shareLeading(base: String, test: String): Boolean {
        return if (base.startsWith("\n")) test.startsWith("\n") else true
        // no newline so can't fail
    }

    /**
     * @return false if base has a trailing newline and test does not, true
     * otherwise
     */
    private fun shareTrailing(base: String, test: String): Boolean {
        return if (base.endsWith("\n")) test.endsWith("\n") else true
        // no newline so can't fail
    }

}
