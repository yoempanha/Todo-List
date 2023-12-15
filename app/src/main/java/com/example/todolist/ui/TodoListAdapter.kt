package com.example.todolist.ui

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.todolist.R
import com.example.todolist.base.adapter.BaseRecyclerView
import com.example.todolist.base.adapter.BaseViewHolder
import com.example.todolist.databinding.ItemTodoListViewBinding
import com.example.todolist.domain.entity.TodoListContentModel

class TodoListAdapter(
    private val listener: TodoListener
) : BaseRecyclerView<TodoListContentModel>() {

    private var selectedItem: Int = -1

    fun setSelectedItem(position: Int) {
        selectedItem = position
        notifyItemChanged(position)
    }

    override fun onCreateItemViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return TodoListContentViewHolder(
            ItemTodoListViewBinding.inflate(
                layoutInflater,
                parent,
                false
            )
        )
    }

    override fun onBindItemViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TodoListContentViewHolder -> holder.onBind(items[position])
        }
    }

    private inner class TodoListContentViewHolder(
        private val binding: ItemTodoListViewBinding
    ) : BaseViewHolder<TodoListContentModel>(binding.root) {

        override fun onBind(data: TodoListContentModel) {
            binding.apply {
                title.text = data.description
                updateStatus(data.isCompleted)
                updateTitleColor(selectedItem == adapterPosition)
                deleteButton.setOnClickListener {
                    listener.onDelete(data = data, position = adapterPosition)
                }
                editButton.setOnClickListener {
                    onItemClickHandler?.invoke(adapterPosition, data)
                }
                markCompleteOrNotButton.setOnClickListener {
                    val isCompleted = data.isCompleted
                    listener.onUpdateStatus(
                        data = data.copy(isCompleted = !isCompleted),
                        adapterPosition
                    )
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

        fun updateTitleColor(isSelected: Boolean) {
            if (isSelected) {
                binding.title.setTextColor(
                    ContextCompat.getColor(
                        itemView.context,
                        R.color.background_color
                    )
                )
            } else {
                binding.title.setTextColor(ContextCompat.getColor(itemView.context, R.color.black))
            }
        }
    }
}

interface TodoListener {
    fun onDelete(data: TodoListContentModel, position: Int)
    fun onUpdateStatus(data: TodoListContentModel, position: Int)
}