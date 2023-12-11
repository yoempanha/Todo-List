package com.example.todolist.base

import android.os.Bundle
import androidx.viewbinding.ViewBinding

abstract class BaseVMActivity<TViewBinding : ViewBinding, VM : BaseViewModel> :
    BaseActivity<TViewBinding>() {

    protected abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObserver()
    }

    protected open fun setupObserver() {
        viewModel.isLoading.observe(this) {
            if (it == null) return@observe
            showLoading(it)
        }
    }
}