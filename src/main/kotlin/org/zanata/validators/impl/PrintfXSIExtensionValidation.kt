package org.zanata.validators.impl

import org.zanata.validators.ValidationId
import org.zanata.validators.ValidationMessages

/**
 * @author Patrick Huang [pahuang@redhat.com](mailto:pahuang@redhat.com)
 */
class PrintfXSIExtensionValidation(id: ValidationId,
                                   messages: ValidationMessages) : PrintfVariablesValidation(id, messages.printfXSIExtensionValidationDesc(), messages) {

    override val sourceExample: String
        get() = "value must be between %x$1 and %y$2"

    override val targetExample: String
        get() = "value must be between %x$1 and <span class='js-example__target txt--warning'>%y$3</span>"

    override fun doValidate(source: String, target: String): List<String> {
        val errors = mutableListOf<String>()

        var sourceVars = findVars(source)
        val targetVars = findVars(target)

        if (hasPosition(targetVars)) {
            sourceVars = appendPosition(sourceVars)
            errors.addAll(checkPosition(targetVars, sourceVars.size))
        }

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

    private fun checkPosition(variables: List<String>, size: Int): List<String> {
        val errors = mutableListOf<String>()
        val posToVars = mutableMapOf<Int, MutableList<String>>()
        for (testVar in variables) {
            val result = POSITIONAL_REG_EXP
                    .find(testVar)
            if (result != null) {
                val positionAndDollar = result.groupValues[1]
                val position = extractPositionIndex(positionAndDollar)
                if (position in 0..(size - 1)) {
                    val list = posToVars.getOrPut(key = position, defaultValue = ::mutableListOf)
                    list.add(testVar)
                } else {
                    errors.add(messages.varPositionOutOfRange(testVar))
                }
            } else {
                errors.add(messages.mixVarFormats())
            }
        }
        if (posToVars.keys.size != variables.size) {
            // has some duplicate positions
            for (entry in posToVars.entries) {
                if (entry.value.size > 1) {
                    errors.add(messages.varPositionDuplicated(
                            entry.value))
                }
            }
        }

        return errors
    }

    companion object {
        // regex to find out whether the variable has position
        private val POSITIONAL_REG_EXP = Regex("%(\\d+\\$).+")

        private fun hasPosition(variables: List<String>): Boolean {
            for (testVar in variables) {
                val result = POSITIONAL_REG_EXP.find(testVar)
                if (result != null) {
                    return true
                }
            }
            return false
        }

        private fun appendPosition(sourceVars: List<String>): List<String> {
            val result = mutableListOf<String>()
            val regex = buildPosRegex(sourceVars.size)
            for (i in sourceVars.indices) {
                val sourceVar = sourceVars[i]
                if (sourceVar.matches(regex.toRegex())) {
                    result.add(sourceVar)
                } else {
                    val position = i + 1
                    val replacement = "%$position$"
                    result.add(sourceVar.replace("%", replacement))
                }
            }
            return result
        }

        private fun buildPosRegex(size: Int): String {
            val numeric = "[1-" + (size + 1) + "]"
            return ".*%$numeric+\\$.*"
        }

        private fun extractPositionIndex(positionAndDollar: String): Int {
            try {
                return (positionAndDollar.substring(0,
                        positionAndDollar.length - 1)).toInt() - 1
            } catch (e: Exception) {
                return -1
            }

        }
    }
}
