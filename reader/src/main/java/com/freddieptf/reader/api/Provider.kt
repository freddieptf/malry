package com.freddieptf.reader.api

import androidx.annotation.Nullable

/**
 * Created by freddieptf on 9/22/18.
 */
abstract class Provider {

    abstract fun hasNextRead(): Boolean

    abstract fun getNextRead(): Chapter?

    abstract fun getCurrentRead(): Chapter

    abstract fun hasPreviousRead(): Boolean

    abstract fun getPreviousRead(): Chapter?

}