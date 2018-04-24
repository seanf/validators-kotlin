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

private val tabs: (Char) -> Boolean = { it == '\t' }

class TabValidation(id: ValidationId, messages: ValidationMessages) : AbstractValidationAction(id, messages.tabValidatorDesc(), messages) {

    override val sourceExample: String
        get() = "\\t hello world"

    override val targetExample: String
        get() = "<span class='js-example__target txt--warning'>missing tab char (\\t)</span> hello world"

    public override fun doValidate(source: String, target: String): List<String> {
        val errors = mutableListOf<String>()

        val sourceTabs = source.count(tabs)
        val targetTabs = target.count(tabs)
        if (sourceTabs > targetTabs) {
            errors.add(messages.targetHasFewerTabs(sourceTabs, targetTabs))
        } else if (targetTabs > sourceTabs) {
            errors.add(messages.targetHasMoreTabs(sourceTabs, targetTabs))
        }

        return errors
    }
}
