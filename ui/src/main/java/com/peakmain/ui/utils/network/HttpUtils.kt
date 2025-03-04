package com.peakmain.ui.utils.network

import android.content.Context
import androidx.annotation.StringDef
import com.peakmain.ui.utils.network.callback.DownloadCallback
import com.peakmain.ui.utils.network.callback.EngineCallBack
import com.peakmain.ui.utils.network.callback.ProgressEngineCallBack
import okhttp3.OkHttpClient
import java.io.File
import java.lang.reflect.ParameterizedType

/**
 * author ：Peakmain
 * createTime：2020/9/12
 * mail:2726449200@qq.com
 * describe：网络请求的工具类
 */
class HttpUtils//不允许外部去调用
private constructor(context: Context) {
    //url
    private lateinit var mUrl: String

    //请求方式
    private var mType = GET_TYPE
    private var mContext: Context = context
    private var mParams: LinkedHashMap<String, Any> = LinkedHashMap()
    private var mFile: File? = null


    //url
    fun url(url: String): HttpUtils {
        mUrl = url
        return this
    }

    //get请求
    fun get(): HttpUtils {
        mType = GET_TYPE
        return this
    }

    fun paramsType(@ParamsType paramsType: String): HttpUtils {
        mParamsType = paramsType
        return this
    }

    /**
     * post请求
     */
    fun post(): HttpUtils {
        mType = POST_TYPE
        return this
    }

    /**
     * 上传
     */
    fun upload(): HttpUtils {
        mType = UPLOAD_TYPE
        return this
    }

    /**
     * 单线程下载
     */
    fun downloadSingle(): HttpUtils {
        mType = DOWNLOAD_SINGLE_TYPE
        return this
    }

    /**
     * 多线程下载
     */
    fun downloadMutil(): HttpUtils {
        mType = DOWNLOAD_MULTI_TYPE
        return this
    }

    /**
     * 添加参数
     *
     * @param key   key
     * @param value string类型
     */
    fun addParams(key: String, value: String): HttpUtils {
        mParams[key] = value
        return this
    }

    fun addParams(key: String, value: Int): HttpUtils {
        mParams[key] = value
        return this
    }

    /**
     * 添加参数
     *
     * @param key  key
     * @param file 文件类型
     */
    fun addParams(key: String, file: File): HttpUtils {
        mParams[key] = file
        return this
    }

    /**
     * 参数集合
     *
     * @param params 参数集合
     */
    fun addParams(params: Map<String, Any>): HttpUtils {
        mParams.putAll(params)
        return this
    }

    fun file(file: File): HttpUtils {
        mFile = file
        return this
    }

    /**
     * 自定义okhttpClient
     *
     * @param okHttpClient okHttpClient
     */
    fun okHttpClient(okHttpClient: OkHttpClient): HttpUtils {
        if (mHttpEngine is OkHttpEngine) {
            val okHttpEngine = mHttpEngine as OkHttpEngine
            okHttpEngine.setOkHttpClient(okHttpClient)
        }
        return this
    }

    /**
     * 执行
     *
     * @param callBack 回掉
     */
    fun execute(callBack: EngineCallBack?) {
        var finalCallBack = callBack
        if (finalCallBack == null) {
            finalCallBack = EngineCallBack.DEFAULT_CALL_BACK
        }
        when (mType) {
            GET_TYPE -> {
                get(mUrl, mParams, finalCallBack)
            }
            POST_TYPE -> {
                post(mUrl, mParams, finalCallBack)
            }
            UPLOAD_TYPE -> {
                uploadFile(mUrl, mFile!!, finalCallBack)
            }
        }
    }

    /**
     * 下载回掉
     */
    fun exectureDownload(callback: DownloadCallback) {
        if (mType == DOWNLOAD_SINGLE_TYPE) {
            downloadSingleManager(mUrl, mFile!!, callback)
        } else if (mType == DOWNLOAD_MULTI_TYPE) {
            downloadMultiManager(mUrl, mFile!!, callback)
        }
    }

    /**
     * 切换引擎
     */
    fun exchangeEngine(httpEngine: IHttpEngine): HttpUtils {
        mHttpEngine = httpEngine
        return this
    }

    private operator fun get(url: String, params: LinkedHashMap<String, Any>, callBack: EngineCallBack) {
        mHttpEngine[mContext, url, params, callBack]
    }

    private fun post(url: String, params: LinkedHashMap<String, Any>, callBack: EngineCallBack) {
        mHttpEngine.post(mContext, url, params, callBack)
    }

    private fun uploadFile(url: String, file: File, callBack: EngineCallBack) {
        if (callBack is ProgressEngineCallBack) {
            mHttpEngine.uploadFile(mContext, url, file, callBack)
        }
    }

    private fun downloadSingleManager(url: String, file: File, callback: DownloadCallback) {
        mHttpEngine.downloadSingleManager(mContext, url, file, callback)
    }

    private fun downloadMultiManager(url: String, file: File, callback: DownloadCallback) {
        mHttpEngine.downloadMultiManager(mContext, url, file, callback)
    }

    @StringDef(PARAMS_KEY_EQUAL_VALUE, PARAMS_KEY_BACKSPLASH_VALUE,PARAMS_KEY_NOKEY_VALUE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ParamsType
    companion object {
        private const val GET_TYPE = 0x0010
        private const val POST_TYPE = 0x0011
        private const val UPLOAD_TYPE = 0x0012
        private const val DOWNLOAD_SINGLE_TYPE = 0x0013
        private const val DOWNLOAD_MULTI_TYPE = 0x0014
        //http://gank.io/api/history/content/2/1
        const val PARAMS_KEY_EQUAL_VALUE: String = "PARAMS_KEY_EQUAL_VALUE"
        //http://gank.io/api/search/query/listview/category/Android/count/10/page/1
        const val PARAMS_KEY_BACKSPLASH_VALUE: String = "PARAMS_KEY_BACKSPLASH_VALUE"
        //http://i.jandan.net/?oxwlxojflwblxbsapi=jandan.get_pic_comments&page=1
        const val PARAMS_KEY_NOKEY_VALUE: String = "PARAMS_KEY_NOKEY_VALUE"
        private var mParamsType = PARAMS_KEY_EQUAL_VALUE

        @JvmStatic
        fun with(context: Context): HttpUtils {
            return HttpUtils(context)
        }

        //默认是OkHttp引擎
        private var mHttpEngine: IHttpEngine = OkHttpEngine()

        //Application中调用
        fun init(httpEngine: IHttpEngine) {
            mHttpEngine = httpEngine
        }

        /**
         * 拼接参数
         */
        fun jointParams(url: String, params: LinkedHashMap<String, Any>): String {
            if (params.isEmpty()) {
                return url
            }
            val stringBuffer = StringBuffer(url)
            if (!url.contains("")) {
                stringBuffer.append("")
            } else {
                if (!url.endsWith("")) {
                    stringBuffer.append("&")
                }
            }
            for ((key, value) in params) {
                when (mParamsType) {
                    PARAMS_KEY_EQUAL_VALUE -> stringBuffer.append("$key=$value&")
                    PARAMS_KEY_BACKSPLASH_VALUE -> stringBuffer.append("$key/$value/")
                    else -> stringBuffer.append("$value/")
                }
            }
            stringBuffer.deleteCharAt(stringBuffer.length - 1)
            return stringBuffer.toString()
        }

        /**
         * 反射获取泛型参数
         */
        fun analysisClazzInfo(`object`: Any): Class<*> {
            val genType = `object`.javaClass.genericSuperclass //获得带有泛型的父类
            val params = (genType as ParameterizedType).actualTypeArguments
            return params[0] as Class<*>
        }
    }
}