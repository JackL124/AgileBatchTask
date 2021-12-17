package com.jackl.agilebatchtask

import com.jackl.agilebatchtask.config.AgileTaskOptions

/**
 * @description:
 * @author: jackl
 * @date: 2021/12/14
 */
open class AgileBatch {

    constructor()

    constructor(agileTaskOptions: AgileTaskOptions) {
        this.agileTaskOptions = agileTaskOptions
    }

    private var agileTaskOptions : AgileTaskOptions? = null

    fun Builder(): AgileTaskOptions {
        if(agileTaskOptions == null){
            agileTaskOptions=AgileTaskOptions()
        }
        return agileTaskOptions!!
    }

    fun execute(){
        agileTaskOptions?.execute()
    }

    fun cancel(){
        agileTaskOptions?.cancel()
    }


}