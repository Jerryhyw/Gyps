package com.swallow.gyps.msc

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.blankj.utilcode.util.NetworkUtils
import com.hnsh.gyps.R
import com.hnsh.gyps.databinding.ActivityVoiceInputBinding
import com.hsp.resource.ext.initBlueActionBar
import com.iflytek.cloud.InitListener
import com.iflytek.cloud.RecognizerResult
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechError
import com.iflytek.cloud.ui.RecognizerDialog
import com.iflytek.cloud.ui.RecognizerDialogListener
import com.swallow.fly.base.view.BaseActivity
import com.swallow.fly.ext.logd
import com.swallow.fly.ext.txt
import com.swallow.gyps.msc.viewmodel.VoiceInputViewModel
import dagger.hilt.android.AndroidEntryPoint


/**
 * @Description:  语音输入测试
 * @Author: Hsp
 * @Email:  1101121039@qq.com
 * @CreateTime: 2020/11/21 15:33
 * @UpdateRemark:
 */
@AndroidEntryPoint
class VoiceInputActivity : BaseActivity<VoiceInputViewModel, ActivityVoiceInputBinding>(),
    View.OnClickListener {
    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, VoiceInputActivity::class.java))
        }
    }

    override val modelClass: Class<VoiceInputViewModel>
        get() = VoiceInputViewModel::class.java

    private lateinit var mIatDialog: RecognizerDialog

    override fun initView(savedInstanceState: Bundle?) {
        initBlueActionBar(true, "语音听写")
        initInputUI()
    }

    private fun initInputUI() {
        mIatDialog = RecognizerDialog(this, object : InitListener {
            override fun onInit(p0: Int) {
                logd { "-----------初始化UI组件----------->" }
            }
        })
        //设置语法ID和 SUBJECT 为空，以免因之前有语法调用而设置了此参数；或直接清空所有参数，具体可参考 DEMO 的示例。
        mIatDialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null)
        mIatDialog.setParameter(SpeechConstant.SUBJECT, null)
        //设置返回结果格式，目前支持json,xml以及plain 三种格式，其中plain为纯听写文本内容
//        mIatDialog.setParameter(SpeechConstant.RESULT_TYPE, "json")
        mIatDialog.setParameter(SpeechConstant.RESULT_TYPE, "plain")
        // 设置离线（local）、在线(cloud) 语音听写
        //设置语音输入语言，zh_cn为简体中文
        mIatDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn")
        //设置结果返回语言
        mIatDialog.setParameter(SpeechConstant.ACCENT, "mandarin")
        // 设置语音前端点:静音超时时间，单位ms，即用户多长时间不说话则当做超时处理
        //取值范围{1000～10000}
        mIatDialog.setParameter(SpeechConstant.VAD_BOS, "4000")
        //设置语音后端点:后端点静音检测时间，单位ms，即用户停止说话多长时间内即认为不再输入，
        //自动停止录音，范围{0~10000}
        mIatDialog.setParameter(SpeechConstant.VAD_EOS, "1000")
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIatDialog.setParameter(SpeechConstant.ASR_PTT, "1")

        //开始识别，并设置监听器
        mIatDialog.setListener(object : RecognizerDialogListener {
            @SuppressLint("SetTextI18n")
            override fun onResult(p0: RecognizerResult?, p1: Boolean) {
                logd { "----------------onResult-------->${p0?.resultString}" }
                binding.etContent.setText("${binding.etContent.txt() ?: ""}${p0?.resultString ?: ""}")
            }

            override fun onError(p0: SpeechError?) {
                logd { "-------------errorCode------>${p0?.errorCode}" }
                logd { "-------------errorDescription------>${p0?.errorDescription}" }
            }
        })
        val tag = mIatDialog.window?.decorView?.findViewWithTag<View>("textlink")
        if (null != tag) {
            val txt = tag as TextView
            txt.text = ""
        }
    }

    override fun initData(savedInstanceState: Bundle?) {
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_input -> startVoiceInput()
            R.id.btn_clear -> binding.etContent.setText("")
        }
    }

    /**
     * 开始听写
     */
    private fun startVoiceInput() {
        if (!mIatDialog.isShowing) {
            val engineType = if (NetworkUtils.isConnected()) "cloud" else "local"
            mIatDialog.setParameter(SpeechConstant.ENGINE_TYPE, engineType);
            mIatDialog.show()
        }
    }


    override fun getStatusBarColor(): Int {
        return R.color.toolbar_blue
    }

    override fun showDarkToolBar(): Boolean {
        return false
    }
}