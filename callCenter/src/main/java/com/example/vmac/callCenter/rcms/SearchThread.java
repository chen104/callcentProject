package com.example.vmac.callCenter.rcms;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;

import com.example.vmac.callCenter.Message;
import com.example.vmac.callCenter.OnClickCallBack;
import com.example.vmac.callCenter.R;
import com.example.vmac.callCenter.watson.conversation.Constant;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;

import static android.R.attr.port;

/**
 * Created by Administrator on 2017/9/21.
 */

public class SearchThread extends  Thread {
    JsonObject jsonObject;
    private String hostName;
    private int port;
    private Context context;
    private Socket  socket;
    private String typeModel;

    OnClickCallBack callBack;
    private List<Message> list;
    private Handler handler;
    private Map<String,Object> conversationContext;

    public void setConversationContext(Map<String, Object> conversationContext) {
        this.conversationContext = conversationContext;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public SearchThread(Context context, OnClickCallBack callBack, List<Message> list, String typeModel){

        this.typeModel=typeModel;
        this.callBack =callBack;
        this.hostName=context.getString(R.string.rcmsHostName);
        this.port=Integer.valueOf(context.getString(R.string.rcmsHostPort));
        this.list=list;
    }
    @Override
    public void run() {
        String jsStr="";
        super.run();
        try {
            socket=new Socket(hostName,port);
            BufferedReader reader=new BufferedReader( new InputStreamReader( socket.getInputStream()));
              BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
              writer.write(typeModel);
                writer.write("\n");
              writer.flush();
           jsStr =reader.readLine();

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
      //  String jsStr ="{\"Model\":\"D40\",\"Instal\":\"05/01/30\",\"Warranty\":\"06/01/30\",\"Start\":\"14/09/01\",\"Stop\":\"99/99/99\",\"Customer\":\"0811501 \",\"Type\":\"PerCall\",\"Service\":\"N/A\",\"Number\":\"      \"}\n";
            JsonElement jsonElement=new JsonParser().parse(jsStr);
            jsonObject=  jsonElement.getAsJsonObject();
            Boolean  stat=  jsonObject.get("stat").getAsBoolean();
            Message msg = new Message();
            list.add(msg);
            msg.msgType = Constant.watsonType;
        android.os.Message send=new android.os.Message();
            if(stat) {
                JsonElement stope = jsonObject.get("Stop");
                if (stope != null) {
                    String str = stope.getAsString();

                    if (RcmsConstant.Not_Warranty.equals(str)) {
                        msg.setMessage("您的机器目前不在保");
                        conversationContext.put(RcmsConstant.State_Key,"不在保");
                        send.obj="不在保";
                    } else {
                        msg.setMessage("您的机器目前在保");
                        conversationContext.put(RcmsConstant.State_Key,"在保");
                        send.obj="在保";
                    }
                }
            }else{
                msg.setMessage("没有查到您要的机器信息");
                conversationContext.put(RcmsConstant.State_Key,"不存在");
                send.obj="机器不存在";
            }


            send.what=Constant.ListDataChang;
            handler.handleMessage(send);


    }


}

