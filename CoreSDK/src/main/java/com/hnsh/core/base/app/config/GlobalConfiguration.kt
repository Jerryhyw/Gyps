package com.hnsh.core.base.app.config

import android.content.Context
import com.google.gson.GsonBuilder
import com.hnsh.core.BuildConfig
import com.hnsh.core.base.app.AppLifecycle
import com.hnsh.core.base.app.AppModule
import com.hnsh.core.base.app.ConfigModule
import com.hnsh.core.http.ResponseErrorListenerImpl
import com.hnsh.core.http.di.ClientModule
import com.hnsh.core.http.interceptor.RequestInterceptor
import me.jessyan.retrofiturlmanager.RetrofitUrlManager
import okhttp3.OkHttpClient

/**
 * @Description: 全局基本配置
 * @Author:   Hsp
 * @Email:    1101121039@qq.com
 * @CreateTime:     2020/9/16 16:31
 * @UpdateRemark:   更新说明：
 */
class GlobalConfiguration : ConfigModule {

    override fun applyOptions(context: Context?, builder: GlobalConfigModule.Builder) {
        if (!BuildConfig.LOG_DEBUG) //Release 时,让框架不再打印 Http 请求和响应的信息
            builder.printHttpLogLevel(RequestInterceptor.Level.NONE)
        builder.baseurl("https://api.github.com")
//            .globalHttpHandler(context?.let { GlobalHttpHandlerImpl(it) })
            .responseErrorListener(context?.let { ResponseErrorListenerImpl(it) })
            .gsonConfiguration(object : AppModule.GsonConfiguration {
                override fun configGson(context: Context?, builder: GsonBuilder?) {
                    //这里可以自己自定义配置Gson的参数，支持将序列化key为object的map,默认只能序列化key为string的map
                    builder?.serializeNulls()
                        ?.enableComplexMapKeySerialization()
                }
            })
            .okhttpConfiguration(object : ClientModule.OkhttpConfiguration {
                override fun configOkhttp(context: Context, builder: OkHttpClient.Builder) {
                    RetrofitUrlManager.getInstance().with(builder)
                }
            })
    }

    override fun injectModulesLifecycle(context: Context, lifecycleList: ArrayList<AppLifecycle>) {

    }
}