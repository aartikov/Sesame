# Localized string
Helps to deal with string resources.

## What problem does it solve?
When we need to pass a string from ViewModel to View it is always a dilemma. Should it be just a String? Or string resource id? Or string resource id with arguments? Or maybe plural resource id? There is no universal way to represent localized strings in Android. So, Sesame adds it.

## LocalizedString interface
Sesame provides an interface for localized strings:

```kotlin
interface LocalizedString {
    fun resolve(context: Context): CharSequence
}
```

In other words `LocalizedString` is something that can be resolved to a string (`CharSequence`). The `resolve` method receives Android Context, so it can return different strings for different locales.

## Create localized string
Sesame provides several implementations of `LocalizedString` and methods to create them.

`LocalizedString.empty()` - creates an empty string.

`LocalizedString.raw("Some string")` - creates a string with a "hardcoded" value. Use it only when a value is already localized (for example received from a backend).

`LocalizedString.resource(R.string.some_string, ...args)` - creates a localized string from string resource id with optional arguments.

`LocalizedString.quantity(R.plurals.some_string, count, ...args)` - creates a localized string from plural resource id with optional arguments.

`localizedString1 + localizedString2` - concatenates two `LocalizedString`s.

## Resolve localized string
Use `resolve` method:
```kotlin
val value = someLocalizedString.resolve(context)
```

Or `TextView` extension property:
```kotlin
textView.localizedText = someLocalizedString
```

## Best practice
Created in ViewModel `LocalizedString` should be resolved on a View side. Unlike ViewModel, View is recreated during configuration change. So when system language setting is changed all displayed strings will be resolved again with a new locale.