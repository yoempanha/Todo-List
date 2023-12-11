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
import com.example.todolist.utils.dialog.ShowDialogUtils
import com.example.todolist.utils.extension.hideKeyboard
import com.example.todolist.utils.extension.show
import com.example.todolist.utils.extension.showKeyboard
import com.example.todolist.utils.extension.textChanges
import com.example.todolist.utils.preference.AppPreferenceManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.Query
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseVMActivity<ActivityMainBinding, MainViewModel>(), TodoListener {

    @Inject
    private lateinit var query: Query

    private lateinit var todoListAdapter: TodoListAdapter

    override val viewModel: MainViewModel by viewModels()

    override fun getViewBinding(inflater: LayoutInflater): ActivityMainBinding {
        return ActivityMainBinding.inflate(inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
        setupListener()
    }

    override fun setupObserver() {
        super.setupObserver()
        viewModel.todoList.observe(this) { state ->
            if (state == null) return@observe
            when (state) {
                is DataState.Failure -> {
                    if (state.error is CancellationException) return@observe
                    Snackbar.make(
                        binding.root,
                        state.error.message.orEmpty(),
                        Snackbar.LENGTH_SHORT
                    ).show()
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
                viewModel.deleteTodoListContent(data = data, position = position)
            }
        )
    }

    override fun onUpdateStatus(data: TodoListContentModel, position: Int) {
        viewModel.upsertTodoListContent(data = data, position = position)
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private fun setupListener() {
        binding.apply {
            todoListEditText.setOnEditorActionListener { editText, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (editText.text.isNotBlank() &&
                        editText.text.toString().lowercase() != "empty"
                    ) {
                        val users = listOf(AppPreferenceManager.instance.deviceId)
                        val data = TodoListContentModel(
                            description = editText.text.toString(),
                            isCompleted = false,
                            timestamp = System.currentTimeMillis().toString(),
                            hashCode = editText.text.toString().hashCode(),
                            users = users
                        )
                        if (viewModel.isDuplicate(data.hashCode)) {
                            ShowDialogUtils.showDialog(
                                this@MainActivity,
                                title = "Duplication",
                                description = "You can not add the item"
                            )
                        } else {
                            viewModel.upsertTodoListContent(data, viewModel.currentPos)
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
                    if (item != null) {

                        return@onEach
                    }
                    viewModel.filter(text.toString())
                }.launchIn(lifecycleScope)

            updateButton.setOnClickListener {
                val data = viewModel.currentEditContent ?: return@setOnClickListener
                viewModel.upsertTodoListContent(
                    data.copy(
                        description = todoListEditText.text.toString(),
                        hashCode = todoListEditText.text.toString().hashCode(),
                        users = if (!data.users.contains(AppPreferenceManager.instance.deviceId)) {
                            data.users.plus(AppPreferenceManager.instance.deviceId)
                        } else data.users
                    ),
                    viewModel.currentPos
                )
                reset()
                hideKeyboard()
            }
        }
    }

    private fun setupRecyclerView() {
        binding.apply {
            todoListAdapter = TodoListAdapter(this@MainActivity)
            todoListAdapter.setOnItemClickHandler() { position, data ->
                viewModel.currentEditContent = data
                viewModel.currentPos = position
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
            todoListEditText.setText("")
            viewModel.currentEditContent = null
            viewModel.currentPos = -1
            updateButton.isEnabled = false
            todoListEditText.imeOptions = EditorInfo.IME_ACTION_DONE
        }
    }
}