package com.example.todolist.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<TViewBinding : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: TViewBinding

    private val progressDialog: AlertDialog by lazy {
        val dialog = AlertDialog.Builder(this).create()
        val progressBar = ProgressBar(this)
        dialog.let {
            it.setView(progressBar)
            it.window?.setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            it.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setCanceledOnTouchOutside(false)
            it.setCancelable(false)
        }
        dialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = getViewBinding(layoutInflater)
        setContentView(binding.root)
    }


    abstract fun getViewBinding(inflater: LayoutInflater): TViewBinding

    protected open fun showLoading(value: Boolean) {
        if (value) {
            progressDialog.show()
        } else {
            progressDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        showLoading(false)
    }
}
