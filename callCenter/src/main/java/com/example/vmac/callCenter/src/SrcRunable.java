package com.example.vmac.callCenter.src;

import android.os.Handler;

import com.example.vmac.callCenter.Message;
import com.example.vmac.callCenter.rcms.RcmsConstant;
import com.example.vmac.callCenter.watson.conversation.Constant;

import java.util.Map;

/**
 * Created by Administrator on 2017/10/23.
 */

public class SrcRunable extends  Thread {

    public String intentAction;
    public Handler handler;
    public String data;
    private Map<String,Object> conversationContext;
    public  SrcRunable (String intentAction,String data,Map<String,Object> conversationContext,Handler handler){
        this.intentAction=intentAction;
        this.handler=handler;
        this.data=data;
        this.conversationContext=conversationContext;
    }
    @Override
    public void run() {
        try {
            sleep(1000);
            doQuery();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        android.os.Message msg =new android.os.Message();
        conversationContext.put("src_status","存在");
        msg.what= Constant.handler_show_rsc_result;
        com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
        message.msgType=Constant.watsonType;
        message.setMessage("https://www.ibm.com/support/knowledgecenter/en/POWER7/p7eai/11001510.htm?view=embed");
        msg.obj=message;

        handler.handleMessage(msg);
    }

    private void doQuery(){

    }


}
