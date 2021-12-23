package me.aartikov.sesame.compose.form.control

fun interface TextTransformation {

    fun transform(text: String): String
}