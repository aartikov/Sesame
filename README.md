# Sesame
[![Release](https://jitpack.io/v/aartikov/Sesame.svg)](https://jitpack.io/#aartikov/Sesame) [![license](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Sesame is a set of architecture components for Android development. It is based on modern technologies including coroutines and Flow. Sesame is simple to learn and easy to use. It is ideally suited for MVVM and MVI architectures.

## Components
[property](https://github.com/aartikov/Sesame/tree/master/property) - provides observable properties and one-time commands.  
[dialog](https://github.com/aartikov/Sesame/tree/master/dialog) - allows to control dialogs from View Models.  
[navigation](https://github.com/aartikov/Sesame/tree/master/navigation) - gives an universal way to navigate between screens.  
[activable](https://github.com/aartikov/Sesame/tree/master/activable) - equips View Models with a very simple lifecycle.  
[loading](https://github.com/aartikov/Sesame/tree/master/loading) - helps to manage state for data loading (including paged one).  
[loop](https://github.com/aartikov/Sesame/tree/master/loop) - provides a simple MVI implementation.

Sesame components are independent. Use only that you like.

## Gradle Setup
Add jitpack.io repository in project level build.gradle:

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

Add the components you need in module level build.gradle:

```gradle
dependencies {
    implementation 'com.github.aartikov.Sesame:property:1.0.0-alpha1'
    implementation 'com.github.aartikov.Sesame:dialog:1.0.0-alpha1'
    implementation 'com.github.aartikov.Sesame:navigation:1.0.0-alpha1'
    implementation 'com.github.aartikov.Sesame:activable:1.0.0-alpha1'
    implementation 'com.github.aartikov.Sesame:loading:1.0.0-alpha1'
    implementation 'com.github.aartikov.Sesame:loop:1.0.0-alpha1'
}
```

## Sample
[The sample application](https://github.com/aartikov/Sesame/tree/master/sample) consists of several screens. Each screen demonstrates certain Sesame feature.

COUNTER - shows how to use properties and commands from [property](https://github.com/aartikov/Sesame/tree/master/property).  
PROFILE - loads ordinary data with [loading](https://github.com/aartikov/Sesame/tree/master/loading).  
DIALOGS - shows how to use [dialog](https://github.com/aartikov/Sesame/tree/master/dialog).  
MOVIES - loads paginated data with [loading](https://github.com/aartikov/Sesame/tree/master/loading).  
CLOCK - shows how to use [activable](https://github.com/aartikov/Sesame/tree/master/activable).  
The whole app - demonstrates [navigation](https://github.com/aartikov/Sesame/tree/master/navigation).  

There is no sample for [loop](https://github.com/aartikov/Sesame/tree/master/loop). See [LoadingLoop](https://github.com/aartikov/Sesame/blob/master/loading/src/main/kotlin/me/aartikov/sesame/loading/simple/internal/LoadingLoop.kt) and [PagedLoadingLoop](https://github.com/aartikov/Sesame/blob/master/loading/src/main/kotlin/me/aartikov/sesame/loading/paged/internal/PagedLoadingLoop.kt) as good examples how to use it.

## Contact the author
Artur Artikov <a href="mailto:a.artikov@gmail.com">a.artikov@gmail.com</a>

## License
```
The MIT License (MIT)

Copyright (c) 2021 Artur Artikov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```