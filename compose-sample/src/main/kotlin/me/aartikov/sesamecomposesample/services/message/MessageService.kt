package me.aartikov.sesamecomposesample.services.message

import me.aartikov.sesame.localizedstring.LocalizedString

interface MessageService {

    fun showMessage(message: LocalizedString)
}