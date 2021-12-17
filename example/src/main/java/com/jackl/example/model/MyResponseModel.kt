package com.jackl.example.model

import com.jackl.agilebatchtask.model.IResponseTask

/**
 * @description:
 * @author: jackl
 * @date:  2021/12/14
 **/

data class MyResponseModel(
    val code: Int,
    val data: Data,
    val message: String,
    val status: String
): IResponseTask

data class Data(
    val avatar: String,
    var index : Int
)

