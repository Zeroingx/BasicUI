package com.peakmain.ui.image.adapter

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.peakmain.ui.R
import com.peakmain.ui.image.PictureSelectorActivity
import com.peakmain.ui.image.`interface`.UpdateSelectListener
import com.peakmain.ui.image.config.PictureConfig
import com.peakmain.ui.image.config.PictureMimeType
import com.peakmain.ui.image.entry.SelectImageFileEntity
import com.peakmain.ui.image.entry.FileInfo
import com.peakmain.ui.recyclerview.adapter.CommonRecyclerAdapter
import com.peakmain.ui.recyclerview.adapter.ViewHolder
import com.peakmain.ui.image.fragment.FileListFragment
import com.peakmain.ui.utils.FileTypeUtil.getFileIconResource
import com.peakmain.ui.utils.FileUtils.FormetFileSize
import com.peakmain.ui.utils.ToastUtils

/**
 * author ：Peakmain
 * createTime：2021/3/26
 * mail:2726449200@qq.com
 * describe：
 */
class FileListAdapter(
        context: Context?,
        private val mSelectFileList: ArrayList<SelectImageFileEntity>,
        private val maxCount: Int,
        data: List<FileInfo?>
) : CommonRecyclerAdapter<FileInfo?>(
        context,
        data,
        R.layout.ui_item_file_list_adapter
) {
    override fun convert(
            holder: ViewHolder,
            item: FileInfo?
    ) {
        holder.setText(R.id.tv_file_name, item?.fileName)
        //判断如果是文件夹就显示文件数量，是文件则显示文件大小
        if (item!!.isDirectory) {
            holder.setVisibility(View.GONE, R.id.ui_file_select)
            holder.setText(
                    R.id.tv_file_detail,
                    String.format(
                            mContext?.getString(R.string.ui_files_numbers)!!,
                            item.fileSize
                    )
            )
        } else {
            val mFileSelect = holder.getView<ImageView>(R.id.ui_file_select)
            mFileSelect?.visibility = View.VISIBLE
            var tempData = SelectImageFileEntity(PictureConfig.FILE, item.filePath)
            if (!TextUtils.isEmpty(item.filePath) && PictureMimeType.isImage(item.filePath!!)) {
                tempData = SelectImageFileEntity(PictureConfig.IMAGE, item.filePath)
            }
            mFileSelect?.isSelected = mSelectFileList.contains(tempData)
            holder.setText(
                    R.id.tv_file_detail,
                    String.format(
                            mContext?.getString(R.string.ui_format_file_size)!!,
                            FormetFileSize(item.fileSize)
                    )
            )
        }
        holder.setImageResource(
                R.id.iv_file_icon,
                getFileIconResource(mContext!!, item)
        )
        holder.setOnItemClickListener(View.OnClickListener {
            if (item.isDirectory) {
                val bundle = Bundle()
                bundle.putString(PictureSelectorActivity.directory, item.filePath)
                bundle.putSerializable(PictureSelectorActivity.SELECT_RESULT_KEY, mSelectFileList)
                (mContext as PictureSelectorActivity).showFragment(bundle)
            } else {

                //判断文件大小是否大于100M  1M=1024x1024B   因为1M=1024K 1K=1024B(字节)
                if (item.fileSize > FileListFragment.MAX_FILESIZE * 1024 * 1024) {
                    ToastUtils.showLong("无法发送大于100M的文件")
                } else {
                    var tempData = SelectImageFileEntity(PictureConfig.FILE, item.filePath)
                    if (PictureMimeType.isImage(item.filePath!!)) {
                        //是图片
                        tempData = SelectImageFileEntity(PictureConfig.IMAGE, item.filePath)
                    }

                    if (mSelectFileList.contains(tempData)) {
                        mSelectFileList.remove(tempData)
                    } else {
                        //判断是否达到最大
                        if (maxCount == mSelectFileList.size) {
                            ToastUtils.showLong(
                                    String.format(
                                            mContext?.getString(R.string.ui_picture_message_max_num)!!,
                                            maxCount
                                    )
                            )
                            return@OnClickListener
                        }
                        if (PictureMimeType.isImage(item.filePath!!)) {
                            mSelectFileList.add(
                                    SelectImageFileEntity(
                                            PictureConfig.IMAGE,
                                            item.filePath!!
                                    )
                            )
                        } else {
                            if (!PictureMimeType.isPdf(item.fileName!!)) {
                                ToastUtils.showShort("暂不支持其他格式")
                            } else {
                                mSelectFileList.add(
                                        SelectImageFileEntity(
                                                PictureConfig.FILE,
                                                item.filePath!!
                                        )
                                )
                            }

                        }


                    }
                    mListener?.selector()
                    (mContext as PictureSelectorActivity).updateSelectDataResult(mSelectFileList)
                    notifyItemChanged(holder.adapterPosition)
                }
            }
        })
    }

    private var mListener: UpdateSelectListener? = null
    fun setOnUpdateSelectListener(listener: UpdateSelectListener?) {
        mListener = listener
    }

}