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
class XmlEntityValidation(id: ValidationId, messages: ValidationMessages) : AbstractValidationAction(id, messages.xmlEntityValidatorDesc(), messages) {

    override val sourceExample: String
        get() = "Pepper &amp;amp; salt"

    override val targetExample: String
        get() = "Pepper amp<span class='js-example__target txt--warning'> incomplete entity, missing '& and ;'</span> salt"

    public override fun doValidate(source: String, target: String): List<String> {
        return validateIncompleteEntity(target)
    }

    private fun validateIncompleteEntity(target: String): List<String> {
        val errors = mutableListOf<String>()

        val words = target.split(" ").map { it.trim() }.filter { !it.isEmpty() }

        for (w in words) {
            var word = w
            if (word.contains(ENTITY_START_CHAR) && word.length > 1) {
                word = replaceEntityWithEmptyString(charRefExp, word)
                word = replaceEntityWithEmptyString(decimalRefExp, word)
                word = replaceEntityWithEmptyString(hexadecimalRefExp, word)

                if (word.contains(ENTITY_START_CHAR)) {
                    // remove any string that occurs in front
                    word = word.substring(word.indexOf(ENTITY_START_CHAR))
                    errors.add(messages.invalidXMLEntity(word))
                }
            }
        }
        return errors
    }

    companion object {
        // &amp;, &quot;
        private val charRefRegex = "&[:a-z_A-Z][a-z_A-Z0-9.-]*;"
        private val charRefExp = Regex(charRefRegex)

        // &#[numeric]
        private val decimalRefRegex = ".*&#[0-9]+;"
        private val decimalRefExp = Regex(decimalRefRegex)

        // &#x[hexadecimal]
        private val hexadecimalRefRegex = ".*&#x[0-9a-f_A-F]+;"
        private val hexadecimalRefExp = Regex(hexadecimalRefRegex)

        private val ENTITY_START_CHAR = "&"

        /**
         * Replace matched string with empty string
         *
         * @param regex
         * @param s
         * @return
         */
        private fun replaceEntityWithEmptyString(regex: Regex, s: String): String {
            var text = s
            var result = regex.find(text)
            while (result != null) {
                // replace match entity with empty string
                text = text.replace(result.groupValues[0], "")
                result = regex.find(text)
            }
            return text
        }
    }
}
