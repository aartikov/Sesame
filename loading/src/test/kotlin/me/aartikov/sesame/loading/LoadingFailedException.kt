package me.aartikov.sesame.loading

data class LoadingFailedException(override val message: String = "Loading failed") : Exception()