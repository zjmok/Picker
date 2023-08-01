package com.zjmok.picker.demo

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.zjmok.picker.PickerLib
import com.zjmok.picker.PickerObserver
import com.zjmok.picker.PickerViewModel
import com.zjmok.picker.data.PickerResult
import com.zjmok.picker.demo.databinding.ActivityMainBinding
import com.zjmok.picker.demo.utils.toast
import kotlinx.coroutines.launch

@SuppressLint("SetTextI18n")
class MainActivity : FragmentActivity() {

    private val pickerViewModel: PickerViewModel by viewModels()
    private val pickerObserver by lazy { PickerObserver(this, pickerViewModel) }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)

        // 使用前初始化即可，可以在使用到的页面进行初始化
        PickerLib.init(this)

        observe()

        binding.btn.setOnClickListener {
            pickerObserver.takePicture()
        }
        binding.btn1.setOnClickListener {
            pickerObserver.pickPicture()
        }
        binding.btn2.setOnClickListener {
            pickerObserver.pickPicture2()
        }
        binding.btn3.setOnClickListener {
            pickerObserver.pickPicture3()
        }
        binding.btn4.setOnClickListener {

        }

    }

    private fun observe() {
        lifecycle.addObserver(pickerObserver)
        lifecycleScope.launch {
            pickerViewModel.pickerResult.collect { pickerResult ->
                pickerResult?.let {
                    when (pickerResult) {
                        is PickerResult.Success -> {
                            val imageInfo = pickerResult.imageInfo

                            val text = "action: ${pickerResult.pickerAction.alias}\n" +
                                    "uri:\n${imageInfo.uri}\n" +
                                    "path:\n${imageInfo.path}"

                            toast(text)

                            binding.tv.text = text
                            binding.iv.setImageURI(imageInfo.uri)
                        }

                        is PickerResult.Failure -> {
                            toast("${pickerResult.pickerAction.alias}: ${pickerResult.errorMessage}")
                        }

                        is PickerResult.Cancelled -> {
                            toast("${pickerResult.pickerAction.alias}: 取消操作")
                        }
                    }
                }

            }
        }
    }

}
