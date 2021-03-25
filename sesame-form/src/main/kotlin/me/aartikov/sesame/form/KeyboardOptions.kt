package me.aartikov.sesame.form

data class KeyboardOptions(
    val capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
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
    VisiblePassword,
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