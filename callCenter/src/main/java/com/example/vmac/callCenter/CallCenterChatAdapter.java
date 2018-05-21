package com.example.vmac.callCenter;

/**
 * Created by VMac on 17/11/16.
 */

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vmac.callCenter.app.WatsonApplication;
import com.example.vmac.callCenter.rcms.RcmsConstant;
import com.example.vmac.callCenter.watson.conversation.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.vmac.callCenter.watson.conversation.Constant.show_chat_item_show_callinfoActivity;


public class CallCenterChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener{
    private String TAG=CallCenterChatAdapter.class.getSimpleName();
    private List<String> playList;
    private String intent;
    private Activity activity;
    public String getIntent() {
        return intent;
    }

    private int SELF = 100;


    public static  final String debug="debug";
    public static final  String isChoise="Choise";
    public Handler handler;

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    private ArrayList<Message> messageArrayList;

    private Context appContext;

    private OnClickCallBack callBack;
    private Map<String,Object> conversationContext;


    public void setConversationContext(Map<String, Object> conversationContext) {
        this.conversationContext = conversationContext;
    }

    public CallCenterChatAdapter(ArrayList<Message> messageArrayList, OnClickCallBack listener, Context context, Activity activity) {
        this.messageArrayList=messageArrayList;
        callBack=listener;
        this.appContext=context;
        this.activity =activity;
    }

    public void setPlayList(List<String> playList) {
        this.playList = playList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        Log.i(TAG,"***onCreateViewHolder viewType"+viewType);
        // view type is to identify where to render the chat message
        // left or right

        if (viewType == Constant.customType) {
            // self message 我的对话内容
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_self, parent, false);

        }
        else  if(viewType==Constant.show_bt_radio_intent){ //功能框

            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_fucntion_choise, parent, false);
            View v_call = itemView.findViewById(R.id.chat_item_functon_call);

            View create_call = itemView.findViewById(R.id.chat_item_functon_create_call);

            View v_repair= itemView.findViewById(R.id.chat_item_functon_repair);
            View v_local= itemView.findViewById(R.id.chat_item_functon_local);
            View v_part = itemView.findViewById(R.id.chat_item_functon_part);
            View v_src=itemView.findViewById(R.id.chat_item_functon_src) ;
            v_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(1,"工单查询");
                }
            });
            create_call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(1,"我要开call ");

                }
            });

            v_repair.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(1,"维保查询");
                }
            });

            v_local.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(1,"位置查询");
                }
            });

            v_part.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(1,"备件查询");
                }
            });

            v_src.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(1,"机器故障代码查询");
                }
            });
        }
        /*
            维保查询的
         */
        else  if(viewType==Constant.show_typeSNInputBox){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_input_orderbill, parent, false);
        }
        else  if(viewType==Constant.show_wansont_single_img){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_watson_singl_img, parent, false);
        }

        /*
        *  callinfo 查询
        * */

//        else  if(viewType==Constant.show_callNo){
//            itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.main_menu_popup.chat_item_input_callnumber, parent, false);
//        }
//        else  if(viewType==Constant.show_input_callinfo_phone) {
//            itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.main_menu_popup.chat_item_input_phone_callinfo, parent, false);
//
//        }
        else  if(viewType==Constant.show_callNo){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_input_callinfo_queryparam, parent, false);
         final    EditText et_param = (EditText) itemView.findViewById(R.id.editText_callinfo_query_param);

           View view = itemView.findViewById(R.id.bt_query_callinfo);
           view.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   String param = et_param.getText().toString().trim();
                   if(param.length()<7){
                       Toast.makeText(appContext,"输入的callno,机器列号，或电话 位数 不足",Toast.LENGTH_LONG).show();
                       return;
                   }
                    param  =  param.replace(" ","");

                   callBack.OnCallBack(Constant.check_callInfo_query,param);
               }
           });

        }


        else  if(viewType==Constant.show_machin_power_no){
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_input_machin_pwr, parent, false);
        }
         else  if(viewType==Constant.web_local_link||viewType==Constant.web_part_link){
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.chat_item_emty_line, parent, false);

        }else  if(viewType==Constant.show_chat_srcbox) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_input_src, parent, false);
        }
        else  if(viewType==Constant.chat_item_src_img) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_web_img, parent, false);

        }else  if(viewType==Constant.chat_item_power_start_web) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_emty_line, parent, false);

        }
        else  if(viewType==Constant.chat_item_show_ratingbar) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_ratingbar, parent, false);

        }else  if(viewType==Constant.chat_item_show_call_or_send) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_choies_call_box, parent, false);

        }else  if(viewType== show_chat_item_show_callinfoActivity) {
            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.chat_item_button_choise_callinfoactivity, parent, false);

        }


        else {

        // WatBot message watSon 对话的内容
        itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_item_watson, parent, false);
            TextView textView =(TextView) itemView.findViewById(R.id.chat_item_watson_message);
            itemView .setOnClickListener(this);
            AppCompatImageView imageView = (AppCompatImageView) itemView.findViewById(R.id.appCompatImageView);
            textView.setOnClickListener(this);
         }



        return new ViewHolder(itemView);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageArrayList.get(position);
        return  message.msgType;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder,final int position) {
        Log.i(TAG,"*** onBindViewHolder position "+position);
        final Message message = messageArrayList.get(position);
        final View itemView = holder.itemView;
        if(message.msgType==Constant.watsonType){
           TextView textView = (TextView) itemView.findViewById(R.id.chat_item_watson_message);
            textView.setText(message.message);

//            ViewHolder item=((ViewHolder) holder);
//            item.message.setText(message.getMessage());
//            if(message.msgType==Constant.watsonType){
//                item.message.setOnClickListener(new View.OnClickListener(){
//                    @Override
//                    public void onClick(View v) {
//                        callBack.OnCallBack(Constant.start_speeker,message.message);
//                    }
//                });
//            }
        }else if(message.msgType==Constant.customType){
            TextView textView = (TextView) itemView.findViewById(R.id.chat_item_custom_message);
            textView.setText(message.message);
        }

        else if(message.msgType==Constant.show_bt_radio_intent){



        }
        //
        else  if(message.msgType==Constant.show_typeSNInputBox){

            final   TextView bt_source= (TextView) itemView.findViewById(R.id.bt_source);

            final TextView bt_how = (TextView) itemView.findViewById(R.id.bt_who_to_getSn);



            bt_how.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(1,  bt_how.getText().toString());
                }
            });

            bt_source.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    View layoutLine= (View) v.getParent().getParent();
                    EditText type= (EditText) layoutLine.findViewById(R.id.editText_type);
                    type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus){
                                EditText temp = (EditText) v;//
                               String tems=  temp.getText().toString().toUpperCase();
                                temp.setText(tems);
                            }
                        }
                    });
                    EditText SN = (EditText) layoutLine.findViewById(R.id.editText_SN);
                    SN.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus){
                                EditText temp = (EditText) v;
                                String tems=  temp.getText().toString().toUpperCase();
                                temp.setText(tems);
                            }
                        }
                    });


                    String types=type.getText().toString();
                    String sns=SN.getText().toString();
                    StringBuilder error=new StringBuilder();
                    if(TextUtils.isEmpty(types)||types.length()!=4){
                        error.append("型号的长度是4位 ,");
                    }
                    if(TextUtils.isEmpty(sns)||sns.length()!=7){
                        error.append("序列号的长度是7位 ");
                    }
                    if(error.length()>0){

                        Toast.makeText(appContext,error.toString(),Toast.LENGTH_LONG).show();
                    }else{
                        messageArrayList.remove(messageArrayList.size()-1);

                        String ty=types.toUpperCase();
                        String s=sns.toUpperCase();

                        Message msg =new Message();
                        msg.msgType=Constant.customType;
                        msg.setMessage("查询机器"+ty+s);
                        messageArrayList.add(msg);

                        conversationContext=((WatsonApplication)appContext.getApplicationContext()).getWatsonClient().getConversationContext();
                        Object action = conversationContext.get(Constant.ACTION);

                        conversationContext.put("type",ty);
                        conversationContext.put("SN",s);
                        msg =new Message();
//                        if(action!=null&&Constant.action_Value_call.equals(action)){
                            //查询callinfo
//                            messageArrayList.remove(position);
//                            String typeSeril=ty+s;
//                            StringBuilder sb = new StringBuilder(s);
//                            sb.insert(2,0);
//                            sb.insert(2,0);
//                            sb.insert(0,"_");
//                            sb.insert(0,ty);
//                            callBack.OnCallBack(Constant.check_callInfo_serial, sb.toString());
//                        }else{
                            msg.msgType=Constant.watsonType;
                            msg.setMessage("正在为您查询机器信息");
                            messageArrayList.add(msg);
                            playList.add(msg.message);
                            callBack.OnCallBack(Constant.check_type_SN,ty+s);//查询
//                        }

                    }
                }
            });
        }else  if(message.msgType==Constant.show_wansont_single_img){
            //so something
        }else  if(message.msgType==Constant.show_callNo){

            {//原查询callInfo
//                TextView bt_source = (TextView) itemView.findViewById(R.id.bt_source);
//                bt_source.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        View layoutLine = (View) v.getParent().getParent();
//                        EditText Text_callnumber = (EditText) layoutLine.findViewById(R.id.editText_callnumber);
//                        String callnumber = Text_callnumber.getText().toString();
//                        if (TextUtils.isEmpty(callnumber) || callnumber.length() != 7) {
//                            Toast.makeText(appContext, "call No 为7位 ", Toast.LENGTH_LONG).show();
//                        } else {
//                            messageArrayList.remove(position);
//                            callBack.OnCallBack(Constant.check_callInfo_callNo, callnumber);
//                        }
//                    }
//                });
//
//                final TextView bt_who_to_getSn = (TextView) itemView.findViewById(R.id.bt_who_to_getSn);
//                bt_who_to_getSn.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        messageArrayList.remove(position);
//                        Message sendMessage = new Message();
//                        sendMessage.msgType = Constant.customType;
//                        messageArrayList.add(sendMessage);
//                        sendMessage.setMessage("通过机器型号序列号查询");
//                        callBack.OnCallBack(Constant.send_wansot_massge, "通过机器型号序列号查询");
//                    }
//                });
//
//
//                final TextView bt_get_byphone = (TextView) itemView.findViewById(R.id.bt_who_to_byphone);
//
//                //
//                bt_get_byphone.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
////                    callBack.OnCallBack(1,  bt_get_byphone.getText().toString());
//                        //删除
//                        messageArrayList.remove(position);
//                        Log.i(TAG, "move message ");
//                        //添加一个信息
//                        Message amsg = new Message();
//                        amsg.msgType = Constant.customType;
//                        amsg.setMessage("通过电话号码查询");
//                        messageArrayList.add(amsg);
//
//
//                        amsg = new Message();
//                        amsg.msgType = Constant.watsonType;
//                        amsg.setMessage("请输入11位电话号码");
//                        messageArrayList.add(amsg);
//                        playList.add(amsg.message);
//
//                        amsg = new Message();
//                        amsg.msgType = Constant.show_input_callinfo_phone;
//                        amsg.setMessage("请输入11位电话号码");
//                        messageArrayList.add(amsg);
//
//                        callBack.OnCallBack(Constant.ListDataChang, "");
//
//                    }
//                });
            }

        }else  if(message.msgType==Constant.show_machin_power_no){
            TextView bt_source= (TextView) itemView.findViewById(R.id.bt_source);
            bt_source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View layoutLine= (View) v.getParent().getParent();
                    EditText Text_pwr_no= (EditText) layoutLine.findViewById(R.id.editText_pwr_no);
                    String _pwr_no=Text_pwr_no.getText().toString();
                    if(TextUtils.isEmpty(_pwr_no)||_pwr_no.length()!=8){
                        Toast.makeText(appContext,"power No 为8位 ",Toast.LENGTH_LONG).show();
                    }else{
                        messageArrayList.remove(position);
                        callBack.OnCallBack(Constant.find_link,_pwr_no);
                    }
                }
            });
        }else  if(message.msgType==Constant.web_local_link||message.msgType==Constant.web_part_link) {

        }else  if(message.msgType==Constant.show_chat_srcbox) {
            TextView bt_source = (TextView) itemView.findViewById(R.id.bt_source);
            bt_source.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  EditText editText= (EditText) itemView.findViewById(R.id.editText_srcNumber);
                    String srcNumer=editText.getText().toString();
                    if(TextUtils.isEmpty(srcNumer)||srcNumer.length()!=8){
                        Toast.makeText(appContext,"报错代码 为8位 ",Toast.LENGTH_LONG).show();
                    }else{
                        messageArrayList.remove(position);
                        Message msg = new Message();
                        msg.msgType=Constant.customType;
                        msg.setMessage("查询:"+srcNumer);
                        messageArrayList.add(msg);
                        callBack.OnCallBack(Constant.show_chat_srcbox,srcNumer);
                    }

                }
            });
        }else  if(message.msgType==Constant.chat_item_src_img) {
              ImageView imageView = (ImageView) itemView.findViewById(R.id.chat_item_web_imgaview);
                imageView.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                callBack.OnCallBack(Constant.callBack_start_web,message.getMessage());
                            }
                        }
                );
        }else  if(message.msgType==Constant.chat_item_power_start_web) {
           ImageView imageView = (ImageView) itemView.findViewById(R.id.chat_item_web_imgaview);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callBack.OnCallBack(Constant.callBack_start_web,message.getMessage());
                }
            });
        }else  if(message.msgType==Constant.chat_item_show_ratingbar) {


             View view_good=   itemView.findViewById(R.id.bt_rating_very_good);
            view_good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  //  Toast.makeText(appContext," 非常好 ",Toast.LENGTH_SHORT).show();
                    choiseEvaluate(" 非常好 ",position);

                }
            });

            View good=   itemView.findViewById(R.id.bt_rating_good);
            good.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //Toast.makeText(appContext," 满意 ",Toast.LENGTH_SHORT).show();
                    choiseEvaluate(" 满意 ",position);
                }
            });
            View bad=   itemView.findViewById(R.id.bt_rating_bad);
            bad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                  //  Toast.makeText(appContext," 不好 ",Toast.LENGTH_SHORT).show();
                    choiseEvaluate(" 不好 ",position);
                }
            });

        }else  if(message.msgType==Constant.chat_item_show_call_or_send) {
            String title=message.optional!=null?message.optional.toString():appContext.getString(R.string.callcenter_nubmer);

            TextView tv_call_title= (TextView) itemView.findViewById(R.id.chat_item_telelCall_text);
            tv_call_title.setText(title);

          View call=  itemView.findViewById(R.id.chat_item_call);
            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 //   Toast.makeText(appContext," 打电话 ",Toast.LENGTH_SHORT).show();
                    String phone=message.message!=null?message.message:appContext.getString(R.string.callcenter_nubmer);
                   callBack.OnCallBack(Constant.callbackTocallphone,phone);
                }
            });
           View noRepair=  itemView.findViewById(R.id.chat_item_no_repair);
            noRepair.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  //  Toast.makeText(appContext," 无需保修 ",Toast.LENGTH_SHORT).show();

                    callBack.OnCallBack(1,appContext.getString(R.string.no_repair));
                }
            });
        }else  if(message.msgType== Constant.show_chat_item_show_callinfoActivity) {
            View view = itemView.findViewById(R.id.bt_go_createCall);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
             //       Toast.makeText(appContext," 打开activity 添加Call ",Toast.LENGTH_SHORT).show();
                    callBack.OnCallBack(Constant.show_chat_item_show_callinfoActivity,"打开activity 添加Call ");

                }
            });
        }
        //原查询callinfo
//        else  if(message.msgType==Constant.show_input_callinfo_phone) {
//
//            final EditText view_phone = (EditText) itemView.findViewById(R.id.editText_phone_phone);
//            View bt_surce=itemView.findViewById(R.id.bt_source);
//            bt_surce.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String phone=view_phone.getText().toString();
//                    if(phone.length()==11){
//                        messageArrayList.remove(position);
//                        callBack.OnCallBack(Constant.check_callInfo_byphone,phone);
//
//                    }else{
//                        Toast.makeText(appContext," 电话号码为11位 ",Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//        }

        }

    @Override
    public int getItemCount() {
            return messageArrayList.size();
    }

    private void choiseEvaluate(String evaluate,int position){
            messageArrayList.remove(position);

            Message rmsg =new Message();
            rmsg.msgType = Constant.customType;
            rmsg.setMessage(evaluate);
            messageArrayList.add(rmsg);
            notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View view) {
            super(view);
//            message = (TextView) itemView.findViewById(R.id.message);
        }
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG,"***点击事件");
        switch (v.getId()){
            case R.id.chat_item_watson_message:
                String text =((TextView)v).getText().toString();
                Log.i(TAG,"播放文本 "+text);
                callBack.OnCallBack(Constant.start_speeker,text);
                break;
        }

    }

}