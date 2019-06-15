package com.freddieptf.malry.di

import com.freddieptf.malry.App
import com.freddieptf.malry.ui.detail.MangaDetailFragment
import com.freddieptf.malry.ui.library.LibraryFragment
import dagger.Component
import javax.inject.Singleton

/**
 * Created by freddieptf on 11/17/18.
 */
@Singleton
@Component(modules = [AppModule::class, DataProviderModule::class])
interface AppComponent {
    fun inject(fragment: LibraryFragment)
    fun inject(fragment: MangaDetailFragment)
    fun inject(app: App)
}