package com.zjmok.picker.data

import android.net.Uri

sealed class PickerResult {

    data class Success(
        val pickerAction: PickerAction,
        val imageInfo: ImageInfo,
    ) : PickerResult()

    data class Failure(
        val pickerAction: PickerAction,
        val errorMessage: String,
        // 异常 uri
        val errorUri: Uri? = null,
    ) : PickerResult()

    data class Cancelled(
        val pickerAction: PickerAction,
    ) : PickerResult()

}