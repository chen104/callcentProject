package com.example.vmac.callCenter.watson.conversation;

import com.example.vmac.callCenter.Message;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/16.
 */

public interface ResponseHandler {
        List<Message>  hadndleResponse(MessageResponse response, Map<String,Object> context);

}
