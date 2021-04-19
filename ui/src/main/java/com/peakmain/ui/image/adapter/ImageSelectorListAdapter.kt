package com.peakmain.ui.image.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.peakmain.ui.R
import com.peakmain.ui.constants.BasicUIUtils
import com.peakmain.ui.recyclerview.adapter.CommonRecyclerAdapter
import com.peakmain.ui.recyclerview.adapter.ViewHolder
import com.peakmain.ui.image.`interface`.UpdateSelectListener
import com.peakmain.ui.image.config.PictureConfig
import com.peakmain.ui.image.entry.SelectImageFileEntity
import com.peakmain.ui.image.entry.ImageEntity
import com.peakmain.ui.imageLoader.ImageLoader
import com.peakmain.ui.utils.FileUtils.createTmpFile
import com.peakmain.ui.utils.ToastUtils
import java.io.IOException
import kotlin.collections.ArrayList

/**
 * author:Peakmain
 * createTime:2021/3/25
 * mail:2726449200@qq.com
 * describe：
 */
class ImageSelectorListAdapter(
        mContext: Context,
        private var mSelectImages: ArrayList<SelectImageFileEntity>,
        private val mMaxCount: Int,
        private val mMode: Int
) : CommonRecyclerAdapter<ImageEntity?>(
        mContext,
        ArrayList<ImageEntity>(),
        R.layout.ui_media_chooser_item
) {

    override fun convert(
            holder: ViewHolder,
            item: ImageEntity?
    ) {
        if (item == null) {
            holder.setVisibility(
                    View.INVISIBLE,
                    R.id.image,
                    R.id.mask,
                    R.id.media_selected_indicator
            )
            holder.setVisibility(View.VISIBLE, R.id.camera_tv)
            holder.setOnItemClickListener(View.OnClickListener { v: View? -> openCamera() })
        } else {
            holder.setVisibility(
                    View.VISIBLE,
                    R.id.image,
                    R.id.media_selected_indicator
            )
            holder.setVisibility(
                    View.INVISIBLE,
                    R.id.camera_tv
            )
            // 显示图片
            val imageView =
                    holder.getView<ImageView>(R.id.image)
            ImageLoader.instance!!.displayImage(mContext!!, item.path, imageView)
            val selectedIndicatorIv =
                    holder.getView<ImageView>(R.id.media_selected_indicator)
            for (mSelectImage in mSelectImages) {
                selectedIndicatorIv!!.isSelected =
                        item.path == mSelectImage.path && mSelectImage.type.equals(
                                PictureConfig.IMAGE
                        )
            }
            val selectImageFileEntity =
                    SelectImageFileEntity(
                            PictureConfig.IMAGE,
                            item.path
                    )
            selectedIndicatorIv!!.isSelected = mSelectImages.contains(selectImageFileEntity)

            val selected = selectedIndicatorIv.isSelected
            if (selected)
                holder.setVisibility(View.VISIBLE, R.id.mask)
            else
                holder.setVisibility(View.INVISIBLE, R.id.mask)
            holder.setOnItemClickListener(R.id.media_selected_indicator, View.OnClickListener {
                if (mSelectImages.contains(selectImageFileEntity)) {
                    mSelectImages.remove(selectImageFileEntity)
                    item.isSelect = false
                } else {
                    // 判断是否到达最大
                    if (mMaxCount == mSelectImages.size) {
                        Toast.makeText(
                                BasicUIUtils.application,
                                String.format(
                                        mContext!!.getString(R.string.ui_picture_message_max_num)!!,
                                        mMaxCount
                                ),
                                Toast.LENGTH_SHORT
                        ).show()
                        return@OnClickListener
                    }
                    item.isSelect = true
                    mSelectImages.add(
                            SelectImageFileEntity(
                                    PictureConfig.IMAGE,
                                    item.path
                            )
                    )
                }
                mListener?.selector()
                notifyDataSetChanged()
            })
            holder.setOnItemClickListener(R.id.image, View.OnClickListener {
                mOnPicturePreview?.onPicturePreviewClick(holder.adapterPosition, mSelectImages)
            })
        }
    }

    private var mOnPicturePreview: PicturePreviewClick? = null
    fun setOnPicturePreviewClick(mOnPicturePreview: PicturePreviewClick) {
        this.mOnPicturePreview = mOnPicturePreview
    }

    interface PicturePreviewClick {
        fun onPicturePreviewClick(
                position: Int,
                selectImages: ArrayList<SelectImageFileEntity>
        )
    }

    /**
     * 打开相机拍照
     */
    private fun openCamera() {
        try {
            val tmpFile =
                    createTmpFile(mContext!!)
            if (mListener != null) {
                mListener!!.openCamera(tmpFile)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            ToastUtils.showLong("相机打开失败")
        }
    }

    /**
     * 设置数据
     */
    fun setData(
            images: MutableList<ImageEntity?>,
            showCamera: Boolean
    ) {
        val dataList = ArrayList<ImageEntity?>()
        if (showCamera) {
            dataList.add(null)
        }
        dataList.addAll(images)
        setData(dataList)
        notifyDataSetChanged()
    }

    fun setSelectResult(selectImages: ArrayList<SelectImageFileEntity>?) {
        if (selectImages != null) {
            this.mSelectImages = selectImages
            notifyDataSetChanged()
        }
    }

    private var mListener: UpdateSelectListener? = null
    fun setOnUpdateSelectListener(listener: UpdateSelectListener?) {
        mListener = listener
    }


    companion object {
        const val REQUEST_CAMERA = 0x0045
    }

}