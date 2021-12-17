package com.jackl.example.utils

import android.provider.DocumentsContract
import android.os.Environment
import android.content.ContentUris
import android.provider.MediaStore
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import java.io.File
import java.lang.IllegalArgumentException

/**
 * @description:
 * @author: jackl
 * @date: 2021/12/14
 */
object FileUtils {
    fun getPathFromUri(context: Context, uri: Uri): String? {
        var path: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            val docId = DocumentsContract.getDocumentId(uri)
            val splits = docId.split(":").toTypedArray()
            var type: String? = null
            var id: String? = null
            if (splits.size == 2) {
                type = splits[0]
                id = splits[1]
            }
            when (uri.authority) {
                "com.android.externalstorage.documents" -> if ("primary" == type) {
                    path =
                        Environment.getExternalStorageDirectory().toString() + File.separator + id
                }
                "com.android.providers.downloads.documents" -> path = if ("raw" == type) {
                    id
                } else {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        java.lang.Long.valueOf(docId)
                    )
                    getMediaPathFromUri(context, contentUri, null, null)
                }
                "com.android.providers.media.documents" -> {
                    var externalUri: Uri? = null
                    when (type) {
                        "image" -> externalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        "video" -> externalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                        "audio" -> externalUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                        "document" -> externalUri = MediaStore.Files.getContentUri("external")
                    }
                    if (externalUri != null) {
                        val selection = "_id=?"
                        val selectionArgs = arrayOf(id)
                        path = getMediaPathFromUri(context, externalUri, selection, selectionArgs)
                    }
                }
            }
        } else if (ContentResolver.SCHEME_CONTENT.equals(uri.scheme, ignoreCase = true)) {
            path = getMediaPathFromUri(context, uri, null, null)
        } else if (ContentResolver.SCHEME_FILE.equals(uri.scheme, ignoreCase = true)) {
            //如果是file类型的Uri(uri.fromFile)，直接获取图片路径即可
            path = uri.path
        }
        //确保如果返回路径，则路径合法
        return if (path == null) null else if (File(path).exists()) path else null
    }

    private fun getMediaPathFromUri(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String?>?
    ): String? {
        var path: String?
        val authroity = uri.authority
        path = uri.path
        val sdPath = Environment.getExternalStorageDirectory().absolutePath
        if (!path!!.startsWith(sdPath)) {
            val sepIndex = path.indexOf(File.separator, 1)
            path = if (sepIndex == -1) null else {
                sdPath + path.substring(sepIndex)
            }
        }
        if (path == null || !File(path).exists()) {
            val resolver = context.contentResolver
            val projection = arrayOf(MediaStore.MediaColumns.DATA)
            val cursor = resolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    try {
                        val index = cursor.getColumnIndexOrThrow(projection[0])
                        if (index != -1) path = cursor.getString(index)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        path = null
                    } finally {
                        cursor.close()
                    }
                }
            }
        }
        return path
    }
}