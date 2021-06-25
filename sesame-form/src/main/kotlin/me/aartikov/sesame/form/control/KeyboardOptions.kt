package me.aartikov.sesame.form.control

/**
 * The keyboard configuration options for [InputControl].
 */
data class KeyboardOptions(

    /**
     * 	The keyboard type to be used in this input control.
     */
    val keyboardType: KeyboardType = KeyboardType.Text,

    /**
     * Informs the keyboard whether to automatically capitalize characters, words or sentences.
     * Applicable to only text based [KeyboardType]s such as [KeyboardType.Text], [KeyboardType.Ascii].
     */
    val capitalization: KeyboardCapitalization = KeyboardCapitalization.None,

    /**
     * The IME action. This IME action is honored by keyboard and may show specific icons on the keyboard.
     */
    val imeAction: ImeAction = ImeAction.Default

) {

    companion object {
        val Default = KeyboardOptions()
    }
}

/**
 * Options to request software keyboard to capitalize the text. Applies to languages which has upper-case and lower-case letters.
 */
enum class KeyboardCapitalization {

    /**
     * Do not auto-capitalize text.
     */
    None,

    /**
     * Capitalize all characters.
     */
    Characters,

    /**
     * Capitalize the first character of every word.
     */
    Words,

    /**
     * Capitalize the first character of each sentence.
     */
    Sentences
}

/**
 * Enums used for indicating keyboard types.
 */
enum class KeyboardType {

    /**
     * A keyboard type used to request an IME that is capable of inputting ASCII characters.
     */
    Ascii,

    /**
     * A keyboard type used to request an IME that is capable of inputting email addresses.
     */
    Email,

    /**
     * A keyboard type used to request an that is capable of inputting digits.
     */
    Number,

    /**
     * A keyboard type used to request an that is capable of inputting decimal number, allowing a decimal point.
     */
    DecimalNumber,

    /**
     * A keyboard type used to request an IME that is capable of inputting number password.
     */
    NumberPassword,

    /**
     * A keyboard type used to request an IME that is capable of inputting password.
     */
    Password,

    /**
     * A keyboard type used to request an IME that is capable of inputting text without auto-correction and auto-suggestion.
     */
    VisiblePassword,

    /**
     * A keyboard type used to request an IME that is capable of inputting phone numbers.
     */
    Phone,

    /**
     * A keyboard type used to request an IME that shows regular keyboard.
     */
    Text,

    /**
     * A keyboard type used to request an IME that is capable of inputting URIs.
     */
    Uri
}

/**
 * Signals the keyboard what type of action should be displayed.
 */
enum class ImeAction {

    /**
     * Use the platform and keyboard defaults and let the keyboard to decide the action.
     */
    Default,

    /**
     * Represents that the user is done providing input to a group of inputs.
     */
    Done,

    /**
     * Represents that the user would like to go to the target of the text in the input.
     */
    Go,

    /**
     * Represents that the user is done with the current input, and wants to move to the next one.
     */
    Next,

    /**
     * Represents that no action is expected from the keyboard.
     */
    None,

    /**
     * Represents that the user wants to return to the previous input.
     */
    Previous,

    /**
     * Represents that the user wants to execute a search
     */
    Search,

    /**
     * Represents that the user wants to send the text in the input
     */
    Send
}