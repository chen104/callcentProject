package com.example.vmac.callCenter.app;
import android.app.Application;
import com.example.vmac.callCenter.R;
import com.example.vmac.callCenter.iflytek.result.IflytekClient;
import com.example.vmac.callCenter.watson.conversation.WatsonClient;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import org.xutils.x;


/**
 * Created by Administrator on 2017/8/17.
 */

public class WatsonApplication extends Application {
    private WatsonClient watsonClient;
    private IflytekClient iflytekClient;
    String analytics_APIKEY;
    public String uuid="";
    @Override
    public void onCreate() {
        super.onCreate();
        analytics_APIKEY = this.getString(R.string.mobileanalytics_apikey);
        //初始化科大讯飞api
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + getString(R.string.iflytekAppId));
        x.Ext.init(this);

    }

    public synchronized IflytekClient getIflytekClient() {
        if (iflytekClient == null) {
            iflytekClient = new IflytekClient(getApplicationContext());
        }
        return iflytekClient;
    }

    public synchronized WatsonClient getWatsonClient() {
        if (watsonClient == null) {
            watsonClient = new WatsonClient(this.getApplicationContext());
        }
        return watsonClient;
    }



}
