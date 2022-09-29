package com.github.serivesmejia.deltadrive.utils.task

fun interface Marker<T> {
    fun run(task: Task<T>)
}