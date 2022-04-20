package me.aartikov.sesamecomposesample.core

import me.aartikov.sesamecomposesample.core.message.MessageService
import me.aartikov.sesamecomposesample.core.message.MessageServiceImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single<MessageService> {
        MessageServiceImpl(androidContext())
    }
}