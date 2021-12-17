package com.jackl.agilebatchtask.config

import com.jackl.agilebatchtask.core.BatchEngine
import com.jackl.agilebatchtask.model.IRequestTask
import com.jackl.agilebatchtask.model.IResponseTask
import java.lang.RuntimeException
import java.util.concurrent.ExecutorService
import com.jackl.agilebatchtask.interfaces.FunctionTask1
import com.jackl.agilebatchtask.interfaces.FunctionTask2
import com.jackl.agilebatchtask.interfaces.BatchCallBack
import com.jackl.agilebatchtask.interfaces.CallBack



/**
 * @description:配置项
 * @author: jackl
 * @date: 2021/12/14
 */
class AgileTaskOptions {
    internal var batchEngine : BatchEngine? = null
    internal var myThreadNum : Int? = null
    internal var myTask : IRequestTask? = null
    internal var myTasks : List<out IRequestTask>? = null
    internal var myThreadPool : ExecutorService? = null
    internal var myBeforeTsak : FunctionTask1<in IRequestTask,out IRequestTask>? = null
    internal var myBeginTsak : FunctionTask1<in IRequestTask,out IResponseTask>? = null
    internal var myAfterTsak : FunctionTask1<in IResponseTask, out IResponseTask>? = null
    internal var myFilter : FunctionTask2<in IRequestTask>? = null
    internal var myBatchkCall : BatchCallBack<in IResponseTask>? = null
    internal var myCall : CallBack<in IResponseTask>? = null
    internal var myRetryCount : Int =0
    internal var myRetryDelayMillis : Long =0L

    /**
     * 添加任务
     * */
    fun addTask(IRequestTasks : IRequestTask) : AgileTaskOptions {
        this.myTask = IRequestTasks
        return this
    }

    /**
     * 添加任务组
     * */
    fun addTasks(IRequestTasks : List<out IRequestTask>) : AgileTaskOptions {
        this.myTasks = IRequestTasks
        return this
    }

    /**
     *配置线程个数，默认cpu核心数1/2
     */
    fun setThreadNum(tnb : Int): AgileTaskOptions {
        this.myThreadNum = tnb
        return this
    }

    /**
     * 自定义线程池
     * */
    fun setExecutorService(threadPool : ExecutorService): AgileTaskOptions {
        this.myThreadPool = threadPool
        return this
    }

    /**
     *同步执行任务前可以预执行的函数
     */
    fun doBeforeTsak(beforeTsak : FunctionTask1<in IRequestTask, out IRequestTask>) : AgileTaskOptions {
        this.myBeforeTsak = beforeTsak
        return this
    }

    /**
     * 同步执行任务时的真正函数
     * */
    fun doBeginTsak(beginTsak : FunctionTask1<in IRequestTask, out IResponseTask>) : AgileTaskOptions {
        this.myBeginTsak = beginTsak
        return this
    }

    /**
     * 同步执行任务后的真正函数
     * */
    fun doAfterTsak(afterTsak : FunctionTask1<in IResponseTask, out IResponseTask>) : AgileTaskOptions {
        this.myAfterTsak = afterTsak
        return this
    }

    /**
     * 过滤不需要的任务
     * */
    fun filter(filter : FunctionTask2<in IRequestTask>) : AgileTaskOptions {
        this.myFilter = filter
        return this
    }

    /**
     * 设置异常重试
     * @param retryCount 重试次数
     * @param retryDelayMillis 重试间隔
     * */
    fun retry(retryCount : Int,retryDelayMillis : Long): AgileTaskOptions {
        this.myRetryCount=retryCount
        this.myRetryDelayMillis=retryDelayMillis
        return this
    }

    /**
     * 设置监听回调
     * */
    fun setCallBack(call : CallBack<in IResponseTask>): AgileTaskOptions {
        this.myCall=call
        return this
    }

    /**
     * 设置批量监听回调
     * */
    fun setBatchCallBack(call : BatchCallBack<in IResponseTask>): AgileTaskOptions {
        this.myBatchkCall=call
        return this
    }

    fun execute() {
        if (myTasks == null && myTask == null) {
            throw RuntimeException("Tasks can not be null")
        }
        if (myBeginTsak == null) {
            throw RuntimeException("AroundTsak can not be null")
        }
        batchEngine= BatchEngine(this)
        batchEngine!!.execute()
    }

    fun cancel(){
        batchEngine?.cancel()
    }
}