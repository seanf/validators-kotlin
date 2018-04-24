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
open class PrintfVariablesValidation(id: ValidationId, description: String,
                                     messages: ValidationMessages) : AbstractValidationAction(id, description, messages) {

    override val sourceExample: String
        get() = "value must be between %x and %y"

    override val targetExample: String
        get() = "value must be between %x and <span class='js-example__target txt--warning'>%z</span>"

    constructor(id: ValidationId,
                messages: ValidationMessages) : this(id, messages.printfVariablesValidatorDesc(), messages) {
    }

    public override fun doValidate(source: String, target: String): List<String> {
        val errors = mutableListOf<String>()

        val sourceVars = findVars(source)
        val targetVars = findVars(target)

        var message = findMissingVariables(sourceVars, targetVars)
        if (!message.isNullOrEmpty()) {
            errors.add(message!!)
        }
        message = findAddedVariables(sourceVars, targetVars)
        if (!message.isNullOrEmpty()) {
            errors.add(message!!)
        }

        return errors
    }

    protected fun findMissingVariables(sourceVars: List<String>,
                                       targetVars: List<String>): String? {
        val missing = listMissing(sourceVars, targetVars)

        return if (!missing.isEmpty()) {
            messages.varsMissing(missing)
        } else null

    }

    protected fun findAddedVariables(sourceVars: List<String>,
                                     targetVars: List<String>): String? {
        // missing from source = added
        val added = listMissing(targetVars, sourceVars)
        return if (!added.isEmpty()) {
            messages.varsAdded(added)
        } else null

    }

    private fun listMissing(baseVars: List<String>,
                            testVars: List<String>): List<String> {
        val remainingVars = testVars.toMutableList()
        val unmatched = mutableListOf<String>()

        for (`var` in baseVars) {
            if (!remainingVars.remove(`var`)) {
                unmatched.add(`var`)
            }
        }
        return unmatched
    }

    protected fun findVars(inString: String): List<String> {
        val vars = mutableListOf<String>()
        // compile each time to reset index
//        val varRegExp = Regex(VAR_REGEX, GLOBAL_FLAG)
        val varRegExp = Regex(VAR_REGEX)
        var result = varRegExp.find(inString)
        while (result != null) {
            vars.add(result.groupValues[0])
            result = varRegExp.find(inString)
        }
        return vars
    }

    companion object {
        private val GLOBAL_FLAG = "g"

        // derived from translate toolkit printf style variable matching regex. See:
        // http://translate.svn.sourceforge.net/viewvc/translate/src/trunk/translate/filters/checks.py?revision=17978&view=markup
        private val VAR_REGEX = "%((?:\\d+\\$|\\(\\w+\\))?[+#-]*(\\d+)?(\\.\\d+)?(hh|h|ll|l|L|z|j|t)?[\\w%])"
    }
}
