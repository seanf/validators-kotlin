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
class HtmlXmlTagValidation(id: ValidationId, messages: ValidationMessages) : AbstractValidationAction(id, messages.xmlHtmlValidatorDesc(), messages) {

    override val sourceExample: String
        get() = "&lt;p&gt;&lt;strong&gt;Hello world&lt;/strong&gt;&lt;/p&gt;"

    override val targetExample: String
        get() = "&lt;p&gt;&lt;strong&gt;Hello world<span class='js-example__target txt--warning'>&lt;/stong&gt;</span>&lt;/p&gt;"

    public override fun doValidate(source: String, target: String): List<String> {
        val errors = mutableListOf<String>()

        var foundErrors = listMissing(source, target)
        if (!foundErrors.isEmpty()) {
            errors.add(messages.tagsMissing(foundErrors))
        }
        foundErrors = listMissing(target, source)
        if (!foundErrors.isEmpty()) {
            errors.add(messages.tagsAdded(foundErrors))
        }

        if (errors.isEmpty()) {
            val sourceTags = getTagList(source)
            val targetTags = getTagList(target)

            errors.addAll(orderValidation(sourceTags, targetTags))
        }
        return errors
    }

    private fun orderValidation(srcTags: List<String>,
                                trgTags: List<String>): List<String> {
        val errors = mutableListOf<String>()

        var longestRun: List<String>? = null
        var currentRun: List<String>

        val src = srcTags.toTypedArray()
        val trg = trgTags.toTypedArray()

        for (i in src.indices) {
            val token = src[i]
            var srcIndex = i
            val trgIndex = trgTags.indexOf(token)

            if (trgIndex > -1) {
                currentRun = mutableListOf<String>()
                currentRun.add(token)

                var j = trgIndex + 1

                while (j < trg.size && srcIndex < src.size - 1) {
                    val nextIndexInSrc = findInTail(trg[j], src, srcIndex + 1)
                    if (nextIndexInSrc > -1) {
                        srcIndex = nextIndexInSrc
                        currentRun.add(src[srcIndex])
                    }
                    j++
                }

                if (currentRun.size == srcTags.size) {
                    // must all match
                    return errors
                }

                if (longestRun == null || longestRun.size < currentRun.size) {
                    longestRun = currentRun
                }
            }
        }

        if (longestRun != null && longestRun.size > 0) {
            val outOfOrder = mutableListOf<String>()

            for (aSrc in src) {
                if (!longestRun.contains(aSrc)) {
                    outOfOrder.add(aSrc)
                }
            }
            if (!outOfOrder.isEmpty()) {
                errors.add(messages.tagsWrongOrder(outOfOrder))
            }
        }

        return errors
    }

    private fun findInTail(toFind: String, findIn: Array<String>, startIndex: Int): Int {
        for (i in startIndex until findIn.size) {
            if (findIn[i] == toFind) {
                return i
            }
        }
        return -1
    }

    private fun getTagList(src: String): List<String> {
//        val regExp = RegExp.compile(tagRegex, "g")
        val regExp = Regex(tagRegex)

        val list = mutableListOf<String>()
        var result = regExp.find(src)
        while (result != null) {
            val node = result.groupValues[0]
            list.add(node)
            result = regExp.find(src)
        }
        return list
    }

    private fun listMissing(compareFrom: String, compareTo: String): List<String> {
//        val regExp = RegExp.compile(tagRegex, "g")
        val regExp = Regex(tagRegex)

        var tmp = compareTo
        val unmatched = mutableListOf<String>()
        var result = regExp.find(compareFrom)

        while (result != null) {
            val node = result.groupValues[0]
            if (!tmp.contains(node)) {
                unmatched.add(node)
            } else {
                val index = tmp.indexOf(node)
                val beforeNode = tmp.substring(0, index)
                val afterNode = tmp.substring(index + node.length)
                // remove matched node from
                tmp = beforeNode + afterNode
            }
            result = regExp.find(compareFrom)
        }
        return unmatched
    }

    companion object {

        private val tagRegex = "<[^>]+>"
    }
}
