package com.example.viewmodeldemo.utils

data class Resource<out T>(val status: StatusClass, val data: T?, val message: String?) {

    companion object {

        fun <T> success(data: T?): Resource<T> {
            return Resource(StatusClass.SUCCESS, data, null)
        }
        fun <T> error(msg: String, data: T?): Resource<T> {
            return Resource(StatusClass.ERROR, data, msg)
        }
        fun <T> loading(data: T?): Resource<T> {
            return Resource(StatusClass.LOADING, data, null)
        }

    }
}