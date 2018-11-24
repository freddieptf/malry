package com.freddieptf.malry.di

import com.freddieptf.malry.detail.ChapterListFragment
import com.freddieptf.malry.library.LibraryFragment
import dagger.Component
import dagger.Subcomponent

/**
 * Created by freddieptf on 11/23/18.
 */
@LibLocationScope
@Subcomponent(modules = [DataProviderModule::class])
interface DataProviderComponent {

    fun inject(fragment: LibraryFragment)

    fun inject(fragment: ChapterListFragment)
}