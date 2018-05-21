package com.example.vmac.callCenter.rcms;

import android.os.Handler;
import android.util.Log;

import com.example.vmac.callCenter.util.CallInfoUtil;
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
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Administrator on 2018/1/16.
 */

public class CallcenterService {
    Handler handler;
    Map<String,Object> context;
    public String temp;
    String TAG=CallcenterService.class.getSimpleName();
    public void setContext(Map<String, Object> context) {
        this.context = context;
    }



        public String queryCallInfo(String param){
            Log.i(TAG,"***查询callInfo"+param);

            JsonObject jsonObject =new JsonObject();
            jsonObject.addProperty("key","value");
           jsonObject = callService(RcmsConstant.IntentGetCallfo,param);
            boolean stat =jsonObject.getAsJsonObject(RcmsConstant.stat).getAsBoolean();
            if(stat){
                context.put("Call_status","找到");
                temp="找到";
              String msg =   CallInfoUtil.parseJson(jsonObject.getAsJsonObject(RcmsConstant.Data));
                return msg;
            }else{
                context.put("Call_status","未找到");
                temp="未找到";
                Log.i(TAG,jsonObject.getAsJsonObject(RcmsConstant.Data).toString());
            }

            return null;

        }


        protected JsonObject callService(String intentValue,String data){
            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty(RcmsConstant.intentKey,intentValue);
            jsonObject.addProperty(RcmsConstant.Data,data);

            String  re=null;
            try {
                Socket socket =new Socket(RcmsConstant.rcmsHostName,RcmsConstant.hostPort);
                InputStream inputStream=socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")));
                writer.write(jsonObject.toString());
                writer.write("\n");
                writer.flush();


                String result =  bufferedReader.readLine();

                if(result!=null&&result.length()>0) {
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(result);
                    JsonObject resultJson = jsonElement.getAsJsonObject();
                    return resultJson;
                }else{
                    jsonObject = new JsonObject();
                    jsonObject.addProperty(RcmsConstant.stat,false);
                    jsonObject.addProperty(RcmsConstant.Data,"返回值为空");
                }

            } catch (IOException e) {
                e.printStackTrace();
                jsonObject = new JsonObject();
                jsonObject.addProperty(RcmsConstant.stat,false);
                jsonObject.addProperty(RcmsConstant.Data,e.getMessage());
            }
            return jsonObject;
        }



}
