
 # **AgileBatchTask** #
 
 [![License](https://img.shields.io/badge/License%20-Apache%202-337ab7.svg)](https://www.apache.org/licenses/LICENSE-2.0)
 [![](https://jitpack.io/v/JackL124/AgileBatchTask.svg)](https://www.jitpack.io/#JackL124/AgileBatchTask)
 [![MinSdk](https://img.shields.io/badge/%20MinSdk%20-%2019%2B%20-f0ad4e.svg)](https://android-arsenal.com/api?level=19)

## 介绍 ###

 这是一个异步多线程批量执行任务的框架，简单、灵活、功能强大
 * 支持任何异步任务处理，无任何耦合
 * 支持过滤任务，对不需要执行的任务直接丢弃
 * 支持对任务执行前插桩，自定义预处理
 * 支持对事件执行后插桩，对结果再处理
 * 支持自定义线程数
 * 支持自定义线程池
 * 支持异常后retry
 * 支持批量进度回显

## 演示

 ![task.gif](https://github.com/JackL124/AgileBatchTask/blob/main/screenshots/task.gif)
 ![task.gif](https://github.com/JackL124/AgileBatchTask/blob/main/screenshots/tasks.gif)
    
## 集成

* ##### 1.在项目根目录的build.gradle 中添加

```
repositories {
       google()
       jcenter()
       maven { url 'https://jitpack.io' }
   }
```

* ##### 2.在app build.gradle 中添加依赖
```
 dependencies {
    implementation 'com.github.JackL124:AgileBatchTask:1.0.0'
 	}
```

## 简单使用

```
/**
 * 执行单一任务
 * */
 AgileBatch().Builder()
            .addTask(MyRequsetModel())
            .doBeginTsak(object :FunctionTask1<IRequestTask,IResponseTask>{
                override fun invoke(p1: IRequestTask?): IResponseTask? {
                // TODO: do something 
                    return MyResponseModel()
                }
            })
            .setCallBack(object :CallBack<IResponseTask>{
                override fun onBegin() {}
                override fun onFinish(t: IResponseTask) {}
                override fun onError(e: Throwable) {}
            })
```

```
/**
 * 执行批量任务
 * */
AgileBatch().Builder()
            .addTasks(taskList)
            .doBeginTsak(object : FunctionTask1<IRequestTask, IResponseTask> {
                override fun invoke(p1: IRequestTask?): IResponseTask? {
                // TODO: do something 
                    return MyResponseModel()
                }
            })
            .setBatchCallBack(object : BatchCallBack<IResponseTask>{
                override fun onBegin() {}
                override fun onProgress(progress: Int, p1: IResponseTask) {}
                override fun onFinish(ls: List<IResponseTask>) {}
                override fun onError(e: Throwable) {}
            })

```

## 案例
可参考[example](https://github.com/JackL124/AgileBatchTask/tree/main/example/src/main/java/com/jackl/example)中的案例使用

## 进阶

* ### doBeforeTsak
此函数触发回调在doBeginTsak前，如需对任务进行预处理，可调用此函数(例如上传前对音视频进行转码、压缩等操作).<br/>
此函数执行在线程池分配的线程中，切勿直接更新ui操作.

* ### doBeginTsak
真正开始执行任务时回调，用户可自定义执行任意任务.<br/>
此函数执行在线程池分配的线程中，切勿直接更新ui操作.

* ### doAfterTsak
此函数触发回调在doBeginTsak后，用户可对结果进行自定义再处理.<br/>
此函数执行在线程池分配的线程中，切勿直接更新ui操作.

* ### filter
此函数触发回调在doBeforeTsak之前，用户可对队列中的任务选择性过滤丢弃.<br/>
此函数执行在线程池分配的线程中，切勿直接更新ui操作.

* ### retry
此函数可设置crash后重试次数及重试间隔时间，如果不设置默认不会重试.<br/>

* ### setExecutorService
此函数可替换内置线程池，用户可以根据业务类型，设置适合自身业务场景的线程池，默认线程池为FixedThreadPool<br/>

* ### setThreadNum
此函数修改默认线程池中corePool线程个数，用户可以根据业务类型，设置适合自身业务场景设置合理线程数，默认线程数为cpu核心数1/2<br/>

* ### cancel
调用此函数可终止正在执行中的任务以及在队列中等待中的任务<br/>

* ### execute
开始执行任务

* ### setCallBack
执行单一任务需调用此方法来接收结果集<br/>
onBegin() ：当开始执行任务前会回调此方法，此回调执行在调用execute()所在的线程，如无特殊写法，默认mainThread.
onFinish() : 任务执行完成后会回调此方法，此回调执行在调用execute()所在的线程，如无特殊写法，默认mainThread.
onError() : 任务执行发生crash或crash后尝试retry后依旧crash回调此方法，此回调执行在调用execute()所在的线程，如无特殊写法，默认mainThread.


* ### BatchCallBack
执行单一任务需调用此方法来接收结果集<br/>
onBegin() ：当开始执行任务前会回调此方法，此回调执行在调用execute()所在的线程，如无特殊写法，默认mainThread.
onProgress() : 线程池中某个任务执行完成后且doAfterTsak也执行完成会回调此方法当前进度及结果，此回调执行在调用execute()所在的线程，如无特殊写法，默认mainThread.
onFinish() : 任务全部执行完成后会回调此方法，此回调执行在调用execute()所在的线程，如无特殊写法，默认mainThread.
onError() : 任务执行发生crash或crash后尝试retry后依旧crash回调此方法，此回调执行在调用execute()所在的线程，如无特殊写法，默认mainThread.

## License
    Copyright 2021 jackl

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.


