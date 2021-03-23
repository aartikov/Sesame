package me.aartikov.sesame.input

data class KeyboardOptions(
    val capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    val autoCorrect: Boolean = true,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val imeAction: ImeAction = ImeAction.Default
) {

    companion object {
        val Default = KeyboardOptions()
    }
}

enum class KeyboardCapitalization {
    None,
    Characters,
    Words,
    Sentences
}

enum class KeyboardType {
    Ascii,
    Email,
    Number,
    NumberPassword,
    Password,
    Phone,
    Text,
    Uri
}

enum class ImeAction {
    Default,
    Done,
    Go,
    Next,
    None,
    Previous,
    Search,
    Send
}