package com.example.vmac.callCenter.rcms;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

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
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/9.
 */

public class CallInfoRunable implements  Runnable {
    public String intentAction;
    public Handler handler;
    public String data;
    private Map<String,Object> conversationContext;
    public CallInfoRunable(String intentAction,String data,Map<String,Object> conversationContext,Handler handler){
        this.intentAction=intentAction;
        this.handler=handler;
        this.data=data;
        this.conversationContext=conversationContext;
    }
    @Override
    public void run() {
            if(RcmsConstant.IntentGetCallByNumber.equals(intentAction)){

                queryCall(RcmsConstant.IntentGetCallByNumber,data);

            }else if(RcmsConstant.IntentGetCallBySerial.equals(intentAction)){
                conversationContext.remove("type");
                conversationContext.remove("SN");
                queryCall(RcmsConstant.IntentGetCallBySerial,data);
            }
    }

    public JsonObject queryCall(String intentAction,String data){
        Message msg =new Message();

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty(RcmsConstant.intentKey,intentAction);
        jsonObject.addProperty(RcmsConstant.Data,data);
        String  re=null;
        try {
            Socket socket =new Socket(RcmsConstant.rcmsHostName,RcmsConstant.hostPort);
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream,Charset.forName("utf-8")));
            writer.write(jsonObject.toString());
            writer.write("\n");
            writer.flush();


            String result =  bufferedReader.readLine();
            if(result!=null&&result.length()>25){
                JsonParser jsonParser =new JsonParser();
                JsonElement jsonElement =   jsonParser.parse(result);
                JsonObject resultJson=jsonElement.getAsJsonObject();
//                String machinStopStatue=  resultJson.get(RcmsConstant.machinStop).getAsString();
//                parseJson(jsonObject);
                com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                message.msgType=Constant.watsonType;
                message.setMessage(parseJson(resultJson));
                msg.obj=    message;
                msg.what= Constant.handler_callInfo;
                conversationContext.put("Call_status","找到");

                handler.handleMessage(msg);

            }else {
                msg.what= Constant.handler_callInfo;
                com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                message.msgType=Constant.watsonType;
                message.setMessage("未找到Call编号为："+data.replace("_","")+" Call的信息");
                msg.obj=message;
                conversationContext.put("Call_status","未找到");
                handler.handleMessage(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
            msg.what= Constant.handler_exception;
            com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
            message.msgType=Constant.watsonType;
            message.setMessage("服务器开小差ing……");
            msg.obj=message;
            //conversationContext.put("Call_status","未找到");
            handler.handleMessage(msg);
        }
        return jsonObject;
    }



        @NonNull
        private  String parseJson(JsonObject jsonObject){
            StringBuilder sb=new StringBuilder();

            readItem(CallInfoKey.callNumberKey,jsonObject, CallInfoKey.callNumber,sb);
            readItem(CallInfoKey.accountNameKey,jsonObject, CallInfoKey.accountName,sb);
            readItem(CallInfoKey.customerNumberKey,jsonObject, CallInfoKey.customerNumber,sb);
//            readItem(CallInfoKey.serialKey,jsonObject,CallInfoKey.serial,sb);
//            readItem(CallInfoKey.typeKey,jsonObject,CallInfoKey.type,sb);
            readTypeSerial(jsonObject, CallInfoKey.type,sb);
            readStartTime(CallInfoKey.startTimeKey,jsonObject, CallInfoKey.startTime,sb);

            readItem(CallInfoKey.callByPeopleKey,jsonObject, CallInfoKey.callByPeople,sb);
            readItem(CallInfoKey.callByNumberKey,jsonObject, CallInfoKey.callByNumber,sb);

            readItem(CallInfoKey.sendEngineerKey,jsonObject, CallInfoKey.sendEngineer,sb);
            readItem(CallInfoKey.engineerPhoneKey,jsonObject, CallInfoKey.engineerPhone,sb);
            readItem(CallInfoKey.faultDescKey,jsonObject, CallInfoKey.faultDesc,sb);
            readItem(CallInfoKey.partNoKey,jsonObject, CallInfoKey.partNo,sb);
            readItem(CallInfoKey.partDescKey,jsonObject, CallInfoKey.partDesc,sb);
            return sb.toString();
        }
        public void readStartTime(String key,JsonObject jsonObject,String chzn,StringBuilder sb){
            JsonElement element =jsonObject.get(key);
            StringBuilder stringBuilder =new StringBuilder();
            if(element==null){return;}
            String accountName =element .getAsString();
            sb.append(chzn);
            sb.append(":");
           stringBuilder.append(accountName);
            stringBuilder.insert(10," ");
            sb.append(stringBuilder.toString());
            sb.append("\n");
        }
        public void  readTypeSerial(JsonObject jsonObject,String chzn,StringBuilder sb){
         //  readItem(CallInfoKey.serialKey,jsonObject,CallInfoKey.serial,sb);
         //   readItem(CallInfoKey.typeKey,jsonObject,CallInfoKey.type,sb);
            JsonElement element =jsonObject.get(CallInfoKey.serialKey);
            JsonElement elementType = jsonObject.get(CallInfoKey.typeKey);
            if(element==null||elementType==null){return;}
            String typeAndSerial = element.getAsString();
            String type =typeAndSerial.substring(0,4);
            StringBuilder stringBuilder=new StringBuilder(typeAndSerial.substring(4));
            stringBuilder.delete(2,4);
            String serial = stringBuilder.toString();
            type =type+"-"+elementType.getAsString();
            sb.append(CallInfoKey.type).append(":").append( type).append("\n");
            sb.append(CallInfoKey.serial).append(":").append(serial).append("\n");

        }

        public void readItem(String key,JsonObject jsonObject,String chzn,StringBuilder sb){
            JsonElement element =jsonObject.get(key);
            if(element==null){return;}
            String accountName =element .getAsString();
            sb.append(chzn);
            sb.append(":");
            sb.append(accountName);
            sb.append("\n");

        }
        public String getStr(JsonObject str){
            return parseJson(str);
        }
}
