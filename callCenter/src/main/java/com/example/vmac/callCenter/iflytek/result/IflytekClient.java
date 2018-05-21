package com.example.vmac.callCenter.iflytek.result;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by Administrator on 2017/8/17.
 */

public class IflytekClient {
    private Context context;

    private  String TAG ;
    // 语音听写对象
    private SpeechRecognizer speechRecognizer;



    // 语音听写UI
    private RecognizerDialog mIatDialog;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    public IflytekClient(final Context context){
        this.context=context;
        TAG=context.getPackageName();




    }

    public synchronized SpeechRecognizer   getSpeechRecognizer(){
        if(speechRecognizer==null){
            // 初始化识别无UI识别对象
            // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
            speechRecognizer = SpeechRecognizer.createRecognizer(context, new InitListener() {
                @Override
                public void onInit(int code) {
                    Log.d(TAG, "SpeechRecognizer init() code = " + code);
                     if (code != ErrorCode.SUCCESS) {
                       showTip("初始化失败，错误码：" + code);
                       Log.i(TAG,"初始化失败，错误码：" + code);
                     }
                }
            });

        }
        return  speechRecognizer;
    }

    private void showTip(String s) {
    }

    public void destroySpeechRecognizer(){
        if( null != speechRecognizer ){
            // 退出时释放连接
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer =null;
        }
    }


    public void setSpeechRecognizerParam() {
        // 清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //翻译
       /*
       Log.i( TAG, "translate enable" );
        mIat.setParameter( SpeechConstant.ASR_SCH, "1" );
       mIat.setParameter( SpeechConstant.ADD_CAP, "translate" );
      mIat.setParameter( SpeechConstant.TRS_SRC, "its" );
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
            mIat.setParameter(SpeechConstant.ACCENT, null);

            if( mTranslateEnable ){
                mIat.setParameter( SpeechConstant.ORI_LANG, "en" );
                mIat.setParameter( SpeechConstant.TRANS_LANG, "cn" );
            }
       */
        // 设置语言
        speechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        speechRecognizer.setParameter(SpeechConstant.ACCENT, "zh_cn");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        speechRecognizer.setParameter(SpeechConstant.VAD_EOS,"1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        speechRecognizer.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        speechRecognizer.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }
}
