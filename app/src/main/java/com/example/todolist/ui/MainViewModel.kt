package com.example.todolist.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.todolist.base.BaseViewModel
import com.example.todolist.base.DataState
import com.example.todolist.domain.base.BaseResult
import com.example.todolist.domain.entity.TodoListContentModel
import com.example.todolist.domain.usecase.DeleteTodoListContentUseCase
import com.example.todolist.domain.usecase.GetTodoListUseCase
import com.example.todolist.domain.usecase.UpsertTodoListContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getTodoListUseCase: GetTodoListUseCase,
    private val deleteTodoListContentUseCase: DeleteTodoListContentUseCase,
    private val upsertTodoListContentUseCase: UpsertTodoListContentUseCase
) : BaseViewModel() {
    private val _todoList: MutableLiveData<DataState<Unit>> = MutableLiveData()
    val todoList: LiveData<DataState<Unit>> = _todoList

    private var _todoListContents: MutableList<TodoListContentModel> = mutableListOf()
    val todoListContents: List<TodoListContentModel> get() = _todoListContents.distinctBy { it.hashCode }

    val isEmpty: Boolean get() = todoListContents.isEmpty()

    var currentPos: Int = -1
    var currentEditContent: TodoListContentModel? = null

    private var job: Job? = null

    init {
        getTodoList()
    }

    private fun getTodoList(data: String = "", isNeedLoading: Boolean = true) {
        job?.cancel()
        job = viewModelScope.launch {
            setLoading(isNeedLoading)
            when (val result = getTodoListUseCase.execute(data)) {
                is BaseResult.Success -> {
                    _todoListContents = result.data.toMutableList()
                    _todoList.value = DataState.Success(Unit)
                }

                is BaseResult.Failure -> {
                    _todoList.value = DataState.Failure(result.error)
                }
            }
            setLoading(false)
        }
    }

    fun deleteTodoListContent(data: TodoListContentModel, position: Int) {
        viewModelScope.launch {
            setLoading(true)
            when (val result = deleteTodoListContentUseCase.execute(data)) {
                is BaseResult.Success -> {
                    if (_todoListContents.isNotEmpty()) {
                        _todoListContents.removeAt(position)
                    }
                    _todoList.value = DataState.Success(Unit)
                }

                is BaseResult.Failure -> {
                    _todoList.value = DataState.Failure(result.error)
                }
            }
            setLoading(false)
        }
    }

    fun upsertTodoListContent(data: TodoListContentModel, position: Int) {
        viewModelScope.launch {
            setLoading(true)
            when (val result = upsertTodoListContentUseCase.execute(data)) {
                is BaseResult.Success -> {
                    if (_todoListContents.size - 1 >= position && position != -1) {
                        _todoListContents[position] = result.data
                    } else {
                        _todoListContents.add(result.data)
                    }
                    _todoList.value = DataState.Success(Unit)
                }

                is BaseResult.Failure -> {
                    _todoList.value = DataState.Failure(result.error)
                }
            }
            setLoading(false)
        }
    }

    fun filter(data: String = "") {
        getTodoList(data = data, isNeedLoading = false)
    }

    fun isDuplicate(dataHasCode: Int): Boolean {
        return todoListContents.any { it.hashCode == dataHasCode }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        job = null
    }
}