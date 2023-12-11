package com.example.todolist.utils.extension

import android.view.View

fun View.show(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.hide() {
    visibility = View.GONE
}