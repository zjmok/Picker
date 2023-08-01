package com.zjmok.picker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.zjmok.picker.data.ImageInfo
import com.zjmok.picker.data.PickerAction
import com.zjmok.picker.data.PickerResult
import com.zjmok.picker.utils.toFile
import com.zjmok.picker.utils.toUri
import java.io.File
import kotlin.math.absoluteValue
import kotlin.random.Random

class PickerObserver : DefaultLifecycleObserver {

    private var context: Context
    private var viewModel: PickerViewModel

    private lateinit var currentAction: PickerAction

    private lateinit var takePictureUri: Uri

    private lateinit var takeLauncher: ActivityResultLauncher<Uri>
    private lateinit var pickLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickLauncher2: ActivityResultLauncher<String>
    private lateinit var pickLauncher3: ActivityResultLauncher<PickVisualMediaRequest>

    private val initTakeLauncher: (owner: ActivityResultCaller, viewModel: PickerViewModel) -> Unit = { owner, viewModel ->
        // 从 相机 获取图片
        takeLauncher = owner.registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) { success ->
            currentAction = PickerAction.TakePicture
            if (success) {
                val uri = takePictureUri
                val file = uri.toFile()
                if (file != null) {
                    val data = ImageInfo(uri, file.absolutePath)
                    viewModel.updatePickerResult(PickerResult.Success(currentAction, data))
                } else {
                    // 文件获取失败
                    viewModel.updatePickerResult(PickerResult.Failure(currentAction, "文件获取失败", uri))
                }
            } else {
                // 取消拍照
                viewModel.updatePickerResult(PickerResult.Cancelled(currentAction))
            }
        }
    }

    private val initPickLauncher: (owner: ActivityResultCaller, viewModel: PickerViewModel) -> Unit = { owner, viewModel ->
        // 从 图库 选择图片
        pickLauncher = owner.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { atyResult ->
            currentAction = PickerAction.PickPicture
            if (atyResult.resultCode == Activity.RESULT_OK) {
                val uri = atyResult.data?.data
                if (uri != null) {
                    val file = uri.toFile()
                    if (file != null) {
                        val data = ImageInfo(uri, file.absolutePath)
                        viewModel.updatePickerResult(PickerResult.Success(currentAction, data))
                    } else {
                        // 文件获取失败
                        viewModel.updatePickerResult(PickerResult.Failure(currentAction, "文件获取失败", uri))
                    }
                } else {
                    // uri 获取失败
                    viewModel.updatePickerResult(PickerResult.Failure(currentAction, "uri 获取失败"))
                }
            } else {
                // 取消选择
                viewModel.updatePickerResult(PickerResult.Cancelled(currentAction))
            }
        }
    }

    private val initPickLauncher2: (owner: ActivityResultCaller, viewModel: PickerViewModel) -> Unit = { owner, viewModel ->
        // 从 文档选择器 选择图片
        pickLauncher2 = owner.registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri ->
            currentAction = PickerAction.PickPicture2
            if (uri != null) {
                val file = uri.toFile()
                if (file != null) {
                    val data = ImageInfo(uri, file.absolutePath)
                    viewModel.updatePickerResult(PickerResult.Success(currentAction, data))
                } else {
                    // 若用户删除了媒体文件，但是选择器上可能会显示图片的缓存，实际上文件不存在，这里就会获取文件失败
                    // 文件获取失败
                    viewModel.updatePickerResult(PickerResult.Failure(currentAction, "文件获取失败", uri))
                }
            } else {
                // 取消选择
                viewModel.updatePickerResult(PickerResult.Cancelled(currentAction))
            }
        }
    }

    private val initPickLauncher3: (owner: ActivityResultCaller, viewModel: PickerViewModel) -> Unit = { owner, viewModel ->
        // 从 图片选择器 选择图片
        pickLauncher3 = owner.registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ) { uri ->
            // result
            currentAction = PickerAction.PickPicture3
            if (uri != null) {
                val file = uri.toFile()
                if (file != null) {
                    val data = ImageInfo(uri, file.absolutePath)
                    viewModel.updatePickerResult(PickerResult.Success(currentAction, data))
                } else {
                    // 若用户删除了媒体文件，但是选择器上可能会显示图片的缓存，实际上文件不存在，这里就会获取文件失败
                    // 文件获取失败
                    viewModel.updatePickerResult(PickerResult.Failure(currentAction, "文件获取失败", uri))
                }
            } else {
                // 取消选择
                viewModel.updatePickerResult(PickerResult.Cancelled(currentAction))
            }
        }
    }

    constructor(activity: ComponentActivity, viewModel: PickerViewModel) {
        context = activity
        this.viewModel = viewModel
    }

    constructor(fragment: Fragment, viewModel: PickerViewModel) {
        context = fragment.requireContext()
        this.viewModel = viewModel
    }

    override fun onCreate(owner: LifecycleOwner) {
        owner as ActivityResultCaller
        initTakeLauncher(owner, viewModel)
        initPickLauncher(owner, viewModel)
        initPickLauncher2(owner, viewModel)
        initPickLauncher3(owner, viewModel)
    }

    /**
     * contract = TakePicture
     *
     * 相机
     */
    fun takePicture() {
        PickerLib.checkInit()

        val file = File(context.externalCacheDir, "take_${Random.nextLong().absoluteValue}.jpeg")
        takePictureUri = file.toUri()

        takeLauncher.launch(takePictureUri)
    }

    /**
     * intent ACTION_PICK
     *
     * 图库。（实际会因设备而异）
     *
     * 兼容性最好
     */
    fun pickPicture() {
        PickerLib.checkInit()

        pickLauncher.launch(Intent(Intent.ACTION_PICK).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        })
    }

    /**
     * contract = GetContent
     *
     * 文档选择器。（实际会因设备而异）
     *
     * 可能有缓存问题，文件失效，体验不太好
     */
    fun pickPicture2() {
        PickerLib.checkInit()

        pickLauncher2.launch("image/*")
    }

    /**
     * contract = PickVisualMedia
     *
     * 图片选择器，新 API。（实际会因设备而异）
     *
     * 可能有缓存问题，文件失效，体验不太好
     */
    fun pickPicture3() {
        PickerLib.checkInit()

        pickLauncher3.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}
