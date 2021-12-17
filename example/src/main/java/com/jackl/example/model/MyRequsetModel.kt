package com.jackl.example.model

import com.jackl.agilebatchtask.model.IRequestTask
import java.io.File

/**
 * @description:
 * @author: jackl
 * @date:  2021/12/14
 **/
data class MyRequsetModel(val index: Int,val path: String,var file:File?=null) : IRequestTask
