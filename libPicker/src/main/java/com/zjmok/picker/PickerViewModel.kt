package com.zjmok.picker

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.zjmok.picker.data.PickerResult

class PickerViewModel : ViewModel() {

    private val _pickerResult = MutableStateFlow<PickerResult?>(null)
    val pickerResult: StateFlow<PickerResult?> = _pickerResult

    internal fun updatePickerResult(result: PickerResult) {
        _pickerResult.value = result
    }

}