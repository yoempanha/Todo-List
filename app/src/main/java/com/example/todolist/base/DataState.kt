package com.example.todolist.base

sealed class DataState<out T> {
    data class Success<T>(val data: T): DataState<T>()
    data class Failure<T>(val error: Throwable): DataState<T>()
}
