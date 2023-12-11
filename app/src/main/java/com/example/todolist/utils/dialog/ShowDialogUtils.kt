package com.example.todolist.utils.dialog

import android.content.Context
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object ShowDialogUtils {
    fun showDialog(
        context: Context,
        isCancelable: Boolean = true,
        title: String = "",
        description: String = "",
        negativeText: String = "Cancel",
        positiveText: String = "OK",
        negativeAction: (() -> Unit)? = null,
        positiveAction: (() -> Unit)? = null
    ) {
        val builder = MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setCancelable(isCancelable)
            .setMessage(description)
            .setNegativeButton(negativeText) { dialog, _ ->
                negativeAction?.invoke()
                dialog.dismiss()
            }
            .setPositiveButton(positiveText) { dialog, _ ->
                positiveAction?.invoke()
                dialog.dismiss()
            }
        builder.create().show()
    }
}