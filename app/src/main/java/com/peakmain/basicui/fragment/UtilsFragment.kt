package com.peakmain.basicui.fragment

import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.View
import com.peakmain.basicui.R
import com.peakmain.basicui.activity.utils.DataBaseActivity
import com.peakmain.basicui.activity.utils.GlideActivity
import com.peakmain.basicui.activity.utils.OkHttpActivity
import com.peakmain.basicui.adapter.BaseRecyclerStringAdapter
import com.peakmain.basicui.base.BaseFragmnet
import com.peakmain.basicui.utils.ActivityUtil
import com.peakmain.ui.navigationbar.DefaultNavigationBar
import com.peakmain.ui.recyclerview.listener.OnItemClickListener
import java.util.*

/**
 * author ：Peakmain
 * createTime：2020/9/13
 * mail:2726449200@qq.com
 * describe：
 */
class UtilsFragment : BaseFragmnet() {
    private var mRecyclerView: RecyclerView? = null
    private lateinit var mUtilsBean: MutableList<String>
    private var mAdapter: BaseRecyclerStringAdapter? = null
    override val layoutId: Int
        protected get() = R.layout.fragment_utils

    override fun initView(view: View?) {
        DefaultNavigationBar.Builder(context, view!!.findViewById(R.id.view_root))
                .hideLeftText()
                .hideRightView()
                .setTitleText("工具类")
                .setToolbarBackgroundColor(R.color.colorAccent)
                .create()
        mRecyclerView = view.findViewById(R.id.recycler_view)
    }

    override fun initData() {
        mUtilsBean = ArrayList()
        mUtilsBean.add("okhttp网络引擎切换工具类封装")
        mUtilsBean.add("数据库封装")
        mUtilsBean.add("Glide图片选择切换封装")
        mAdapter = BaseRecyclerStringAdapter(context, mUtilsBean)
        mRecyclerView!!.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL))
        mRecyclerView!!.adapter = mAdapter
        mAdapter!!.setOnItemClickListener(object : OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> ActivityUtil.gotoActivity(context, OkHttpActivity::class.java)
                    1 -> ActivityUtil.gotoActivity(context, DataBaseActivity::class.java)
                    2 -> ActivityUtil.gotoActivity(context, GlideActivity::class.java)
                    else -> {
                    }
                }
            }
        })
    }
}