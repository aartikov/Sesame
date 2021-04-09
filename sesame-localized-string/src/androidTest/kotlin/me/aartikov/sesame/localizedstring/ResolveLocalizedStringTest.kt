package me.aartikov.sesame.localizedstring

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import me.aartikov.sesame.localizedstring.test.R
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ResolveLocalizedStringTest {

    @Test
    fun empty_string() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val string = LocalizedString.empty()

        val actual = string.resolve(context)

        assertEquals("", actual)
    }

    @Test
    fun raw_string() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val string = LocalizedString.raw("Sample")

        val actual = string.resolve(context)

        assertEquals("Sample", actual)
    }

    @Test
    fun resource_string() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val string = LocalizedString.resource(R.string.sample)

        val actual = string.resolve(context)

        assertEquals("Sample", actual)
    }

    @Test
    fun resource_string_with_args() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val string = LocalizedString.resource(R.string.sample_with_args, 1, "two")

        val actual = string.resolve(context)

        assertEquals("Sample 1 two", actual)
    }

    @Test
    fun resource_string_with_localized_args() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val arg = LocalizedString.resource(R.string.two)
        val string = LocalizedString.resource(R.string.sample_with_args, 1, arg)

        val actual = string.resolve(context)

        assertEquals("Sample 1 two", actual)
    }

    @Test
    fun quantity_string() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val string = LocalizedString.quantity(R.plurals.cat_quantity, 2, 2)

        val actual = string.resolve(context)

        assertEquals("2 cats", actual)
    }

    @Test
    fun quantity_string_with_localized_arg() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val arg = LocalizedString.resource(R.string.two)
        val string = LocalizedString.quantity(R.plurals.cat_quantity, 2, arg)

        val actual = string.resolve(context)

        assertEquals("two cats", actual)
    }

    @Test
    fun string_concatenation() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val string1 = LocalizedString.resource(R.string.sample)
        val string2 = LocalizedString.raw(" ")
        val string3 = LocalizedString.quantity(R.plurals.cat_quantity, 2, 2)
        val string = string1 + string2 + string3

        val actual = string.resolve(context)

        assertEquals("Sample 2 cats", actual)
    }
}