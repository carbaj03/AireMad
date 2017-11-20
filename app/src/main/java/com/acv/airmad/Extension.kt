package com.acv.airmad

import android.app.Activity
import android.content.Context
import android.os.StrictMode
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager

infix fun ViewGroup.inflate(res: Int) =
        LayoutInflater.from(context).inflate(res, this, false)

fun Context.color(color: Int) =
        ContextCompat.getColor(this, color)

fun strictMode() =
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())

fun Context.inputMethodManager() =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager


fun AppCompatActivity.actionbar() = supportActionBar!!.apply {
    setDisplayShowTitleEnabled(true)
    setDisplayHomeAsUpEnabled(true)
    title = ""
}

infix fun View.focusChange(f: (view: View, b: Boolean) -> Unit) {
    onFocusChangeListener = View.OnFocusChangeListener(f)
}

infix fun View.click(f: () -> Unit) =
        setOnClickListener { f() }

fun Activity.getExtra(extra: String): String =
        intent?.getSerializableExtra(extra)?.let { it as String } ?: ""

