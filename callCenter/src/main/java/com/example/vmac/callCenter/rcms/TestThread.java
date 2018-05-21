package com.example.vmac.callCenter.rcms;

import android.content.Context;
import android.os.Handler;

import com.example.vmac.callCenter.Message;
import com.example.vmac.callCenter.OnClickCallBack;
import com.example.vmac.callCenter.R;
import com.example.vmac.callCenter.watson.conversation.Constant;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.Socket;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/24.
 */

public class TestThread extends Thread {
    JsonObject jsonObject;
    private String hostName;
    private int port;
    private Context context;
    private Socket socket;
    private String typeModel;
    private Handler handler;
    OnClickCallBack callBack;
    private List<Message> list;
    private Map<String,Object> conversationContext;

    public void setConversationContext(Map<String, Object> conversationContext) {
        this.conversationContext = conversationContext;
    }

    public TestThread(Context context, OnClickCallBack callBack, List<Message> list, String typeModel){

        this.typeModel=typeModel;
        this.callBack =callBack;
        this.hostName=context.getString(R.string.rcmsHostName);
        this.port=Integer.valueOf(context.getString(R.string.rcmsHostPort));
        this.list=list;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        String jsStr="";
        super.run();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        try {
//            socket=new Socket(hostName,port);
//            BufferedReader reader=new BufferedReader( new InputStreamReader( socket.getInputStream()));
//            BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
//            writer.write(typeModel);
//            writer.write("\n");
//            writer.flush();
//            jsStr =reader.readLine();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return;
//        }
       jsStr ="{\"stat\":\"true\",\"Model\":\"D40\",\"Instal\":\"05/01/30\",\"Warranty\":\"06/01/30\",\"Start\":\"14/09/01\",\"Stop\":\"18/45/99\",\"Customer\":\"0811501 \",\"Type\":\"PerCall\",\"Service\":\"N/A\",\"Number\":\"      \"}\n";
        JsonElement jsonElement=new JsonParser().parse(jsStr);
        jsonObject=  jsonElement.getAsJsonObject();
        Boolean  stat=  jsonObject.get("stat").getAsBoolean();
        Message msg = new Message();
        msg.msgType = Constant.watsonType;
        android.os.Message osMsg = new android.os.Message();
//        if(stat) {
//            JsonElement stope = jsonObject.get("Stop");
//            if (stope != null) {
//                String str = stope.getAsString();
//                if (RcmsConstant.Not_Warranty.equals(str)) {
//                    msg.setMessage("您的机器目前不在保");
//                    conversationContext.put(RcmsConstant.State_Key,"不在保");
//                    osMsg.obj="不在保";
//                } else {
//                    msg.setMessage("您的机器目前在保");
//                    conversationContext.put(RcmsConstant.State_Key,"在保");
//                    osMsg.obj="在保";
//                }
//            }
//        }else{
//            msg.setMessage("没有查到您要的机器信息");
//        }

        {
            if(!"123413K1KKR".equals(typeModel)) {
                JsonElement stope = jsonObject.get("Stop");
                if ("172613K1KKR".equals(typeModel)) {
//                    String str = stope.getAsString();
//                    if (RcmsConstant.Not_Warranty.equals(str)) {
                        msg.setMessage("您的机器目前不在保");
                        conversationContext.put(RcmsConstant.State_Key,"不在保");
                        osMsg.obj="不在保";
//                    } else {
//                        msg.setMessage("您的机器目前在保");
//                        conversationContext.put(RcmsConstant.State_Key,"在保");
//                        osMsg.obj="在保";
//                    }
                }else{
                    msg.setMessage("您的机器目前在保");
                    conversationContext.put(RcmsConstant.State_Key,"在保");
                    osMsg.obj="在保";
                }
            }else{
                msg.setMessage("没有查到您要的机器信息");
                conversationContext.put(RcmsConstant.State_Key,"不存在");
                osMsg.obj="机器不存在";
            }
        }
        list.add(msg);
     //   callBack.OnCallBack(Constant.ListDataChang, "更新状态");

        osMsg.what=Constant.ListDataChang;

       handler.handleMessage(osMsg);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}
