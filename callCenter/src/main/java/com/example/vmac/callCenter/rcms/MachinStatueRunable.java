package com.example.vmac.callCenter.rcms;

import android.os.Handler;
import android.os.Message;

import com.example.vmac.callCenter.watson.conversation.Constant;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/9.
 */

public class MachinStatueRunable implements Runnable {
    String data;
    private List<com.example.vmac.callCenter.Message> list;
    private Handler handler;
    private Map<String,Object> conversationContext;

    public MachinStatueRunable(String data, List<com.example.vmac.callCenter.Message> list, Map<String,Object> conversationContext, Handler handler){
        this.data=data;
        this.list=list;
        this.handler=handler;
        this.conversationContext=conversationContext;
    }
    @Override
    public void run() {
        searchMachinStatue();
    }
    public void  searchMachinStatue(){
        String intentAction=RcmsConstant.IntentGetMachineState;
        com.example.vmac.callCenter.Message msg = new com.example.vmac.callCenter.Message();

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty(RcmsConstant.intentKey,intentAction);
        jsonObject.addProperty(RcmsConstant.Data,data);
        String  re=null;
        Message send =new Message();
        try {
            Socket socket =new Socket(RcmsConstant.rcmsHostName,RcmsConstant.hostPort);
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
            writer.write(jsonObject.toString());
            writer.write("\n");
            writer.flush();


            String result =  bufferedReader.readLine();
            socket.close();


            if(result!=null&&result.length()>25){
                JsonParser jsonParser =new JsonParser();
                JsonElement jsonElement =   jsonParser.parse(result);
                JsonObject resultJson=jsonElement.getAsJsonObject();

                JsonElement stope = resultJson.get(RcmsConstant.machinStop);

                if (stope ==null) {
                    send.what= Constant.handler_exception;
                    com.example.vmac.callCenter.Message message=new com.example.vmac.callCenter.Message();
                    message.msgType=Constant.handler_exception;
                    message.setMessage("查询异常");
                    send.obj=message;
                    handler.handleMessage(send);

                }else{
                    String str = stope.getAsString();
                    send.what= Constant.handler_add_send_message;
                    msg.msgType = Constant.watsonType;
                    if (RcmsConstant.Not_Warranty.equals(str)) {
                        msg.setMessage("您的机器目前不在保");
                        conversationContext.put(RcmsConstant.State_Key,"不在保");
                        send.obj=msg;
                    } else {
                        msg.setMessage("您的机器目前在保");
                        conversationContext.put(RcmsConstant.State_Key,"在保");
                        send.obj=msg;
                    }
                    handler.handleMessage(send);
                }



            }else {
                send.what= Constant.handler_add_send_message;
                msg.setMessage("没有查到您要的机器信息");
                conversationContext.put(RcmsConstant.State_Key,"不存在");
                send.obj=msg;
               handler.handleMessage(send);
            }



        } catch (IOException e) {
            e.printStackTrace();
            send.what= Constant.handler_exception;
            com.example.vmac.callCenter.Message message=new com.example.vmac.callCenter.Message();
            message.msgType=Constant.handler_exception;
            message.setMessage("查询异常");
            send.obj=message;
            handler.handleMessage(send);
        }
    }
}
