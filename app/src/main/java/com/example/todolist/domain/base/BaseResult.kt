package com.example.todolist.domain.base

sealed class BaseResult<out T> {
    data class Success<T>(val data: T): BaseResult<T>()
    data class Failure(val error: Throwable): BaseResult<Nothing>()
}
