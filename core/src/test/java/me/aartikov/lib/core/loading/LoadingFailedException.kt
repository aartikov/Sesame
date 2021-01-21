package me.aartikov.lib.core.loading

data class LoadingFailedException(override val message: String = "Loading failed") : Exception()