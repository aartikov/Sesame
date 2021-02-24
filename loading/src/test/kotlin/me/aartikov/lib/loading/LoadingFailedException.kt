package me.aartikov.lib.loading

data class LoadingFailedException(override val message: String = "Loading failed") : Exception()