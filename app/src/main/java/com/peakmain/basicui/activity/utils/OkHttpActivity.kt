package com.peakmain.basicui.activity.utils

import android.os.Environment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.peakmain.basicui.R
import com.peakmain.basicui.adapter.BaseRecyclerStringAdapter
import com.peakmain.basicui.base.BaseActivity
import com.peakmain.basicui.utils.ToastUtils
import com.peakmain.ui.navigationbar.DefaultNavigationBar
import com.peakmain.ui.recyclerview.listener.OnItemClickListener
import com.peakmain.ui.utils.LogUtils
import com.peakmain.ui.utils.network.HttpUtils.Companion.with
import com.peakmain.ui.utils.network.callback.DownloadCallback
import com.peakmain.ui.utils.network.callback.EngineCallBack
import java.io.File
import java.util.*

/**
 * author ：Peakmain
 * createTime：2020/9/13
 * mail:2726449200@qq.com
 * describe：okhttp工具类
 */
class OkHttpActivity : BaseActivity() {
    private lateinit var file: File
    private lateinit var mBean: MutableList<String>
    private var mAdapter: BaseRecyclerStringAdapter? = null
    private var mRecyclerView: RecyclerView? = null
    private var mProgressBar: ProgressBar? = null
    private lateinit var mTvResult: TextView
    override fun getLayoutId(): Int {
        return R.layout.basic_linear_recycler_view
    }

    override fun initView() {
        mRecyclerView = findViewById(R.id.recycler_view)
        DefaultNavigationBar.Builder(this, findViewById(android.R.id.content))
                .hideLeftText()
                .hideRightView()
                .setTitleText("okhttp网络引擎切换工具类")
                .setToolbarBackgroundColor(R.color.colorAccent)
                .create()
        mProgressBar = findViewById(R.id.progressbar)
        mTvResult = findViewById(R.id.tv_result)
        mTvResult.setVisibility(View.VISIBLE)
    }

    override fun initData() {
        mBean = ArrayList()
        mBean.add("get方法请求")
        mBean.add("post方法请求")
        mBean.add("单线程下载")
        mBean.add("多线程下载")
        mAdapter = BaseRecyclerStringAdapter(this, mBean)
        mRecyclerView!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        mRecyclerView!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> with(this@OkHttpActivity)
                            .url("http://i.jandan.net/")
                            .addParams("oxwlxojflwblxbsapi", "jandan.get_pic_comments")
                            .addParams("page", "1")
                            .execture(object : EngineCallBack {
                                override fun onError(e: Exception?) {
                                    LogUtils.e(e!!.message)
                                }

                                override fun onSuccess(result: String?) {
                                    mTvResult!!.text = result
                                }
                            })
                    1 -> with(this@OkHttpActivity)
                            .url("https://www.wanandroid.com/user/login")
                            .addParams("username", "peakmain")
                            .addParams("password", "123456")
                            .post()
                            .execture(object : EngineCallBack {
                                override fun onError(e: Exception?) {
                                    LogUtils.e(e!!.message)
                                }

                                override fun onSuccess(result: String?) {
                                    mTvResult!!.text = result
                                }
                            })
                    2 -> {
                        val file = File(Environment.getExternalStorageDirectory(), "test.apk")
                        if (file.exists()) {
                            file.delete()
                        }
                        with(this@OkHttpActivity)
                                .url("http://imtt.dd.qq.com/16891/apk/87B3504EE9CE9DC51E9F295976F29724.apk")
                                .downloadSingle()
                                .file(file)
                                .exectureDownload(object : DownloadCallback {
                                    override fun onFailure(e: Exception?) {
                                        LogUtils.e(e!!.message)
                                    }

                                    override fun onSucceed(file: File?) {
                                        ToastUtils.showShort("file下载完成")
                                        LogUtils.e("文件保存的位置:" + file!!.absolutePath)
                                        mProgressBar!!.visibility = View.GONE
                                        mProgressBar!!.progress = 0
                                    }

                                    override fun onProgress(progress: Int) {
                                        LogUtils.e("单线程下载apk的进度:$progress")
                                        mProgressBar!!.progress = progress
                                        mProgressBar!!.visibility = View.VISIBLE
                                    }
                                })
                    }
                    3 -> {
                        file = File(Environment.getExternalStorageDirectory(), "test.apk")
                        if (file.exists()) {
                            file.delete()
                        }
                        with(this@OkHttpActivity)
                                .url("http://imtt.dd.qq.com/16891/apk/87B3504EE9CE9DC51E9F295976F29724.apk")
                                .downloadMutil()
                                .file(file)
                                .exectureDownload(object : DownloadCallback {
                                    override fun onFailure(e: Exception?) {
                                        LogUtils.e(e!!.message)
                                    }

                                    override fun onSucceed(file: File?) {
                                        LogUtils.e(file!!.absolutePath + "," + file.name)
                                        Toast.makeText(this@OkHttpActivity, "下载完成", Toast.LENGTH_LONG).show()
                                        mProgressBar!!.visibility = View.GONE
                                        mProgressBar!!.progress = 0
                                    }

                                    override fun onProgress(progress: Int) {
                                        LogUtils.e("$progress%")
                                        mProgressBar!!.visibility = View.VISIBLE
                                        mProgressBar!!.progress = progress
                                    }
                                })
                    }
                }
            }
        })
    }
}