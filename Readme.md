# Picker

[![](https://jitpack.io/v/zjmok/Picker.svg)](https://jitpack.io/#zjmok/Picker)

## 依赖

settings.gradle.kts

```
dependencyResolutionManagement {
    repositories {
        maven("https://jitpack.io")
    }
}
```

app/build.gradle.kts

```
implementation("com.github.zjmok:picker:0.1.0")
```

## 代码

- `PickerLib.init(this)` 初始化

- `lifecycle.addObserver(pickerObserver)` 绑定生命周期

- `PickerObserver` 具体调用会因设备而已
    - `pickerObserver.takePicture()` 执行拍照获取图片
    - `pickerObserver.pickPicture()` 执行从 图库 获取图片 （兼容性好）
    - `pickerObserver.pickPicture2()` 执行从 文档选择器 选取图片 （可能有缓存问题，文件失效，体验不太好）
    - `pickerObserver.pickPicture4()` 执行从 图片选择器 选取图片 （新 API，但同样有失效问题）

- `pickerViewModel.pickerResult` 用于获取结果，获取 StateFlow，可以 collect 或转为 LiveData 进行 observe

```
    private val pickerViewModel: PickerViewModel by viewModels()
    private val pickerObserver by lazy { PickerObserver(this, pickerViewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContent
        // ...
        observe()
        initView()
    }

    private fun initView() {
        binding.btnTakePicture.onClick {
            // 拍照
            pickerObserver.takePicture()
        }
        binding.btnTakePicture.onClick {
            // 图库
            pickerObserver.pickPicture()
        }
    }

    private fun observe() {
        // 绑定生命周期
        lifecycle.addObserver(pickerObserver)

        // 获取结果
        pickerViewModel.pickerResult.asLiveData().observe(this) { pickerResult ->
            pickerResult?.let {
                when (pickerResult) {
                    is PickerResult.Success -> {
                        val imageInfo = pickerResult.imageInfo
                        toast("action: ${pickerResult.pickerAction}\nuri:\n${imageInfo.uri}\npath:\n${imageInfo.path}")
                    }

                    is PickerResult.Failure -> {
                        toast("${pickerResult.pickerAction}: ${pickerResult.errorMessage}")
                    }

                    is PickerResult.Cancelled -> {
                        toast("${pickerResult.pickerAction}: 取消操作")
                    }
                }
            }
        }
    }
```
