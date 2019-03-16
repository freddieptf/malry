package com.freddieptf.malry.di

import android.content.Context
import dagger.Module
import dagger.Provides

/**
 * Created by freddieptf on 11/17/18.
 */
@Module
class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context = context

}