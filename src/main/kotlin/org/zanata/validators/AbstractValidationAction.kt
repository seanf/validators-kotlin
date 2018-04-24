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
package org.zanata.validators

/**
 *
 * @author Alex Eng [aeng@redhat.com](mailto:aeng@redhat.com)
 *
 * @see HtmlXmlTagValidation
 *
 * @see JavaVariablesValidation
 *
 * @see NewlineLeadTrailValidation
 *
 * @see PrintfVariablesValidation
 *
 * @see PrintfXSIExtensionValidation
 *
 * @see TabValidation
 *
 * @see XmlEntityValidation
 */
abstract class AbstractValidationAction(override val id: ValidationId, override val description: String,
                                        protected val messages: ValidationMessages) : ValidationAction {

    override val rules: ValidationDisplayRules

    private var exclusiveVals: List<ValidationAction> = listOf()
    override val exclusiveValidations: List<ValidationAction>
        get() = exclusiveVals

    override var state: ValidationAction.State = ValidationAction.State.Warning
        set(state) {
            field = state
            rules.updateRules(state)
        }

    init {
        this.rules = ValidationDisplayRules(this.state)
    }

    override fun validate(source: String?, target: String?): List<String> {
        return if (!target.isNullOrEmpty() && !source.isNullOrEmpty()) {
            doValidate(source!!, target!!)
        } else listOf()
    }

    protected abstract fun doValidate(source: String, target: String): List<String>

    override fun mutuallyExclusive(vararg exclusiveValidations: ValidationAction) {
        this.exclusiveVals = listOf(*exclusiveValidations)
    }
}
