package org.zanata.validators

//import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale
//import com.google.gwt.i18n.client.LocalizableResource.Generate
//import com.google.gwt.i18n.client.Messages

/**
 * @author David Mason, damason@redhat.com
 */
//@DefaultLocale
//@Generate(format = "com.google.gwt.i18n.rebind.format.PropertiesFormat")
interface ValidationMessages {

    fun ab(a: Int, b: Int): String

//    @DefaultMessage("Validation error - See validation message")
    fun notifyValidationError(): String

    // Newline validator
//    @DefaultMessage("Check for consistent leading and trailing newline (\\n).")
    fun newLineValidatorDesc(): String

//    @DefaultMessage("Leading newline (\\n) is missing")
    fun leadingNewlineMissing(): String

//    @DefaultMessage("Unexpected leading newline (\\n)")
    fun leadingNewlineAdded(): String

//    @DefaultMessage("Trailing newline (\\n) is missing")
    fun trailingNewlineMissing(): String

//    @DefaultMessage("Unexpected trailing newline (\\n)")
    fun trailingNewlineAdded(): String

    // Tab validator
//    @DefaultMessage("Check whether source and target have the same number of tabs.")
    fun tabValidatorDesc(): String

//    @DefaultMessage("Target has fewer tabs (\\t) than source (source: {0}, target: {1})")
    fun targetHasFewerTabs(sourceTabs: Int, targetTabs: Int): String

//    @DefaultMessage("Target has more tabs (\\t) than source (source: {0}, target: {1})")
    fun targetHasMoreTabs(sourceTabs: Int, targetTabs: Int): String

    // Printf variables validator
//    @DefaultMessage("Check that printf style (%x) variables are consistent.")
    fun printfVariablesValidatorDesc(): String

//    @DefaultMessage("Variable {0} position is out of range")
    fun varPositionOutOfRange(`var`: String): String

//    @DefaultMessage("Numbered arguments cannot mix with unnumbered arguments")
    fun mixVarFormats(): String

//    @DefaultMessage("Variables have same position: {0,collection,string}")
    fun varPositionDuplicated(vars: Collection<String>): String

    // Java variables validator
//    @DefaultMessage("Check that java style ('{x}') variables are consistent.")
    fun javaVariablesValidatorDesc(): String

//    @Description("Lists variables that appear a different number of times between source and target strings")
//    @DefaultMessage("Inconsistent count for variables: {0,list,string}")
//    @AlternateMessage("one", "Inconsistent count for variable: {0,list,string}")
    fun differentVarCount(/*@PluralCount*/ vars: List<String>): String

//    @DefaultMessage("Number of apostrophes ('') in source does not match number in translation. This may lead to other warnings.")
    fun differentApostropheCount(): String

//    @DefaultMessage("Quoted characters found in translation but not in source text. "
//            + "Apostrophe character ('') must be doubled ('''') to prevent quoting "
//            + "when it is used in Java MessageFormat strings.")
    fun quotedCharsAdded(): String

    // Shared variables validator messages
//    @DefaultMessage("Check that positional printf style (%n\$x) variables are consistent.")
    fun printfXSIExtensionValidationDesc(): String

//    @Description("Lists the variables that are in the original string but have not been included in the target")
//    @DefaultMessage("Missing variables: {0,list,string}")
//    @AlternateMessage("one", "Missing variable: {0,list,string}")
    fun varsMissing(/*@PluralCount*/ vars: List<String>): String

//    @Description("Lists the variables that are in the original string and are present but quoted in the target")
//    @DefaultMessage("Unexpected quoting of variables: {0,list,string}")
//    @AlternateMessage("one", "Unexpected quoting of variable: {0,list,string}")
    fun varsMissingQuoted(/*@PluralCount*/ vars: List<String>): String

//    @Description("Lists the variables that are in the target but are not in the original string")
//    @DefaultMessage("Unexpected variables: {0,list,string}")
//    @AlternateMessage("one", "Unexpected variable: {0,list,string}")
    fun varsAdded(/*@PluralCount*/ vars: List<String>): String

//    @Description("Lists the variables that are in the target and are present but quoted in the original string")
//    @DefaultMessage("Variables not quoted: {0,list,string}")
//    @AlternateMessage("one", "Variable not quoted: {0,list,string}")
    fun varsAddedQuoted(/*@PluralCount*/ vars: List<String>): String

    // XHM/HTML tag validator
//    @DefaultMessage("Check that XML/HTML tags are consistent.")
    fun xmlHtmlValidatorDesc(): String

//    @Description("Lists the xml or html tags that are in the target but are not in the original string")
//    @DefaultMessage("Unexpected tags: {0,list,string}")
//    @AlternateMessage("one", "Unexpected tag: {0,list,string}")
    fun tagsAdded(/*@PluralCount*/ tags: List<String>): String

//    @Description("Lists the xml or html tags that are in the original string but have not been included in the target")
//    @DefaultMessage("Missing tags: {0,list,string}")
//    @AlternateMessage("one", "Missing tag: {0,list,string}")
    fun tagsMissing(/*@PluralCount*/ tags: List<String>): String

//    @DefaultMessage("Tags in unexpected position: {0,list,string}")
//    @AlternateMessage("one", "Tag in unexpected position: {0,list,string}")
    fun tagsWrongOrder(/*@PluralCount*/ tags: List<String>): String

    // XML Entity validator
//    @DefaultMessage("Check that XML entity are complete.")
    fun xmlEntityValidatorDesc(): String

//    @DefaultMessage("Invalid XML entity [ {0} ]")
    fun invalidXMLEntity(entity: String): String
}
