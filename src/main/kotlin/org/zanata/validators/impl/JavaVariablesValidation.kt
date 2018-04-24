/*
 * Copyright 2012, Red Hat, Inc. and individual contributors
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
 * Checks for consistent java-style variables between two strings.
 *
 * The current implementation will only check that each argument index is used a
 * consistent number of times. This will be extended in future to check that
 * each argument index is used with the same FormatType.
 *
 * @author David Mason, damason@redhat.com
 * @see http://docs.oracle.com/javase/7/docs/api/java/text/MessageFormat.html
 */
class JavaVariablesValidation(id: ValidationId, messages: ValidationMessages) : AbstractValidationAction(id, messages.javaVariablesValidatorDesc(), messages) {

    override val sourceExample: String
        get() = "value must be between {0} and {1}"

    override val targetExample: String
        get() = "value must be between {0} and <span class='js-example__target txt--warning'>{2}</span>"

    public override fun doValidate(source: String, target: String): List<String> {
        val errors = mutableListOf<String>()

        val sourceInfo = analyseString(source)
        val targetInfo = analyseString(target)

        // check if any indices are added/missing

        val missing = mutableListOf<String>()
        val missingQuoted = mutableListOf<String>()
        val added = mutableListOf<String>()
        val addedQuoted = mutableListOf<String>()
        val different = mutableListOf<String>()

        for ((key, value) in sourceInfo.varCounts!!) {
            val targetCount = targetInfo.varCounts!!.remove(key)
            if (targetCount == null) {
                val quotedCount = targetInfo.quotedVarCounts!![key]
                if (quotedCount != null && quotedCount > 0) {
                    missingQuoted.add("{$key}")
                } else {
                    missing.add("{$key}")
                }
            } else if (value != targetCount) {
                if (targetInfo.quotedVars.contains(key)) {
                    missingQuoted.add("{$key}")
                } else {
                    different.add("{$key}")
                }
            }
        }

        // TODO could warn if they were quoted in original
        for (targetVar in targetInfo.varCounts!!.keys) {
            if (sourceInfo.quotedVarCounts!!.containsKey(targetVar)) {
                addedQuoted.add("{$targetVar}")
            } else {
                added.add("{$targetVar}")
            }
        }

        // Sort variable lists to ensure consistent ordering of variables
        // in error messages:
        missing.sort()
        missingQuoted.sort()
        added.sort()
        addedQuoted.sort()
        different.sort()

        val looksLikeMessageFormatString = !sourceInfo.varCounts!!.isEmpty()

        if (!missing.isEmpty()) {
            errors.add(messages.varsMissing(missing))
        }

        if (looksLikeMessageFormatString && sourceInfo.singleApostrophes != targetInfo.singleApostrophes) {
            // different number of apos.
            errors.add(messages.differentApostropheCount())
        }
        if (looksLikeMessageFormatString && sourceInfo.quotedChars == 0
                && targetInfo.quotedChars > 0) {
            // quoted chars in target but not source
            errors.add(messages.quotedCharsAdded())
        }
        if (!missingQuoted.isEmpty()) {
            errors.add(messages.varsMissingQuoted(missingQuoted))
        }
        if (!added.isEmpty()) {
            errors.add(messages.varsAdded(added))
        }
        if (!addedQuoted.isEmpty()) {
            errors.add(messages.varsAddedQuoted(addedQuoted))
        }
        if (!different.isEmpty()) {
            errors.add(messages.differentVarCount(different))
        }

        // TODO check if indices are used with the same format types
        // e.g. "You owe me {0, currency}" --> "Du schuldest mir {0, percent}"
        // is not correct

        return errors
    }

    private fun countIndices(fullVars: List<String>): HashMap<String, Int> {
        val argumentIndexCounts = HashMap<String, Int>()
        for (fullVar in fullVars) {
            var argIndexEnd = fullVar.indexOf(',')
            argIndexEnd = if (argIndexEnd != -1) argIndexEnd else fullVar.length - 1
            val argumentIndex = fullVar.substring(1, argIndexEnd).trim { it <= ' ' }

            if (argumentIndexCounts.containsKey(argumentIndex))
                argumentIndexCounts.put(argumentIndex,
                        argumentIndexCounts[argumentIndex]!! + 1)
            else
                argumentIndexCounts.put(argumentIndex, 1)
        }
        return argumentIndexCounts
    }

    private fun analyseString(inString: String): StringInfo {
        val descriptor = StringInfo()

        // stack of opening brace positions, replace if better gwt LIFO
        // collection found
        val openings = mutableListOf<Int>()
        val quotedOpenings = mutableListOf<Int>()

        val escapeChars = mutableListOf<Char>()
        escapeChars.add('\\')

        var isEscaped = false
        var isQuoted = false
        var quotedLength = 0

        // scan for opening brace
        for (i in 0 until inString.length) {
            // escaping skips a single character
            if (isEscaped) {
                isEscaped = false
                continue
            }

            // TODO add handling of quoting within SubFormatPatternParts and
            // Strings

            val c = inString[i]

            // begin or end quoted sections
            if (c == '\'') {
                if (isQuoted) {
                    if (quotedLength == 0) {
                        // don't count doubled quotes
                        descriptor.singleApostrophes--
                    }
                    isQuoted = false
                } else {
                    isQuoted = true
                    quotedLength = 0
                    quotedOpenings.clear()
                    descriptor.singleApostrophes++
                }
                continue
            }

            if (isQuoted) {
                quotedLength++
                descriptor.quotedChars++

                // identify quoted variables (not valid variables, identified to
                // warn user)
                if (c == '{') {
                    quotedOpenings.add(i)
                } else if (c == '}' && quotedOpenings.size > 0) {
                    val variable = inString.substring(quotedOpenings
                            .removeAt(quotedOpenings.size - 1), i + 1)
                    descriptor.quotedVars.add(variable)
                }

                continue
            }

            // identify escape character (intentionally after quoted section
            // handling)
            if (escapeChars.contains(c)) {
                isEscaped = true
                continue
            }

            // identify non-quoted variables
            if (c == '{') {
                openings.add(i)
            } else if (c == '}' && openings.size > 0) {
                val variable = inString.substring(
                        openings.removeAt(openings.size - 1), i + 1)
                descriptor.vars.add(variable)
            }
        }

        descriptor.varCounts = countIndices(descriptor.vars)
        descriptor.quotedVarCounts = countIndices(descriptor.quotedVars)

        return descriptor
    }

}

/**
 * Holds information about java variables, quoting etc. for a string.
 */
private class StringInfo {
    var quotedChars = 0
    var singleApostrophes = 0

    val vars = mutableListOf<String>()
    val quotedVars = mutableListOf<String>()

    internal var varCounts: HashMap<String, Int>? = null
    internal var quotedVarCounts: HashMap<String, Int>? = null
}
