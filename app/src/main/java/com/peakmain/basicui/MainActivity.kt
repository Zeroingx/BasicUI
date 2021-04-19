package com.peakmain.basicui

import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentTransaction
import android.view.MenuItem
import com.peakmain.basicui.base.BaseActivity
import com.peakmain.basicui.fragment.HomeFragment
import com.peakmain.basicui.fragment.MineFragment
import com.peakmain.basicui.fragment.UtilsFragment

class MainActivity : BaseActivity() {
    //View
    var mBottomNavigation: BottomNavigationView? = null

    //fragments
    private var mHomeFragment: HomeFragment? = null
    private var mUtilsFragment: UtilsFragment? = null
    private var mMineFragment: MineFragment? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_main
    }

    override fun initView() {
        mBottomNavigation = findViewById(R.id.bottom_navigation)
    }

    override fun initData() {
        showFragment(FRAGMENT_HOME)
        mBottomNavigation!!.setOnNavigationItemSelectedListener { item: MenuItem -> onOptionsItemSelected(item) }
        mBottomNavigation!!.selectedItemId = R.id.menu_home
        if (!haveNetWorkPerm()) {
            requestNetWorkPerm()
        }
        if (!haveReadAndWritePerm()) {
            requestReadAndWritePerm()
        }
        if(!haveCameraPerm()){
            requestCameraPerm()
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val itemId = item.itemId
        when (itemId) {
            R.id.menu_home -> {
                showFragment(FRAGMENT_HOME)
                return true
            }
            R.id.menu_utils -> {
                showFragment(FRAGMENT_UTILS)
                return true
            }
            R.id.menu_me -> {
                showFragment(FRAGMENT_ME)
                return true
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showFragment(index: Int) {
        val ft = supportFragmentManager.beginTransaction()
        hintFragment(ft)
        when (index) {
            FRAGMENT_HOME ->
                /**
                 * 如果Fragment为空，就新建一个实例
                 * 如果不为空，就将它从栈中显示出来
                 */
                if (mHomeFragment == null) {
                    mHomeFragment = HomeFragment()
                    ft.add(R.id.container, mHomeFragment!!, HomeFragment::class.java.name)
                } else {
                    ft.show(mHomeFragment!!)
                }
            FRAGMENT_ME -> if (mMineFragment == null) {
                mMineFragment = MineFragment()
                ft.add(R.id.container, mMineFragment!!, MineFragment::class.java.name)
            } else {
                ft.show(mMineFragment!!)
            }
            FRAGMENT_UTILS -> if (mUtilsFragment == null) {
                mUtilsFragment = UtilsFragment()
                ft.add(R.id.container, mUtilsFragment!!, UtilsFragment::class.java.name)
            } else {
                ft.show(mUtilsFragment!!)
            }
            else -> {
            }
        }
        ft.commit()
    }

    /**
     * 隐藏fragment
     */
    private fun hintFragment(ft: FragmentTransaction) {
        // 如果不为空，就先隐藏起来
        if (mHomeFragment != null) {
            ft.hide(mHomeFragment!!)
        }
        if (mMineFragment != null) {
            ft.hide(mMineFragment!!)
        }
        if (mUtilsFragment != null) {
            ft.hide(mUtilsFragment!!)
        }
    }

    companion object {
        //底部切换的tab常量
        private const val FRAGMENT_HOME = 0
        private const val FRAGMENT_ME = 1
        private const val FRAGMENT_UTILS = 2
    }
}