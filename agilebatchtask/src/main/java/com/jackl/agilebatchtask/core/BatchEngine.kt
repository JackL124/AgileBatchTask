package com.jackl.agilebatchtask.core

import com.jackl.agilebatchtask.model.IRequestTask
import com.jackl.agilebatchtask.model.IResponseTask
import com.jackl.agilebatchtask.config.AgileTaskOptions
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


/**
 * @description:
 * @author: jackl
 * @date:  2021/12/14
 **/
internal class BatchEngine(agileTaskOptions: AgileTaskOptions) {
    private val myOptions: AgileTaskOptions = agileTaskOptions
    private var threadNum = myOptions.myThreadNum ?: Runtime.getRuntime().availableProcessors() / 2
    private lateinit var executor: ExecutorService
    private lateinit var myTasks: List<IRequestTask>
    private var totalSize = 0f
    private var index = 0f
    private lateinit var result: IResponseTask
    private var results = arrayListOf<IResponseTask>()
    private var disposable: Disposable? = null


    fun execute() {
        val observable: Observable<IResponseTask>
        if (myOptions.myTask == null) {
            executor = myOptions.myThreadPool ?: Executors.newFixedThreadPool(threadNum)
            myTasks = myOptions.myTasks!!
            totalSize = myTasks!!.size.toFloat()
            observable = creatBatchObservable()
        } else {
            observable = creatObservable()
        }
        observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(ObserverImpl())
    }

    fun cancel() {
        disposable?.let {
            if (!it.isDisposed)
                it.dispose()
        }
    }

    private fun creatObservable(): Observable<IResponseTask> {
        return Observable
            .just(myOptions.myTask)
            .filter {
                if (myOptions.myFilter?.onFilt(it) == true) {
                    totalSize--
                    return@filter false
                } else {
                    return@filter true
                }
            }
            .map {
                myOptions.myBeforeTsak?.invoke(it) ?: it
            }
            .map {
                myOptions.myBeginTsak!!.invoke(it)
            }
            .map {
                myOptions.myAfterTsak?.invoke(it) ?: it
            }
            .retryWhen(RetryImpl())
            .subscribeOn(Schedulers.io())
    }

    private fun creatBatchObservable(): Observable<IResponseTask> {
        return Observable
            .fromIterable(myTasks)
            .flatMap {
                Observable.just(it)
                    .filter {
                        if (myOptions.myFilter?.onFilt(it) == true) {
                            totalSize--
                            return@filter false
                        } else {
                            return@filter true
                        }
                    }
                    .map {
                        myOptions.myBeforeTsak?.invoke(it) ?: it
                    }
                    .map {
                        myOptions.myBeginTsak!!.invoke(it)
                    }
                    .map {
                        myOptions.myAfterTsak?.invoke(it) ?: it
                    }
                    .retryWhen(RetryImpl())
                    .subscribeOn(Schedulers.from(executor))
            }
            .doFinally { executor.shutdownNow() }
    }

    inner class ObserverImpl : Observer<IResponseTask> {
        override fun onSubscribe(d: Disposable) {
            this@BatchEngine.disposable = d
            if (myOptions.myTask == null)
                myOptions.myBatchkCall?.onBegin()
            else
                myOptions.myCall?.onBegin()
        }

        override fun onNext(t: IResponseTask) {
            if (myOptions.myTask == null) {
                index++
                val progress = (index / totalSize) * 100
                myOptions.myBatchkCall?.onProgress(progress.toInt(), t)
                results.add(t)
            } else {
                result = t
            }
        }

        override fun onError(e: Throwable) {
            if (myOptions.myTask == null)
                myOptions.myBatchkCall?.onError(e)
            else
                myOptions.myCall?.onError(e)
        }

        override fun onComplete() {
            if (myOptions.myTask == null)
                myOptions.myBatchkCall?.onFinish(results)
            else
                myOptions.myCall?.onFinish(result)
        }
    }

    inner class RetryImpl : Function<Observable<out Throwable>, Observable<*>> {
        val retryCount = myOptions.myRetryCount
        val retryDelayMillis = myOptions.myRetryDelayMillis
        private var currentRetryCount = 0
        override fun apply(t: Observable<out Throwable>): Observable<*> {
            return t.flatMap(object : Function<Throwable, Observable<*>> {
                override fun apply(t: Throwable): Observable<*> {
                    if (++currentRetryCount <= retryCount) {
                        return Observable.timer(retryDelayMillis, TimeUnit.MILLISECONDS)
                    }
                    return Observable.error<Throwable>(t)
                }
            })
        }
    }
}