package com.example.vmac.callCenter.rcms;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.example.vmac.callCenter.watson.conversation.Constant;
import com.google.gson.Gson;
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
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/10/9.
 */

public class CallInfoRunable1 implements  Runnable {
    public String intentAction;
    public Handler handler;
    public String data;
    private Map<String,Object> conversationContext;
    private CallInfoEntity callInfoEntity ;
    public String uuid="CallInfoRunable1";
    private List<com.example.vmac.callCenter.Message> messageList;

    public void setCallInfoEntity(CallInfoEntity callInfoEntity) {
        this.callInfoEntity = callInfoEntity;
    }

    public void setMessageList(List<com.example.vmac.callCenter.Message> messageList) {
        this.messageList = messageList;
    }

    public CallInfoRunable1(String intentAction, String data, Map<String,Object> conversationContext, Handler handler){
        this.intentAction=intentAction;
        this.handler=handler;
        this.data=data;
        this.conversationContext=conversationContext;
    }

    @Override
    public void run() {
        if(conversationContext==null){
            conversationContext=new HashMap<>();
        }
            if(RcmsConstant.IntentGetCallByNumber.equals(intentAction)){

                queryCall(RcmsConstant.IntentGetCallByNumber,data);

            }if(RcmsConstant.IntentGetCallfo.equals(intentAction)){
                queryCall(RcmsConstant.IntentGetCallfo,data);
            }
            else if(RcmsConstant.IntentGetCallBySerial.equals(intentAction)){
                conversationContext.remove("type");
                conversationContext.remove("SN");
              //  data=data.split("_")[1];
              StringBuffer sb =new StringBuffer(data);
//                sb.delete(2,4);
                queryCall(RcmsConstant.IntentGetCallBySerial,sb.toString());
            }
            else if(RcmsConstant.IntentGetCallByPhone.equals(intentAction)){
                conversationContext.remove("type");
                conversationContext.remove("SN");
                queryCall(RcmsConstant.IntentGetCallByPhone,data);

                //  data=data.split("_")[1];
            }
            else if(RcmsConstant.IntentAddCall.equals(intentAction)){
                if(callInfoEntity!=null){
                    addCall();
                }
            }
    }

    public void addCall(){
        Message msg =new Message();

        JsonObject jsonObject=new JsonObject();
        Gson Gson =new Gson();
        String str = Gson.toJson(callInfoEntity);
        JsonParser jsonParser =new JsonParser();
        JsonElement jsonElement = jsonParser.parse(str);
        jsonObject.addProperty(RcmsConstant.intentKey,intentAction);
        jsonObject.add(RcmsConstant.Data,jsonElement );

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

            if(result!=null&&result.length()>0){

                jsonElement =   jsonParser.parse(result);
                JsonObject resultJson=jsonElement.getAsJsonObject();
                Boolean stat=resultJson.get(RcmsConstant.stat).getAsBoolean();
                if(stat){


                    com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                    message.msgType=Constant.watsonType;
                    message.setMessage("call 编号为 "+callInfoEntity.getCallNo()+" 添加成功");
                    msg.obj=    message;
                    msg.what= Constant.handler_callInfo;
                    handler.handleMessage(msg);

                }else{
                    msg.what= Constant.handler_callInfo;
                    com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                    message.msgType=Constant.watsonType;
                    message.setMessage("call 编号为 "+callInfoEntity.getCallNo()+" 添加失败");
                    msg.obj=message;

                    handler.handleMessage(msg);
                }



            }else {
                msg.what= Constant.handler_callInfo;
                com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                message.msgType=Constant.watsonType;
                message.setMessage("call 编号为 "+callInfoEntity.getCallNo()+" 添加失败");
                msg.obj=message;

                handler.handleMessage(msg);
            }

        } catch (IOException e) {
            e.printStackTrace();
            msg.what= Constant.handler_exception;
            com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
            message.msgType=Constant.watsonType;
            message.setMessage("服务器开小差ing……");
            msg.obj=message;
            handler.handleMessage(msg);
        }


    }

    public JsonObject queryCall(String intentAction,String data){
        Message msg =new Message();

        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty(RcmsConstant.intentKey,intentAction);
        jsonObject.addProperty(RcmsConstant.Data,data);
        jsonObject.addProperty(RcmsConstant.sign,uuid);
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

            if(result!=null&&result.length()>0){
                JsonParser jsonParser =new JsonParser();
                JsonElement jsonElement =   jsonParser.parse(result);
                JsonObject resultJson=jsonElement.getAsJsonObject();
                Boolean stat=resultJson.get(RcmsConstant.stat).getAsBoolean();
                if(stat){

                    com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                    message.msgType=Constant.watsonType;
                    message.setMessage(parseJson(resultJson.getAsJsonObject(RcmsConstant.Data)));
                    msg.obj=    message;
                    msg.what= Constant.handler_callInfo;
                    conversationContext.put("Call_status","找到");
                    addMessage(message);
                    handler.handleMessage(msg);

                }else{
                    msg.what= Constant.handler_callInfo;
                    com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                    message.msgType=Constant.watsonType;
                    message.setMessage("未找到 Call　的信息");
                    msg.obj=message;
                    conversationContext.put("Call_status","未找到");
                    handler.handleMessage(msg);
                }



            }else {
                msg.what= Constant.handler_callInfo;
                com.example.vmac.callCenter.Message message = new com.example.vmac.callCenter.Message();
                message.msgType=Constant.watsonType;
//                message.setMessage("未找到Call编号为："+data.replace("_","")+" Call的信息");
                message.setMessage("未找到 Call　的信息");
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

            readItem(CallInfoConstant.callNoKey,jsonObject,CallInfoConstant.callNo,sb);

            readItem(CallInfoConstant.enduserKey,jsonObject,CallInfoConstant.enduser,sb);

            readItem(CallInfoConstant.customerNumberKey,jsonObject,CallInfoConstant.customerNumber,sb);

            readItem(CallInfoConstant.customerNameKey,jsonObject,CallInfoConstant.customerName,sb);

            readItem(CallInfoConstant.telephoneKey,jsonObject,CallInfoConstant.telephone,sb);

            readItem(CallInfoConstant.machineTypeKey,jsonObject,CallInfoConstant.machineType,sb);

            readItem(CallInfoConstant.ibmEngineerKey,jsonObject,CallInfoConstant.ibmEngineer,sb);

           readItem(CallInfoConstant.ibmEngeineerSnKey,jsonObject,CallInfoConstant.ibmEngeineerSn,sb);

            readItem(CallInfoConstant.engineerTelephoneKey,jsonObject,CallInfoConstant.engineerTelephone,sb);

            readItem(CallInfoConstant.faultPartKey,jsonObject,CallInfoConstant.faultPart,sb);

            readItem(CallInfoConstant.faultDetailKey,jsonObject,CallInfoConstant.faultDetail,sb);

            readItem(CallInfoConstant.levelKey,jsonObject,CallInfoConstant.level,sb);

//            readItem(CallInfoConstant.engineerTelephoneKey,jsonObject,CallInfoConstant.engineerTelephone,sb);



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
        private void addMessage(com.example.vmac.callCenter.Message mesg){
            if(messageList!=null) {
                messageList.add(mesg);
            }
        }
}
interface  CallInfoConstant{




    public static String idKey="id";
    public static String callNoKey="callNo"; //call编号
    public static String callNo="call编号";
    public static String customerAddressKey="customerAddress"; //客户地址
    public static String customerAddress="客户地址";

    public static String enduserKey="enduser"; //客户名称
    public static String enduser="报call人";

    public static String customerNameKey="customerName"; //报call人
    public static String customerName="客户名称";
    public static String telephoneKey="telephone";//客户电话
    public static String telephone="客户电话";

    public static String customerNumberKey="customerNumber";//客户编码
    public static String customerNumber="客户编码";
    public static String machineTypeKey="machineType"; //机器类型
    public static String machineType="机器类型";
    public static String ibmEngineerKey="ibmEngineer";//ibm工程工程师
    public static String ibmEngineer="ibm工程工程师";
    public static String ibmEngeineerSnKey="ibmEngeineerSn";//ibm工程师编号
    public static String ibmEngeineerSn="工程师编号";

    public static String   engineerTelephoneKey="engineerTelephone";
    public static String  engineerTelephone="工程师电话";

    public static String  faultPartKey ="faultPart";
    public static String  faultPart="故障备件";

    public static String  remarkKey ="remark";
    public static String   remark="其他备注";

    public static String  levelKey ="level";
    public static String   level="紧急程度";

    public static String faultDetailKey="faultDetail";
    public static String faultDetail="故障说明";



}