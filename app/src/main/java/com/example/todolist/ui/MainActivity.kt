package com.example.todolist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolist.base.BaseVMActivity
import com.example.todolist.base.DataState
import com.example.todolist.databinding.ActivityMainBinding
import com.example.todolist.domain.entity.TodoListContentModel
import com.example.todolist.domain.mapper.toContentModel
import com.example.todolist.utils.dialog.ShowDialogUtils
import com.example.todolist.utils.extension.hideKeyboard
import com.example.todolist.utils.extension.show
import com.example.todolist.utils.extension.showKeyboard
import com.example.todolist.utils.extension.textChanges
import com.example.todolist.utils.preference.AppPreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class MainActivity : BaseVMActivity<ActivityMainBinding, MainViewModel>(), TodoListener {

    private lateinit var listenerRegistration: ListenerRegistration
    private lateinit var todoListAdapter: TodoListAdapter

    override val viewModel: MainViewModel by viewModels()

    override fun getViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
        setupListener()
        viewModel.setViewLoading(true)
    }

    override fun onStart() {
        super.onStart()
        listenerRegistration = Firebase.firestore.collection("todo_list_content")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    showError(error)
                    return@addSnapshotListener
                }

                snapshots?.documentChanges?.forEach {
                    val data = it.document.toContentModel()
                    when (it.type) {
                        DocumentChange.Type.ADDED -> todoListAdapter.add(data)

                        DocumentChange.Type.MODIFIED -> todoListAdapter.update(data)

                        DocumentChange.Type.REMOVED -> todoListAdapter.remove(data)
                    }
                }
                viewModel.setViewLoading(false)
            }
    }

    override fun onStop() {
        super.onStop()
        listenerRegistration.remove()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.todoList.observe(this) { state ->
            if (state == null) return@observe
            when (state) {
                is DataState.Failure -> {
                    showError(state.error)
                }

                is DataState.Success -> initView()
            }
        }
    }

    override fun onDelete(data: TodoListContentModel, position: Int) {
        ShowDialogUtils.showDialog(
            this,
            title = "Delete",
            description = "Do you want to delete this item?",
            positiveAction = {
                viewModel.deleteTodoListContent(data = data)
            }
        )
    }

    override fun onUpdateStatus(data: TodoListContentModel, position: Int) {
        viewModel.upsertTodoListContent(data = data)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setupListener() {
        binding.apply {
            todoListEditText.setOnEditorActionListener { editText, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val message = editText.text.toString().trim()
                    if (message.isNotBlank() && message.lowercase() != "empty") {
                        val users = listOf(AppPreferenceManager.instance.deviceId)
                        val data = TodoListContentModel(
                            description = message,
                            isCompleted = false,
                            timestamp = System.currentTimeMillis().toString(),
                            itemHashCode = message.hashCode(),
                            users = users
                        )
                        if (viewModel.isDuplicate(data.itemHashCode)) {
                            ShowDialogUtils.showDialog(
                                this@MainActivity,
                                title = "Duplication",
                                description = "You can not add the item"
                            )
                        } else {
                            viewModel.upsertTodoListContent(data)
                            reset()
                            hideKeyboard()
                        }
                    }
                    return@setOnEditorActionListener true
                }
                return@setOnEditorActionListener false
            }

            todoListEditText.textChanges()
                .debounce(300L)
                .drop(1)
                .onEach { text ->
                    val item = viewModel.currentEditContent
                    if (item != null) return@onEach
                    viewModel.filter(text.toString())
                }.launchIn(lifecycleScope)

            updateButton.setOnClickListener {
                val message = todoListEditText.text.toString()
                if (message.isEmpty() || message.lowercase() == "empty") return@setOnClickListener
                val data = viewModel.currentEditContent ?: return@setOnClickListener
                viewModel.upsertTodoListContent(
                    data.copy(
                        description = message,
                        itemHashCode = message.hashCode(),
                        users = if (!data.users.contains(AppPreferenceManager.instance.deviceId)) {
                            data.users.plus(AppPreferenceManager.instance.deviceId)
                        } else data.users
                    )
                )
                hideKeyboard()
                reset()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            todoListAdapter = TodoListAdapter(this@MainActivity)
            todoListAdapter.setItemClickHandler { _, data ->
                viewModel.currentEditContent = data
                updateButton.isEnabled = true
                todoListEditText.imeOptions = EditorInfo.IME_ACTION_NONE
                todoListEditText.setText(data.description)
                todoListEditText.setSelection(todoListEditText.text?.length ?: 0)
                todoListEditText.requestFocus()
                showKeyboard()
            }
            recyclerView.adapter = todoListAdapter
            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    private fun initView() {
        binding.apply {
            noResultTextView.show(visible = viewModel.isEmpty)
            todoListAdapter.set(viewModel.todoListContents)
        }
    }

    private fun reset() {
        binding.apply {
            todoListEditText.imeOptions = EditorInfo.IME_ACTION_DONE
            todoListEditText.setText("")
            viewModel.currentEditContent = null
            updateButton.isEnabled = false
        }
    }

    private fun showError(error: Throwable) {
        if (error is CancellationException) return
        Snackbar.make(
            binding.root,
            error.message.orEmpty(),
            Snackbar.LENGTH_SHORT
        ).show()
    }
}