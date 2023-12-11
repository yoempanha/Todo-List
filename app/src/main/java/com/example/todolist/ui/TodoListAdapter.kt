package com.example.todolist.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.todolist.R
import com.example.todolist.base.adapter.BaseViewHolder
import com.example.todolist.databinding.ItemTodoListViewBinding
import com.example.todolist.domain.entity.TodoListContentModel
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class TodoListAdapter(
    private val options: FirestoreRecyclerOptions<TodoListContentModel>
) : FirestoreRecyclerAdapter<TodoListContentModel, ViewHolder>(
    options
) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return TodoListContentViewHolder(
            ItemTodoListViewBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: TodoListContentModel) {
        when (holder) {
            is TodoListContentViewHolder -> holder.onBind()
        }
    }


    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        val payload = payloads.firstOrNull()
        if (payload != null && holder is TodoListContentViewHolder) {
            when (payload) {
                is Boolean -> holder.updateStatus(payload)
            }
            return
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    private inner class TodoListContentViewHolder(
        private val binding: ItemTodoListViewBinding
    ) : BaseViewHolder<TodoListContentModel>(binding.root) {

        override fun onBind(data: TodoListContentModel) {
            binding.apply {
                title.text = data.description
                updateStatus(data.isCompleted)
                deleteButton.setOnClickListener {
                    listener.onDelete(data = data, position = adapterPosition)
                }
                editButton.setOnClickListener {
                    onItemClickHandler?.invoke(adapterPosition, data)
                }
                markCompleteOrNotButton.setOnClickListener {
                    data.isCompleted = !data.isCompleted
                    notifyItemChanged(adapterPosition, data.isCompleted)
                    listener.onUpdateStatus(data = data, adapterPosition)
                }
            }
        }

        fun updateStatus(isCompleted: Boolean) {
            binding.apply {
                markCompleteOrNotButton.text = itemView.context.getString(
                    if (isCompleted) {
                        R.string.incompleted
                    } else {
                        R.string.completed
                    }
                )
                editButton.isEnabled = !isCompleted
                if (isCompleted) {
                    title.paintFlags = title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    title.paintFlags = title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }
            }
        }
    }
}

interface TodoListener {
    fun onDelete(data: TodoListContentModel, position: Int)
    fun onUpdateStatus(data: TodoListContentModel, position: Int)
}