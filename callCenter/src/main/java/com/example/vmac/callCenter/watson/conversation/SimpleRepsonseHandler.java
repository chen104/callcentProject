package com.example.vmac.callCenter.watson.conversation;

import android.text.TextUtils;

import com.example.vmac.callCenter.Message;
import com.example.vmac.callCenter.rcms.CallcenterService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/16.
 */

public class SimpleRepsonseHandler implements ResponseHandler {
    private ResponseHandler nextReponseHandler;
    CallcenterService service =new CallcenterService();

    public List<Message> hadndleResponse(MessageResponse response, Map<String,Object> context) {
        List<Message> resList=new ArrayList<>();
        Message outMessage;
        if (response != null) {
            if (response.getOutput() != null && response.getOutput().containsKey("text")) {
                ArrayList responseList = (ArrayList) response.getOutput().get("text");
                Object bj=response.getContext().get(Constant.Show);
                Object obj_do=context.get(Constant.action_key_do);
                if (null != responseList && responseList.size() > 0) {
                    for(int i=0;i<responseList.size();i++){
                        outMessage=new Message();
                        if(TextUtils.isEmpty(responseList.get(i).toString())){
                            continue;
                        }
                        outMessage.setMessage(responseList.get(i).toString());
                        outMessage.setId("2");
                        resList.add(outMessage);
                        outMessage.msgType=Constant.watsonType;
                        if(bj!=null){
                            String actionType=bj.toString();
                            outMessage.action=actionType;
                                if(Constant.show_bt_radio_TypeModel.equals(actionType)) {
                                    Message msg = new Message();
                                    msg.setMessage("选择意图");
                                    msg.msgType = Constant.show_bt_radio_intent;
                                    resList.add(msg);
                                }else  if(Constant.show_chat_item_ratingbar_key.equals(actionType)) {
                                    Message msg = new Message();
                                    msg.setMessage("显示评分");
                                    msg.msgType = Constant.chat_item_show_ratingbar;
                                    resList.add(msg);
                                }

                        }



                    }

                }

            }
        }
        return resList;
    }


}
