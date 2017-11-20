package com.acv.airmad

import android.arch.lifecycle.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity


//LiveData
typealias Obs<T> = ((T) -> Unit)
typealias Obs2<T> = (Obs<T>) -> Unit

infix fun <M, T : M> LifecycleOwner.observe(f: () -> LiveData<T>): Obs2<T> =
        { o: (T) -> Unit -> f().observe(this, Observer { o(it!!) }) }

infix fun <T> Obs2<T>.`do`(f: Obs<T>) =
        this({ f(it) })

inline fun <reified T : ViewModel> Fragment.viewModelProviders(): T =
        ViewModelProviders.of(this).get(T::class.java)

inline fun <reified T : ViewModel> FragmentActivity.viewModelProviders(): T =
        ViewModelProviders.of(this).get(T::class.java)