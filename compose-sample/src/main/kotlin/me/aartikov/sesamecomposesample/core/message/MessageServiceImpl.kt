package me.aartikov.sesamecomposesample.core.message

import android.content.Context
import android.widget.Toast
import me.aartikov.sesame.localizedstring.LocalizedString

class MessageServiceImpl(
    private val context: Context
) : MessageService {

    override fun showMessage(message: LocalizedString) {
        Toast.makeText(context, message.resolve(context), Toast.LENGTH_SHORT).show()
    }
}