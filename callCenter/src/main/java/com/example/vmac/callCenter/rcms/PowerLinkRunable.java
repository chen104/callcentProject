package com.example.vmac.callCenter.rcms;

import android.os.Handler;

import com.example.vmac.callCenter.Message;
import com.example.vmac.callCenter.watson.conversation.Constant;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/19.
 */

public class PowerLinkRunable implements Runnable {
    String data;
    private List<Message> list;
    private Handler handler;
    private Map<String, Object> conversationContext;
    String intentAction = RcmsConstant.IntentGetPowerPart;

    public PowerLinkRunable(String data,  Map<String, Object> conversationContext, Handler handler, String intentAction) {
        this.data = data;
        this.list = list;
        this.handler = handler;
        this.conversationContext = conversationContext;
        this.intentAction = intentAction;
    }

    @Override
    public void run()

    { //test();
       doCheck();
    }

    public void test(){
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        android.os.Message send = new android.os.Message();
        com.example.vmac.callCenter.Message msg = new com.example.vmac.callCenter.Message();
        send.what= Constant.handler_add_message;
        msg.setMessage("https://www.ibm.com/support/knowledgecenter/en/POWER7/p7ecs/p7ecsparts_9179mhc.htm?view=embed");
        msg.msgType=Constant.watsonType;
        send.obj=msg;
        Object intent =conversationContext.get(Constant.ACTION);
        if(RcmsConstant.IntentGetPowerPart.equals(intentAction)){
            msg.msgType=Constant.web_part_link;
            conversationContext.put(RcmsConstant.power_part_key,"找到");
        }else{
            msg.msgType=Constant.web_local_link;
            conversationContext.put(RcmsConstant.power_local_key,"找到");
        }
        handler.handleMessage(send);
    }

    private void doCheck() {

        com.example.vmac.callCenter.Message msg = new com.example.vmac.callCenter.Message();

        Object intent =conversationContext.get(Constant.ACTION);
        if(intent!=null&&Constant.action_local_values.equals(intent)) {
            intentAction =RcmsConstant.IntentGetPowerLocal;
        }else if(Constant.action_part_values.equals(intent)){
            intentAction =RcmsConstant.IntentGetPowerPart;
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(RcmsConstant.intentKey, intentAction);
        jsonObject.addProperty(RcmsConstant.Data, data);
        String re = null;
        android.os.Message send = new android.os.Message();
        try {
            Socket socket = new Socket(RcmsConstant.rcmsHostName, RcmsConstant.hostPort);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            writer.write(jsonObject.toString());
            writer.write("\n");
            writer.flush();


            String result = bufferedReader.readLine();
            socket.close();


            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(result);
            JsonObject resultJson = jsonElement.getAsJsonObject();

            JsonElement jstat = resultJson.get("stat");
            String imgParent= File.separator;
            if (jstat != null&&jstat.getAsBoolean()) {
                send.what= Constant.handler_add_message;
               JsonElement data= resultJson.get(RcmsConstant.Data);
              JsonObject  pwoerImte =  data.getAsJsonObject();
                String link="";
                if(RcmsConstant.IntentGetPowerPart.equals(intentAction)){
                    JsonElement JLING = pwoerImte.get("PART_LINK");
                    if(JLING!=null&&!"".equals(JLING.getAsString())){
                        link=JLING.getAsString();
                        conversationContext.put(RcmsConstant.power_part_key,"找到");
                    }
                    JsonElement jimg = pwoerImte.get("PARTIMAGE");
                    if(jimg!=null&&!"".equals(jimg.getAsString())){
                        imgParent=imgParent+"part"+File.separator+jimg.getAsString();
                    }

                }else{
                    JsonElement JLING = pwoerImte.get("LOCAL_LINK");
                    if(JLING!=null&&!"".equals(JLING.getAsString())){
                        link=JLING.getAsString();
                        conversationContext.put(RcmsConstant.power_local_key,"找到");
                    }
                    JsonElement jimg = pwoerImte.get("LOCALIMAGE");
                    if(jimg!=null&&!"".equals(jimg.getAsString())){
                        imgParent=imgParent+"local"+File.separator+jimg.getAsString();
                    }
                }
                if(link==null||"".equals(link)){
                    msg.msgType= Constant.watsonType;
                    msg.setMessage("没有找到");
                    if(RcmsConstant.IntentGetPowerPart.equals(intentAction)){

                        conversationContext.put(RcmsConstant.power_part_key,"未找到");
                    }else{

                        conversationContext.put(RcmsConstant.power_local_key,"未找到");
                    }
                }else {

                    msg.msgType=Constant.chat_item_power_start_web;
                    msg.setMessage(link);
                }

                //下载图片

                //
                send.what=Constant.handler_add_send_message;
                send.obj=msg;
                handler.handleMessage(send);

            } else {
                send.what= Constant.handler_add_message;
                msg.setMessage("没有查到您要的 power 的信息");
                msg.msgType=Constant.watsonType;
                send.obj=msg;
                if(RcmsConstant.IntentGetPowerPart.equals(intentAction)){

                    conversationContext.put(RcmsConstant.power_part_key,"未找到");
                }else{

                    conversationContext.put(RcmsConstant.power_local_key,"未找到");
                }
                handler.handleMessage(send);

            }

        } catch (IOException e) {
            e.printStackTrace();
            send.what = Constant.handler_exception;
            com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
            message.msgType = Constant.watsonType;
            message.setMessage("查询异常");
            send.obj = message;
            handler.handleMessage(send);
        }
    }


    private boolean downloadFile(String fileName, ByteArrayOutputStream byout){
        Socket socket = null;
        boolean re=true;
        try {
            socket = new Socket(RcmsConstant.rcmsHostName, RcmsConstant.hostPort);
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(RcmsConstant.intentKey, "downloadFile");
            jsonObject.addProperty("fileName", fileName);
            writer.write(jsonObject.toString());
            writer.write("\n");
            writer.flush();
            byte[] by =new byte[1024];
            int n = inputStream.read(by);
            while(n!=-1){
                byout.write(by,0,n);
                n=inputStream.read(by);
            }

        } catch (IOException e) {
            e.printStackTrace();
            return  false;
        }
      return  re;
    }
}
