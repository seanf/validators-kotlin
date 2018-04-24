package org.zanata.validators

/**
 *
 * @author Alex Eng [aeng@redhat.com](mailto:aeng@redhat.com)
 */
enum class ValidationId private constructor(
        val displayName: String) {

    HTML_XML("HTML/XML tags"),
    NEW_LINE("Leading/trailing newline (\\n)"),
    TAB("Tab characters (\\t)"),
    JAVA_VARIABLES("Java variables"),
    XML_ENTITY("XML entity reference"),
    PRINTF_VARIABLES("Printf variables"),
    PRINTF_XSI_EXTENSION("Positional printf (XSI extension)")
}
