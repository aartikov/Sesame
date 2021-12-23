package me.aartikov.sesamecomposesample.di

import me.aartikov.sesamecomposesample.services.message.MessageServiceImpl
import me.aartikov.sesamecomposesample.services.message.MessageService
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object ServiceModule {

    fun create() = module {

        single<MessageService> {
            MessageServiceImpl(androidContext())
        }
    }
}