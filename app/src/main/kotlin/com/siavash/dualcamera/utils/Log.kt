package com.siavash.dualcamera.utils

import android.util.Log

/**
 * Created by sia on 12/5/15.
 */
fun Any.v(msg: () -> String) {
    if (Log.isLoggable(tag, Log.VERBOSE)) v(msg())
}

fun Any.d(msg: () -> String) {
    if (Log.isLoggable(tag, Log.DEBUG)) d(msg())
}

fun Any.i(msg: () -> String) {
    if (Log.isLoggable(tag, Log.INFO)) i(msg())
}

fun Any.e(msg: () -> String) {
    if (Log.isLoggable(tag, Log.ERROR)) e(msg())
}

fun Any.w(msg: () -> String) {
    w(msg())
}

fun Any.wtf(msg: () -> String) {
    wtf(msg())
}

fun Any.v(msg: String) {
    v(tag, msg)
}

fun Any.d(msg: String) {
    d(tag, msg)
}

fun Any.i(msg: String) {
    i(tag, msg)
}

fun Any.w(msg: String) {
    w(tag, msg)
}

fun Any.e(msg: String) {
    e(tag, msg)
}

fun Any.wtf(msg: String) {
    wtf(tag, msg)
}

fun Any.v(tag: String, msg: String) {
    Log.v(tag, msg)
}

fun Any.d(tag: String, msg: String) {
    Log.d(tag, msg)
}

fun Any.i(tag: String, msg: String) {
    Log.i(tag, msg)
}

fun Any.w(tag: String, msg: String) {
    Log.w(tag, msg)
}

fun Any.e(tag: String, msg: String) {
    Log.e(tag, msg)
}

fun Any.wtf(tag: String, msg: String) {
    Log.wtf(tag, msg)
}

private val Any.tag: String
    get() = javaClass.simpleName