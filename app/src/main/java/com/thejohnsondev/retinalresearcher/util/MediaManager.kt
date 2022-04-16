package com.thejohnsondev.retinalresearcher.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.BITMAP_COMPRESS_QUALITY
import com.thejohnsondev.retinalresearcher.util.Const.DefaultValues.SECOND_IN_MILLIS
import com.thejohnsondev.retinalresearcher.util.Const.MediaPath.DEFAULT_GALLERY_PATH
import com.thejohnsondev.retinalresearcher.util.Const.MediaPath.IMAGE_CONTENT_TYPE
import com.thejohnsondev.retinalresearcher.util.Const.MediaPath.IMAGE_EXTENSION
import java.io.File
import java.io.File.separator
import java.io.FileOutputStream
import java.io.OutputStream

class MediaManager {

    fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            val values = contentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, DEFAULT_GALLERY_PATH + folderName)
            values.put(MediaStore.Images.Media.IS_PENDING, true)

            val uri: Uri? =
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(it))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(it, values, null, null)
            }

        } else {
            val directory =
                File(Environment.getExternalStorageDirectory().toString() + separator + folderName)

            if (!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = System.currentTimeMillis().toString() + IMAGE_EXTENSION
            val file = File(directory, fileName)
            saveImageToStream(bitmap, FileOutputStream(file))
            val values = contentValues()
            values.put(MediaStore.Images.Media.DATA, file.absolutePath)
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        }
    }

    private fun contentValues(): ContentValues {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.MIME_TYPE, IMAGE_CONTENT_TYPE)
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / SECOND_IN_MILLIS);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values
    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, BITMAP_COMPRESS_QUALITY, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}