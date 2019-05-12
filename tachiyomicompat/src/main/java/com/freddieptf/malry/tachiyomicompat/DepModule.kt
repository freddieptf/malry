package com.freddieptf.malry.tachiyomicompat

import android.app.Application
import eu.kanade.tachiyomi.network.NetworkHelper
import uy.kohesive.injekt.api.InjektModule
import uy.kohesive.injekt.api.InjektRegistrar
import uy.kohesive.injekt.api.addSingleton
import uy.kohesive.injekt.api.addSingletonFactory

internal class DepModule(val app: Application) : InjektModule {

    override fun InjektRegistrar.registerInjectables() {
        addSingleton(app)
        addSingletonFactory { NetworkHelper(app) }
    }

}