package com.example.todolist.domain.base

abstract class BaseUseCase<Type, in Params> {

    abstract suspend fun execute(param: Params): BaseResult<Type>

    protected suspend fun <T> executeAsync(executor: suspend () -> T): BaseResult<T> {
        return try {
            BaseResult.Success(executor())
        } catch (e: Exception) {
            e.printStackTrace()
            BaseResult.Failure(e)
        }
    }
}