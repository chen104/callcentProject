package com.example.vmac.callCenter.watson.conversation;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.vmac.callCenter.Message;
import com.example.vmac.callCenter.OnClickCallBack;
import com.example.vmac.callCenter.R;
import com.example.vmac.callCenter.rcms.CallcenterService;
import com.example.vmac.callCenter.rcms.RcmsClient;
import com.example.vmac.callCenter.rcms.RcmsConstant;
import com.google.gson.JsonObject;
import com.ibm.watson.developer_cloud.conversation.v1.Conversation;
import com.ibm.watson.developer_cloud.conversation.v1.model.InputData;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageOptions;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2017/8/16.
 */

public class WatsonClient {
    private String TAG=WatsonClient.class.getSimpleName();
    private Context appContext;
    public  Map<String,Object> conversationContext=null;
    private String workspace_id;
    private String conversation_username;
    private String conversation_password;
    private String STT_username;
    private String STT_password;
    private String TTS_username;
    private String TTS_password;
    private String analytics_APIKEY;
    public Conversation conversationService;
    private RcmsClient rcmsClient;
    private ResponseHandler responseHandler;
    private String objAction;
    private String objShow;

    private MessageFilter filter=new MessageFilter();
    public Map<String, Object> getConversationContext() {
        return conversationContext;
    }
    private CallcenterService callcenterSerivce;
    private OnClickCallBack callBack;

    public void setCallcenterSerivce(CallcenterService callcenterSerivce) {
        this.callcenterSerivce = callcenterSerivce;
    }

    public void setCallBack(OnClickCallBack callBack) {
        this.callBack = callBack;
    }

    private List<Message> resList=new ArrayList<Message>();//wat
    public WatsonClient(Context mContext){
        rcmsClient=new RcmsClient();
        callcenterSerivce = new CallcenterService();
        this.appContext=mContext;
        conversation_username = mContext.getString(R.string.conversation_username);
        conversation_password = mContext.getString(R.string.conversation_password);
        workspace_id = mContext.getString(R.string.workspace_id);
        STT_username = mContext.getString(R.string.STT_username);
        STT_password = mContext.getString(R.string.STT_password);
        TTS_username = mContext.getString(R.string.TTS_username);
        TTS_password = mContext.getString(R.string.TTS_password);
        analytics_APIKEY = mContext.getString(R.string.mobileanalytics_apikey);
        responseHandler=new CallCenterHanlder();
        //init
        conversationService =new Conversation(Conversation.VERSION_DATE_2017_05_26);
        conversationService.setUsernameAndPassword(conversation_username,conversation_password);

    }

    public void setResponseHandler(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
    }

    public List<Message> sendMessage(String inputMsg) {
        resList.clear();


        //构建请求信息对象
      //  conversationContext.remove("action");//清理上次上下文，可选
//        inputMsg = replaceStr(inputMsg);
        InputData input = new InputData.Builder(inputMsg).build();
        com.ibm.watson.developer_cloud.conversation.v1.model.Context context=null;
        if(conversationContext!=null){
            context = (com.ibm.watson.developer_cloud.conversation.v1.model.Context) conversationContext;


            Log.i(TAG,"发送Context："+conversationContext.toString());
        }
        Log.i(TAG,"发送信息："+inputMsg);
        MessageOptions newMessage= new MessageOptions.Builder(  workspace_id).input(input).context(context).build();

      // MessageRequest newMessage = new MessageRequest.Builder().inputText(inputMsg).context(conversationContext).build();

        //发送信息到Watson conversation服务中
        MessageResponse response=null;
        try {
           response = conversationService.message(newMessage).execute();
           if(response==null){
               Log.i(TAG,"response is null");
           }

        }catch (Exception e){
            Log.i(TAG,"***"+e.getMessage());
            e.printStackTrace();
           List<Message> list =  new ArrayList<Message>();

            Message ms=new Message();
            ms.msgType=Constant.watsonType;
            ms.setMessage("服务异常");
            list.add(ms);
            return list;
        }
        //Passing Context of last conversation
        //获得结果
        if (response.getContext() != null) {

            conversationContext=response.getContext();
            Object obj = conversationContext.get(Constant.ACTION);
            if(obj!=null){
                objAction = obj.toString();
            }


            Log.i(TAG,"*** action "+objAction);
            Log.i(TAG,"*** backe"+conversationContext);
        }

        List<Message> list= responseHandler.hadndleResponse(response, conversationContext );
        Log.i(TAG,"***response "+list);
         return list;
    }


    public List<Message> sendMessage(String inputMsg, final Handler handler) {
        resList.clear();
        inputMsg = inputMsg.trim();
        if(Constant.action_Value_call.equals(objAction)){

           MessageFilter.MatcherResulter re= filter.isMatcherCallInfoParam(inputMsg);
           if(re.isMatcher) {
               Message message = new Message();
               message.msgType = Constant.watsonType;
               message.setMessage("正在为你查找call 信息……");
               resList.add(message);
               callBack.OnCallBack(Constant.check_callInfo_query, re.matcherString);
               return resList;
           }
        }else if(Constant.action_Machin_State.equals(objAction)){
            MessageFilter.MatcherResulter re= filter.isMatcherMachinTypeAndMachinSn(inputMsg);
            if(re.isMatcher) {
                Message message = new Message();
                message.msgType = Constant.watsonType;
                message.setMessage("正在为你查找机器的信息……");
                resList.add(message);
                callBack.OnCallBack(Constant.check_type_SN, re.matcherString);
                return resList;
            }
        }else  if(Constant.action_local_values.equals(objAction)){
            MessageFilter.MatcherResulter re= filter.isMatcherMachinTypeAndMachin(inputMsg);
            if(re.isMatcher) {
                Message message = new Message();
                message.msgType = Constant.watsonType;
                message.setMessage("正在为你查找localtion的信息……");
                resList.add(message);
                callBack.OnCallBack(Constant.check_power_local, re.matcherString);
                return resList;
            }
        }
        else  if(Constant.action_part_values.equals(objAction)){
            MessageFilter.MatcherResulter re= filter.isMatcherMachinTypeAndMachin(inputMsg);
            if(re.isMatcher) {
                Message message = new Message();
                message.msgType = Constant.watsonType;
                message.setMessage("正在为你查找part的信息……");
                resList.add(message);
                callBack.OnCallBack(Constant.check_power_local,  re.matcherString);
                return resList;
            }
        } else  if(Constant.action_src_values.equals(objAction)){
            MessageFilter.MatcherResulter re= filter.isMatcherSRCCode(inputMsg);
            if(re.isMatcher) {
                Message message = new Message();
                message.msgType = Constant.watsonType;
                message.setMessage("正在为你查找报错代码的信息……");
                resList.add(message);
                callBack.OnCallBack(Constant.show_chat_srcbox,  re.matcherString);
                return resList;
            }
        }


        //构建请求信息对象
        //  conversationContext.remove("action");//清理上次上下文，可选
//        inputMsg = replaceStr(inputMsg);
        InputData input = new InputData.Builder(inputMsg).build();
        com.ibm.watson.developer_cloud.conversation.v1.model.Context context=null;
        if(conversationContext!=null){
            context = (com.ibm.watson.developer_cloud.conversation.v1.model.Context) conversationContext;
        }
        Log.i(TAG,"发送信息："+inputMsg);
        MessageOptions newMessage= new MessageOptions.Builder(  workspace_id).input(input).context(context).build();

        // MessageRequest newMessage = new MessageRequest.Builder().inputText(inputMsg).context(conversationContext).build();

        //发送信息到Watson conversation服务中
        MessageResponse response=null;
        try {
            response = conversationService.message(newMessage).execute();
            if(response==null){
                Log.i(TAG,"response is null");
            }

        }catch (Exception e){
            Log.i(TAG,"***"+e.getMessage());
            e.printStackTrace();
            List<Message> list =  new ArrayList<Message>();

            Message ms=new Message();
            ms.msgType=Constant.watsonType;
            ms.setMessage("服务异常");
            list.add(ms);
            return list;
        }
        //Passing Context of last conversation
        //获得结果
        if (response.getContext() != null) {

            conversationContext=response.getContext();
            Object obj = conversationContext.get(Constant.ACTION);
            if(obj!=null){
                objAction = obj.toString();
            }
            obj = conversationContext.get(Constant.Show);
            if(obj!=null){
                objShow=obj.toString();
                if(Constant.show_chat_item_show_callinfo_activity.equals(objShow)){
                    new Thread(){
                        @Override
                        public void run() {
                            try {
                                sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            android.os.Message message = new android.os.Message();
                            callBack.OnCallBack(Constant.callBack_add_callinfo,"添加");
                            super.run();
                        }
                    }.start();
                }
            }

            Log.i(TAG,"*** action "+objAction);

        }

        List<Message> list= responseHandler.hadndleResponse(response, conversationContext );
        Log.i(TAG,"***response "+list);
        return list;
    }



    public String speechToText(){
        return  "";
    }

    public static String replaceStr(String input){
        String pattern = "[1-9][0-9]*万?+-[1-9][0-9]万";
        String  subPattern="[1-9][0-9]*万";
        // 创建 Pattern 对象
        Pattern r = Pattern.compile(pattern);
        Matcher matcher=  r.matcher(input);
        String subStr="";

        while(matcher.find()){
            subStr= matcher.group();
            Pattern subPath=Pattern.compile(subPattern);
            Matcher mat= subPath.matcher(subStr);
            String subNum="";
            while(mat.find()){
                subNum=	mat.group();
            }
            input=  input.replace(subStr, subNum);
        }
        return input;
    }

    public JsonObject searchTypeSN(String typeSN){
        JsonObject json = rcmsClient.getMsg(typeSN);
        String stop =json.get("Stop").getAsString();
        if("99/99/99".equals(stop)){
            json.addProperty(RcmsConstant.isWarranty,false);
        }else{
            json.addProperty(RcmsConstant.isWarranty,true);
        }
        return  json;
    }

}
