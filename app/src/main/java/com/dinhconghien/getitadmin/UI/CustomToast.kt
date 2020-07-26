package com.dinhconghien.getitadmin.UI

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.dinhconghien.getitadmin.R

class CustomToast(context: Context?) : Toast(context) {

    companion object{
        fun makeText(
            context: Context?,
            message: String?,
            duration: Int,
            type: Int
        ): Toast? {
            val toast = Toast(context)
            toast.duration = duration
            val layout: View =
                LayoutInflater.from(context).inflate(R.layout.my_custom_toast, null, false)
            val l1 = layout.findViewById<View>(R.id.tv_toastContent) as TextView
            val linearLayout = layout.findViewById<View>(R.id.toast_type) as LinearLayout
            val img =
                layout.findViewById<View>(R.id.imv_iconToast) as ImageView
            l1.text = message

            when (type) {
                1 -> {
                    linearLayout.setBackgroundResource(R.drawable.toast_success_shape)
                    img.setImageResource(R.drawable.icon_success)
                }
                2 -> {
                    linearLayout.setBackgroundResource(R.drawable.toast_warning_shape)
                    img.setImageResource(R.drawable.icon_warning)
                }
                3 -> {
                    linearLayout.setBackgroundResource(R.drawable.toast_error_shape)
                    img.setImageResource(R.drawable.icon_error)
                }
            }
            toast.view = layout
            return toast
        }
    }

}