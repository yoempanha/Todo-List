package com.example.todolist.base.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

abstract class BaseRecyclerView<T>(
    protected var items: ArrayList<T> = arrayListOf()
) : RecyclerView.Adapter<ViewHolder>() {

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return onCreateItemViewHolder(layoutInflater, parent, viewType)
    }

    override fun getItemCount(): Int = items.size

    final override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        onBindItemViewHolder(holder, position)
    }

    protected abstract fun onCreateItemViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder

    protected abstract fun onBindItemViewHolder(holder: ViewHolder, position: Int)

    protected var onItemClickHandler: ((position: Int, data: T) -> Unit)? = null

    open fun setItemClickHandler(action: (position: Int, data: T) -> Unit) {
        this.onItemClickHandler = action
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun set(data: List<T>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    open fun add(data: T) {
        val index = items.indexOf(data)
        if (index != -1) return
        val startIndex = this.items.size
        this.items.add(data)
        notifyItemRangeInserted(startIndex, 1)
    }

    open fun update(data: T, position: Int) {
        this.items[position] = data
        notifyItemChanged(position)
    }

    open fun update(data: T) {
        val position = items.indexOf(data)
        if (position == -1) return
        update(data, position)
    }

    open fun remove(position: Int) {
        this.items.removeAt(position)
        notifyItemRemoved(position)
    }

    open fun remove(item: T) {
        val position = items.indexOf(item)
        if (position == -1) return
        remove(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    open fun clear() {
        items = arrayListOf()
        notifyDataSetChanged()
    }
}