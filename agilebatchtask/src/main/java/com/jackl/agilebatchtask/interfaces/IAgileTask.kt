package com.jackl.agilebatchtask.interfaces

interface FunctionTask1<P1, out R> : Function<R> {
    operator fun invoke(p1: P1?): R?
}

interface FunctionTask2<in P1> {
    fun onFilt(p1: P1?): Boolean
}

interface CallBack<P1> {
    /**
     * 任务开始
     * */
    fun onBegin()

    /**
     * 任务结束
     * */
    fun onFinish(t: P1)

    /**
     * crash中断
     * */
    fun onError(e: Throwable)
}

interface BatchCallBack<P1> {
    /**
     * 任务开始
     * */
    fun onBegin()

    /**
     * 进度更新
     * */
    fun onProgress(progress: Int ,p1:P1)

    /**
     * 任务结束
     * */
    fun onFinish(ls: List<out P1>)

    /**
     * crash中断
     * */
    fun onError(e: Throwable)
}