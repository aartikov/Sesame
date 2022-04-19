package me.aartikov.sesamecomposesample.core.message

import me.aartikov.sesame.localizedstring.LocalizedString

interface MessageService {

    fun showMessage(message: LocalizedString)
}