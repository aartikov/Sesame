package me.aartikov.sesame.localizedstring

import android.widget.TextView

var TextView.localizedText: LocalizedString
    get() = LocalizedString.raw(text)
    set(value) {
        text = value.resolve(context)
    }
