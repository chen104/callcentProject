package com.example.vmac.callCenter.watson.conversation;

import android.content.Context;
import android.text.TextUtils;

import com.example.vmac.callCenter.Message;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/9/21.
 */

public class CallCenterHanlder implements ResponseHandler{


    @Override
    public  List<Message>  hadndleResponse(MessageResponse response, Map<String, Object> context) {
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
                        }

                    }

                    if(bj!=null){
                        String actionType=bj.toString();

                        if(Constant.show_bt_radio_TypeModel.equals(actionType)){
                            Message msg=new Message();
                            msg.setMessage("选择意图");
                            msg.msgType=Constant.show_bt_radio_intent;
                            resList.add(msg);
                        }
                        else if(Constant.showTypeSNInputBox.equals(actionType)) {
                            Message msg = new Message();
                            msg.setMessage("输入type sn");
                            msg.msgType = Constant.show_typeSNInputBox;
                            resList.add(msg);
                        }
                        else if(Constant.showWanstSingleValues.equals(actionType)) {
                            Message msg = new Message();
                            msg.setMessage("输入type sn");
                            msg.msgType = Constant.show_wansont_single_img;
                            resList.add(msg);
                        }
                        else if(Constant.showCallNoInput.equals(actionType)) {
                            Message msg = new Message();
                            msg.setMessage("输入CallNo");
                            msg.msgType = Constant.show_callNo;
                            resList.add(msg);
                        }else if(Constant.show_mathinType_input.equals(actionType)) {
                            Message msg = new Message();
                            msg.setMessage("输入8位型号");
                            msg.msgType = Constant.show_machin_power_no;
                            resList.add(msg);
                        }
                        else if(Constant.show_context_src_boxInput.equals(actionType)) {
                            Message msg = new Message();
                            msg.setMessage("输入8位型号");
                            msg.msgType = Constant.show_chat_srcbox;
                            resList.add(msg);
                        }
                        else if(Constant.show_chat_item_ratingbar_key.equals(actionType)) {
                            Message msg = new Message();
                            msg.setMessage("显示评分");
                            msg.msgType = Constant.chat_item_show_ratingbar;
                            resList.add(msg);
                        } else if(Constant.show_chat_item_choise_call_chat.equals(actionType)) {

                            Message msg1 = new Message();
                           // msg1.setMessage("拨打电话");
                            msg1.msgType = Constant.chat_item_show_call_or_send;
                            Object obt_phone=response.getContext().get(Constant.show_chat_item_telephone_key);

                            if(obt_phone!=null) {
                                msg1.setMessage(obt_phone.toString());
                            }

                            Object obt_title=response.getContext().get(Constant.show_chat_item_telephone_title);
                            if(obt_title != null){
                                msg1.setOptional(obt_title);
                            }

                            resList.add(msg1);
                        }else if(Constant.show_chat_item_show_callinfo_activity.equals(actionType)) {

                            Message msg1 = new Message();
                            msg1.setMessage("添加call");
                            msg1.msgType = Constant.show_chat_item_show_callinfoActivity;
                            resList.add(msg1);
                        }


                    }

                }

            }
        }
        return resList;
    }
}
