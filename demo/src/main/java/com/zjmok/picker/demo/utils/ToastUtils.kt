package com.zjmok.picker.demo.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.toColorInt
import com.zjmok.picker.demo.App
import kotlin.math.roundToInt

fun toast(any: Any?) {
    ToastUtils.showShort(any)
    Log.d("toast", any.toString())
}

inline val Int.dp2px: Float get() = this * Resources.getSystem().displayMetrics.density
inline val Int.dp2pxInt: Int get() = (this * Resources.getSystem().displayMetrics.density).roundToInt()

object ToastUtils {

    private var currentToast: Toast? = null

    fun showShort(any: Any?) {
        show(context = App.INSTANCE, any.toString(), Toast.LENGTH_SHORT)
    }

    fun showLong(any: Any?) {
        show(context = App.INSTANCE, any.toString(), Toast.LENGTH_LONG)
    }

    @SuppressLint("ToastUsage")
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        val appContext = context.applicationContext
        val showToast = {
            // 取消之前的 Toast
            currentToast?.cancel()
            // 自定义 view 的 toast 复用易出问题，直接使用新的对象
            // 创建新的 Toast
            // 默认的 Toast 会有 APP 名称前缀
//            currentToast = Toast.makeText(context, message, duration).apply {
//                setGravity(Gravity.TOP, 0, 200) // 顶部居中 向下偏移 200px
////                setGravity(Gravity.TOP or Gravity.END, 0, 0) // 右上角
//            }
            // 使用 自定义 View 的 Toast
            currentToast = Toast(appContext).apply {
                // 创建自定义布局
                val layout = LinearLayout(appContext).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16.dp2pxInt, 12.dp2pxInt, 16.dp2pxInt, 12.dp2pxInt)
                    background = GradientDrawable().apply {
                        setColor("#E6EEEEEE".toColorInt()) // 设置背景颜色
                        cornerRadius = 16.dp2px // 设置圆角
                    }
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                // 创建显示消息的 TextView
                val textView = TextView(appContext).apply {
                    text = message
                    setTextColor("#DE000000".toColorInt())
                    textSize = 14f
                }
                layout.addView(textView)

                // 设置自定义布局到 Toast
                this.view = layout // Android 11+ 不会在后台显示自定义 view 的 toast
                this.duration = duration
            }

            currentToast?.show()
        }

        if (Looper.myLooper() == Looper.getMainLooper()) {
            showToast()
        } else {
            Handler(Looper.getMainLooper()).post {
                showToast()
            }
        }
    }

    fun cancel() {
        val cancelToast = {
            // 如果有 Toast 正在显示，则取消
            currentToast?.cancel()
            currentToast = null
        }
        if (Looper.myLooper() == Looper.getMainLooper()) {
            cancelToast()
        } else {
            Handler(Looper.getMainLooper()).post {
                cancelToast()
            }
        }
    }

}
