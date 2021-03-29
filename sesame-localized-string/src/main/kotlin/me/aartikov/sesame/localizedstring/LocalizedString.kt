package me.aartikov.sesame.localizedstring

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

interface LocalizedString {

    fun resolve(context: Context): CharSequence

    companion object {

        fun empty() = EmptyString

        fun raw(value: CharSequence): LocalizedString = when {
            value.isEmpty() -> EmptyString
            else -> RawString(value)
        }

        fun resource(@StringRes resourceId: Int, vararg args: Any) = ResourceString(resourceId, args.toList())

        fun quantity(@PluralsRes resourceId: Int, quantity: Int, vararg args: Any) =
            QuantityResourceString(resourceId, quantity, args.toList())
    }
}

object EmptyString : LocalizedString {
    override fun resolve(context: Context): CharSequence {
        return ""
    }
}

data class RawString(val value: CharSequence) : LocalizedString {
    override fun resolve(context: Context): CharSequence {
        return value
    }
}

data class ResourceString(
    @StringRes val resourceId: Int,
    val args: List<Any>
) : LocalizedString {
    override fun resolve(context: Context): CharSequence {
        return context.getString(resourceId, *getArgValues(context, args))
    }
}

data class QuantityResourceString(
    @PluralsRes val resourceId: Int,
    val quantity: Int,
    val args: List<Any>
) : LocalizedString {
    override fun resolve(context: Context): CharSequence {
        return context.resources.getQuantityString(resourceId, quantity, *getArgValues(context, args))
    }
}

data class CompoundString(val parts: List<LocalizedString>) : LocalizedString {

    override fun resolve(context: Context): CharSequence {
        return parts.joinToString(separator = "") { it.resolve(context) }
    }
}

operator fun LocalizedString.plus(other: LocalizedString): LocalizedString {
    return when {
        this is CompoundString && other is CompoundString -> CompoundString(this.parts + other.parts)
        this is CompoundString -> CompoundString(this.parts + other)
        other is CompoundString -> CompoundString(listOf(this) + other.parts)
        else -> CompoundString(listOf(this, other))
    }
}

private fun getArgValues(context: Context, args: List<Any>): Array<Any> {
    return args.map {
        if (it is LocalizedString) {
            it.resolve(context)
        } else {
            it
        }
    }.toTypedArray()
}