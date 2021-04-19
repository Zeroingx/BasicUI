package com.peakmain.ui.image.fragment

import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.constraint.Group
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.peakmain.ui.R
import com.peakmain.ui.image.PictureSelectorActivity
import com.peakmain.ui.image.`interface`.UpdateSelectListener
import com.peakmain.ui.image.adapter.FileListAdapter
import com.peakmain.ui.image.config.PictureSelectionConfig
import com.peakmain.ui.image.entry.SelectImageFileEntity
import com.peakmain.ui.image.entry.FileInfo
import com.peakmain.ui.utils.FileUtils
import com.peakmain.ui.utils.LoadFileTask
import java.io.File
import kotlin.collections.ArrayList

/**
 * author ：Peakmain
 * createTime：2021/3/26
 * mail:2726449200@qq.com
 * describe：文件列表的Fragment
 */
internal class FileListFragment : Fragment(), LoadFileTask.FileCallback, UpdateSelectListener {
    private lateinit var mRvFileList: RecyclerView
    private var mFileListAdapter: FileListAdapter? = null
    private var mFileInfoList: List<FileInfo>? = null
    private lateinit var mResultList: ArrayList<SelectImageFileEntity>
    private lateinit var mGroupEmppty: Group
    private lateinit var mConfig: PictureSelectionConfig
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.ui_fragment_file_list, container, false)
        initView(view)
        getFileListData(bundle = arguments)
        return view
    }

    companion object {
        const val MAX_FILESIZE = 100 //文件最大体积
    }

    private fun initView(view: View) {
        mRvFileList = view.findViewById(R.id.rv_list_file)
        mResultList = ArrayList()
        mGroupEmppty = view.findViewById(R.id.group_empty)
        mConfig = PictureSelectionConfig.newInstance
    }

    override fun onGetFileList(mFileInfoList: List<FileInfo>?) {
        this.mFileInfoList = mFileInfoList
        mFileListAdapter = FileListAdapter(activity, mResultList, mConfig.maxSelectNumber, mFileInfoList!!)
        if (mFileInfoList.isEmpty()) {
            mGroupEmppty.visibility = View.VISIBLE
        } else {
            mGroupEmppty.visibility = View.INVISIBLE
        }
        mRvFileList.adapter = mFileListAdapter
        mRvFileList.layoutManager = LinearLayoutManager(activity)
        mFileListAdapter!!.setOnUpdateSelectListener(this)
    }

    override fun showLoading(message: String?) {

    }

    override fun hideLoading() {
    }


    private fun getFileListData(bundle: Bundle?) {
        var currentPath = ""
        if (bundle != null) {
            currentPath = bundle.getString(PictureSelectorActivity.directory).toString()
            mResultList =
                    bundle.getSerializable(PictureSelectorActivity.SELECT_RESULT_KEY) as ArrayList<SelectImageFileEntity>
            Log.e("TAG", "size==" + mResultList.size)
        }
        if (TextUtils.isEmpty(currentPath)) {
            currentPath = Environment.getExternalStorageDirectory().path
        }
        FileUtils.loadFileList(currentPath, this)
    }

    override fun selector() {

    }

    override fun openCamera(file: File?) {
        TODO("Not yet implemented")
    }

}