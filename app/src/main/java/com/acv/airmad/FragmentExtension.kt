package com.acv.airmad

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.collapsing_toolbar.*


inline fun <reified T : Fragment> AppCompatActivity.replace(vararg args: Pair<String, String>) =
        with(supportFragmentManager) {
            beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.container, createFragment<T>(*args), T::class.java.simpleName)
                    .addToBackStack(T::class.java.simpleName)
                    .commit()
        }


inline fun <reified T : Fragment> AppCompatActivity.load(c: Int = R.id.container, vararg args: Pair<String, String>) =
        with(supportFragmentManager) {
            beginTransaction()
                    .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
                    .replace(c, createFragment<T>(*args), T::class.java.simpleName)
                    .commit()
        }

inline fun <reified T : Fragment> createFragment(vararg args: Pair<String, String>): T =
        with(T::class.java.newInstance()) {
            val bundle = Bundle()
            args.map { bundle.putString(it.first, it.second) }
            arguments = bundle
            return this
        }

fun Fragment.linearLayoutManager() =
        LinearLayoutManager(context)

fun Fragment.configToolbar(newTitle: String) =
        with(activity as AppCompatActivity) {
            setSupportActionBar(toolbar)
            supportActionBar!!.apply {
                setDisplayShowTitleEnabled(true)
                setDisplayHomeAsUpEnabled(true)
                title = newTitle
            }
        }

