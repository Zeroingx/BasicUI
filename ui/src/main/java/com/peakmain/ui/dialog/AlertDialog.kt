package com.peakmain.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import com.peakmain.ui.R

/**
 * author peakmain
 * createdata：2019/7/9
 * mail:2726449200@qq.com
 * desiption:Dialog to encapsulate
 */
class AlertDialog : Dialog {
    private var mAlert: AlertController

    private constructor(@NonNull context: Context) : super(context) {
        mAlert = AlertController(this, window!!)
    }

    private constructor(@NonNull context: Context, themeResId: Int) : super(
            context,
            themeResId
    ) {
        mAlert = AlertController(this, window!!)
    }

    /**
     * reduce findViewById times
     */
    fun <T : View> getView(viewId: Int): T? {
        return mAlert.getView(viewId)
    }

    fun setText(viewId: Int, text: CharSequence?) {
        mAlert.setText(viewId, text)
    }

    fun setOnClickListener(viewId: Int, listener: View.OnClickListener?) {
        mAlert.setOnClickListener(viewId, listener)
    }

    fun setVisibility(isShow: Boolean, id: Int) {
        getView<View>(id)?.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    fun setVisibility(isShow: Boolean, vararg ids: Int) {
        ids.forEach { id ->
            setVisibility(isShow, id)
        }
    }

    class Builder @JvmOverloads constructor(
            context: Context?,
            themeResId: Int = R.style.dialog
    ) {
        private val P: AlertController.AlertParams
        private var mDialog: AlertDialog? = null
        fun create(): AlertDialog {
            val dialog =
                    AlertDialog(
                            P.mContext,
                            P.themeResId
                    )
            P.apply(dialog.mAlert)
            dialog.setCancelable(P.mCancelable)
            if (P.mCancelable) {
                //Click outside to cancel
                dialog.setCanceledOnTouchOutside(true)
            }
            dialog.setOnCancelListener(P.mOnCancelListener)
            dialog.setOnDismissListener(P.mOnDismissListener)
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener)
            }
            mDialog = dialog
            return dialog
        }


        /**
         * set view
         */
        fun setContentView(layoutId: Int): Builder {
            P.mView = null
            P.mViewLayoutResId = layoutId
            return this
        }

        fun setContentView(view: View?): Builder {
            P.mView = view
            P.mViewLayoutResId = 0
            return this
        }

        /**
         * save text in the custom View
         */
        fun setText(
                layoutId: Int,
                text: CharSequence?
        ): Builder {
            P.mTextArray.put(layoutId, text)
            return this
        }

        /**
         * save button click events in the custom View
         */
        fun setOnClickListener(
                layoutId: Int,
                listener: View.OnClickListener?
        ): Builder {
            P.mClickArray.put(layoutId, listener)
            return this
        }

        /**
         * save button click events in the custom View and return dialog for cancel
         */
        fun addOnClickListener(
                layoutId: Int,
                listener: (dialog: AlertDialog?) -> Unit
        ): Builder {
            P.mClickArray.put(layoutId, View.OnClickListener { listener(mDialog) })
            return this
        }

        /**
         * set width and height
         */
        fun setWidthAndHeight(
                width: Int,
                height: Int
        ): Builder {
            P.mWidth = width
            P.mHeigth = height
            return this
        }

        /**
         * Settings pop up from the bottom
         */
        fun fromBottom(isAnimation: Boolean): Builder {
            if (isAnimation) {
                P.mAnimation = R.style.dialog_from_bottom_anim
            }
            P.mGravity = Gravity.BOTTOM
            return this
        }

        fun setFullWidth(): Builder {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT
            P.mHeigth = ViewGroup.LayoutParams.WRAP_CONTENT
            return this
        }

        /**
         * set defalut animation
         */
        fun addDefaultAnimation(): Builder {
            P.mAnimation = R.style.dialog_scale_anim
            return this
        }

        /**
         * set animation
         */
        fun setAnimation(styleAnimation: Int): Builder {
            P.mAnimation = styleAnimation
            return this
        }

        /**
         * set cancelListener
         */
        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?): Builder {
            P.mOnCancelListener = onCancelListener
            return this
        }

        /**
         * set dismissListener
         */
        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?): Builder {
            P.mOnDismissListener = onDismissListener
            return this
        }

        /**
         * set cancelable
         */
        fun setCancelable(flag: Boolean): Builder {
            P.mCancelable = flag
            return this
        }

        /**
         * show dialog
         */
        fun show(): AlertDialog {
            if (mDialog == null) {
                create()
            }
            mDialog!!.show()
            return mDialog!!
        }

        init {
            P = AlertController.AlertParams(
                    context!!,
                    themeResId
            )
        }
    }
}