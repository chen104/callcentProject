package com.example.vmac.callCenter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.vmac.callCenter.app.WatsonApplication;
import com.example.vmac.callCenter.iflytek.result.speech.util.JsonParser;
import com.example.vmac.callCenter.rcms.CallInfoRunable1;
import com.example.vmac.callCenter.rcms.MachinStatueRunable1;
import com.example.vmac.callCenter.rcms.PowerLinkRunable1;
import com.example.vmac.callCenter.rcms.RcmsConstant;
import com.example.vmac.callCenter.src.SrcRunable;
import com.example.vmac.callCenter.util.IflyUtil;
import com.example.vmac.callCenter.util.StatusBarUtils;
import com.example.vmac.callCenter.view.AddCallInfoActivity;
import com.example.vmac.callCenter.view.CallPopWindow;
import com.example.vmac.callCenter.view.KeyboardChangeListener;
import com.example.vmac.callCenter.watson.conversation.Constant;
import com.example.vmac.callCenter.watson.conversation.MessageFilter;
import com.example.vmac.callCenter.watson.conversation.SimpleRepsonseHandler;
import com.example.vmac.callCenter.watson.conversation.WatsonClient;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xutils.DbManager;
import org.xutils.x;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

public class SimpleMessagActivity extends AppCompatActivity implements  OnClickCallBack , KeyboardChangeListener.KeyBoardListener,View.OnClickListener{
    String TAG=SimpleMessagActivity.class.getSimpleName();
    MessageFilter filter =new MessageFilter();
    PopupWindow popupMenu;
    DbManager dbManager;
    private boolean isShowDialog;
    AdapterView.OnItemSelectedListener OnItemlistener;
    Dialog phoneDialog;
    Dialog functionDialog;
    ViewGroup buttonScrollView;
    private Dialog dialogInput;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final android.os.Message msag) {
            super.handleMessage(msag);
//            final Message message = (Message) msag.obj;

//            final String sendStr = message.getMessage();

            switch (msag.what) {


                case Constant.handler_callInfo:

//                    messageArrayList.add(message);
//                    changeData();
                 Object obj=  watsonClient.conversationContext.get("Call_status");
                    if(obj!=null){
                        sendStrMessage(obj.toString());
                    }else {
                        sendStrMessage("完成");
                    }

                    break;

                case Constant.handler_add_send_message:
//                    messageArrayList.add(message);
//                    playList.add(message.message);

                    sendStrMessage("完成");
                    break;
                case Constant.handler_porwer_link:

                    Object obj1=  watsonClient.conversationContext.get("loc_status");
                    Object obj2=  watsonClient.conversationContext.get("part_status");

                    if(obj1!=null){
                        sendStrMessage(obj1.toString());
                    }else if(obj2!=null){
                        sendStrMessage(obj2.toString());
                    }else {
                        sendStrMessage("完成");
                    }

                    break;

                case  Constant.handler_add_message:
                    sendStrMessage("查找完成");
                    break;
                case Constant.handler_show_rsc_result:

//                    if ("存在".equals(src_stat)) {
                        Message msg = new Message();
                        msg.msgType = Constant.chat_item_src_img;
                        msg.setMessage("https://www.ibm.com/support/knowledgecenter/en/POWER7/p7eai/11001510.htm?view=embed");
                        messageArrayList.add(msg);
//                    }

                        sendStrMessage("存在");
//                    Thread thread1 = new Thread(new Runnable() {
//                        public void run() {
//                            List<Message> list = watsonClient.sendMessage("存在");
//                            if (list.size() == 0) {
//                                return;
//                            }
//                            Object src_stat = watsonClient.getConversationContext().get("src_status");
//
//
//                            changeData();
//
//                        }
//                    });
//                    thread1.start();
                    break;

                default:
                    break;
            }
        }
    };
    private List<String> playList=new ArrayList<>();
    private Toast mToast;
    // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
    // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
    private RecognizerDialog mIatDialog;
    private Map<String, String> mIatResults = new LinkedHashMap<String, String>();//科大讯飞返回处理缓存
    private KeyboardChangeListener keyboardChangeListener;
    private RecyclerView recyclerView;
    private MessageAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int DEVICE_REQUEST_CODE = 102;
    private View call_phone;

    private Logger myLogger;
    private WatsonApplication watsonApplication;
    private WatsonClient watsonClient;
    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人
    private String voicer = "xiaoyan";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;




    public void sendStrMessage(final String str) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String input=str;
                if(input==null){
                    input="";
                }
                List<Message> list = watsonClient.sendMessage( input);
                if (list.size() == 0) {
                    changeData();
                    return;
                }
                messageArrayList.addAll(list);
              copyMessage(list,playList);
                if(watsonClient.getConversationContext().get(Constant.Show)!=null){
                    showFunctionScollor();
                }else {
                    final LinearLayout linearLayout1 = (LinearLayout) buttonScrollView.getChildAt(0);
                    if(linearLayout1.getChildCount()>0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linearLayout1.removeAllViews();
                            }
                        });
                    }
                }
              changeData();

            }


        });
        thread.start();
    }
    private void copyMessage(List<Message> list, List<String> playList) {
        for(Message e:list){
            if(e.msgType==Constant.watsonType) {
                playList.add(e.message);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("life'","oncreate");
       mToast = Toast.makeText(this,"",Toast.LENGTH_SHORT);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.content_chat_room);
        ActionBar actionBar = getSupportActionBar();
//        int color = Color.parseColor("#00b0da");
//
//        ColorDrawable drawable = new ColorDrawable(color);
//        actionBar.setBackgroundDrawable(drawable);
        actionBar.hide();
//        actionBar.setLogo(R.mipmap.watson_title);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
         StatusBarUtils.setWindowStatusBarColor(this,R.color.bg_actionbar_bg);

        call_phone = findViewById(R.id.actionbar_menu);



//        popupMenu =new CallListPopupMenu(this,  OnItemlistener);
        popupMenu = new CallPopWindow(this);
        OnClickListener onClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG,"拨打电话");
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data=null;
                if (ActivityCompat.checkSelfPermission(SimpleMessagActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(SimpleMessagActivity.this,"没有权限",Toast.LENGTH_SHORT).show();
                    ActivityCompat.requestPermissions(SimpleMessagActivity.this, new String[]{Manifest.permission.CALL_PHONE}, RECORD_REQUEST_CODE);
                    return;
                }

                switch (v.getId()){
                    case R.id.popup_callcenter:
                         data = Uri.parse("tel:" + watsonApplication.getString(R.string.callcenter_nubmer));
                        Log.i(TAG,"拨打电话 "+data);
                        intent.setData(data);
                        startActivity(intent);
                        break;
                    case  R.id.popup_callpart:
                        data = Uri.parse("tel:" + watsonApplication.getString(R.string.partcenter_nubmer));
                        Log.i(TAG,"拨打电话 "+data);
                        intent.setData(data);
                        startActivity(intent);
                        break;
                }
            }
        };
        ((CallPopWindow)popupMenu).setOnClickListener(onClickListener);
        call_phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.showAsDropDown(v);
            }
        });


        initPhonedialog();
        buttonScrollView = (ViewGroup) findViewById(R.id.chat_room_button_function);

//        String[] litle={"测试菜单","测试菜单2","测试菜单3"};
        initInputDialog();
//        intiDialog();
//        initBottionScrollView(litle, new OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                functionDialog.show();
//                dialogInput.show();
//            }
//        });


      watsonApplication = (WatsonApplication) getApplicationContext();
        watsonClient = watsonApplication.getWatsonClient();
        watsonClient.setResponseHandler(new SimpleRepsonseHandler());
        watsonClient.setCallBack(this);
        myLogger = Logger.getLogger("myLogger");


        inputMessage = (EditText) findViewById(R.id.message);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        btnRecord = (ImageButton) findViewById(R.id.btn_record);
        String customFont = "Montserrat-Regular.ttf";
        Typeface typeface = Typeface.createFromAsset(getAssets(), customFont);

        inputMessage.setTypeface(typeface);
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            makeRequest();

        }


        permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_SETTINGS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to record denied");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_SETTINGS},
                    RECORD_REQUEST_CODE);
        }
        //获取设备标识
//       permission =  ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
//        if ( permission!= PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.READ_PHONE_STATE},
//                    DEVICE_REQUEST_CODE);
//        }

        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_SETTINGS);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to write denied");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_SETTINGS},
                    RECORD_REQUEST_CODE);
        }

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            if(!Settings.System.canWrite(this)){
//                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
//                        Uri.parse("package:" + getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivityForResult(intent, 10);
//            }else{
////                有了权限，你要做什么呢？具体的动作
//
//
//            }
//        }

        permission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission to write denied");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    RECORD_REQUEST_CODE);
        }



        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        messageArrayList = new ArrayList<>();

        mAdapter = new MessageAdapter(messageArrayList, this, getApplicationContext(), this);

        mAdapter.setPlayList(playList);

        mAdapter.setConversationContext(watsonClient.getConversationContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);

//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),
//                LinearLayoutManager.VERTICAL, reverseLayout);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

//        recyclerView.setRecyclerListener(new RecyclerView.RecyclerListener() {
//            @Override
//            public void onViewRecycled(RecyclerView.ViewHolder holder) {
//
//            }
//        });
        this.inputMessage.setText("");
        this.initialRequest = true;

        sendMessage();

        //发送信息
        btnSend.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    sendMessage();
                }
            }
        });

        //录音按钮 点击录音
        btnRecord.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //  recordMessage();
                stopSpeeking();
                ikfyRecordeMessage();
            }
        });


        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + watsonApplication.getString(R.string.iflytekAppId));
        mIatDialog = new RecognizerDialog(SimpleMessagActivity.this, new InitListener() {
            @Override
            public void onInit(int code) {
                final int i = code;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (i == ErrorCode.SUCCESS) {
                            Log.i(TAG,"初始化成功:" + i);
//                            showTip("初始化成功:" + i);
                        } else {
//                            showTip("初始化失败:" + i);
                            Log.i(TAG,"初始化失败:" + i);
                        }
                    }
                });

            }
        });

        // 初始化合成对象
        mTts = SpeechSynthesizer.createSynthesizer(SimpleMessagActivity.this,
                new InitListener() {
                    @Override
                    public void onInit(int code) {
                        Log.d(TAG, "InitListener init() code = " + code);
                        if (code != ErrorCode.SUCCESS) {
//                            showTip("初始化失败,错误码：" + code);
                            Log.e(TAG,"初始化失败,错误码：" + code);
                        } else {
                            // 初始化成功，之后可以调用startSpeaking方法
                            // 注：有的开发者在onCreate方法中创建完合成对象之后马上就调用startSpeaking进行合成，
                            // 正确的做法是将onCreate中的startSpeaking调用移至这里
                        }
                    }
                });
        //  mIatDialog.setParameter();
        keyboardChangeListener=new KeyboardChangeListener(this);
        keyboardChangeListener.setKeyBoardListener(this);


    };

    private void changeData() {
        playText();
        runOnUiThread(new Runnable() {
            public void run() {
                mAdapter.notifyDataSetChanged();
                if (mAdapter.getItemCount() > 1) {
//                    recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() );
                    recyclerView.scrollToPosition(mAdapter.getItemCount()-1 );
                }

            }
        });

    }

    // Speech-to-Text Record Audio permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
            case RECORD_REQUEST_CODE: {

                if (grantResults.length == 0
                        || grantResults[0] !=
                        PackageManager.PERMISSION_GRANTED) {

                    Log.i(TAG, "Permission has been denied by user");
                } else {
                    Log.i(TAG, "Permission has been granted by user");
                }
                return;
            }
        }
        if (!permissionToRecordAccepted) finish();

    }

    //申请录音的权限
    protected void makeRequest() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }

    // Sending a message to Watson Conversation Service
    private void sendMessage(String msg){
        this.inputMessage.setText(msg);
        sendMessage();
    }
    private void sendMessage() {

        final String inputmessage = this.inputMessage.getText().toString().trim();
        int count = recyclerView.getAdapter().getItemCount();
        if (TextUtils.isEmpty(inputmessage) && count != 0) {
            Toast.makeText(this, "没有输入内容", Toast.LENGTH_SHORT).show();
            return;
        }


        if (!this.initialRequest) {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("1");
            inputMessage.msgType = Constant.customType;

            messageArrayList.add(inputMessage);
            myLogger.info("Sending a message to Watson Conversation Service");

        } else {
            Message inputMessage = new Message();
            inputMessage.setMessage(inputmessage);
            inputMessage.setId("100");
            inputMessage.msgType = Constant.customType;
            this.initialRequest = false;
          //  Toast.makeText(getApplicationContext(), "Tap on the message for Voice", Toast.LENGTH_LONG).show();

        }

        this.inputMessage.setText("");
        changeData();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                List<Message> list = watsonClient.sendMessage(inputmessage,handler);

                if (list.size() == 0) {
                    return;
                }

               Object oshow=  watsonClient.getConversationContext().get(Constant.Show);
                if(oshow!=null&& MessageFilter.isNumber(oshow.toString())){

                            showFunctionScollor();


                }else {
                    final LinearLayout linearLayout1 = (LinearLayout) buttonScrollView.getChildAt(0);
                    if(linearLayout1.getChildCount()>0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                linearLayout1.removeAllViews();
                            }
                        });
                    }
                }
                messageArrayList.addAll(list);
                copyMessage(list,playList);
                changeData();
            }
        });

        thread.start();
    }


    private void ikfyRecordeMessage() {

        mIatResults.clear();
        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        // mIatDialog.setParameter(SpeechConstant.VAD_EOS, "3000");
        // 显示听写对话框
        mIatDialog.setListener(mRecognizerDialogListener);
        Log.d(TAG, "测试");
        mIatDialog.show();
//        showTip();
        Log.i(TAG,getString(R.string.text_begin));
    }

    /**
     * Check Internet Connection
     *
     * @return
     */
    //检查是否可以连接网络
    private boolean checkInternetConnection() {
        //获得网络服务
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }





    @Override
    public void OnCallBack(int actionType, final String callBackmessage) {
        /*
            actionType 1 发送信息到watson
         */
        Log.i(TAG,"actionType = "+actionType+"  "+callBackmessage);
        if(actionType == 1){
            sendStrMessage(callBackmessage);
          int visibility =  buttonScrollView.getVisibility();
          if(View.GONE==visibility){
              buttonScrollView.setVisibility(View.VISIBLE);
          }else{
              buttonScrollView.setVisibility(View.GONE);
          }
        }else  if(actionType == Constant.start_speeker){
            startSpeeker(callBackmessage);

        }else if(Constant.check_callInfo_query==actionType){
            CallInfoRunable1 callInfoRunable = new CallInfoRunable1(RcmsConstant.IntentGetCallfo, callBackmessage, watsonClient.getConversationContext(), handler);
            Thread thread = new Thread(callInfoRunable);
            callInfoRunable.uuid=watsonApplication.uuid;
            callInfoRunable.setMessageList(messageArrayList);
            thread.start();

        }
        else if(Constant.check_type_SN == actionType){
            MachinStatueRunable1 machinStatueRunable = new MachinStatueRunable1(callBackmessage, messageArrayList, watsonClient.getConversationContext(), handler);
            Thread thread = new Thread(machinStatueRunable);
            thread.start();

        }else if(Constant.check_power_local == actionType){
            PowerLinkRunable1 powerLinkRunable = new PowerLinkRunable1(callBackmessage, watsonClient.getConversationContext(), handler, RcmsConstant.IntentGetPowerLocal);
            Thread thread = new Thread(powerLinkRunable);
            powerLinkRunable.setMessagesList(messageArrayList);
            thread.start();
            changeData();
        }else if(Constant.check_power_part == actionType){
            PowerLinkRunable1 powerLinkRunable = new PowerLinkRunable1(callBackmessage, watsonClient.getConversationContext(), handler, RcmsConstant.IntentGetPowerLocal);
            Thread thread = new Thread(powerLinkRunable);
            powerLinkRunable.setMessagesList(messageArrayList);
            thread.start();
            changeData();
        }
        else if(Constant.callBack_start_web == actionType){
            Intent intent = new Intent(this, WebVewActivity.class);
            intent.putExtra("url", callBackmessage);
            startActivity(intent);
        }  else if(Constant.callBack_add_callinfo == actionType){
            Intent intent = new Intent(this,AddCallInfoActivity.class);
            startActivityForResult(intent,1);
        }
        else if (Constant.show_chat_srcbox == actionType) {
            String url = "https://www.ibm.com/support/knowledgecenter/en/POWER7/p7eai/11001510.htm?view=embed";
//            Intent intent = new Intent(this, WebVewActivity.class);
//            intent.putExtra("url",url);
//            startActivity(intent);
            SrcRunable srcRunable = new SrcRunable(callBackmessage, "", watsonClient.getConversationContext(), handler);
            srcRunable.start();
            changeData();
        }

    }


    /**
     * 返回activity 调用方法
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(DEVICE_REQUEST_CODE==requestCode){
            if(requestCode == 0){
                Log.i(TAG,"读取手机信息权限拒绝了");
            }
        }

        if(resultCode==0){
            sendStrMessage("取消了");
        }else if(resultCode==1||resultCode==2){
            String str =data.getStringExtra("send");
            sendStrMessage(str);
        }
    }

    private void showMicText(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputMessage.setText(text);
            }
        });
    }

    private void enableMicButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnRecord.setEnabled(true);
            }
        });
    }

    private void showError(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(SimpleMessagActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }


    private void showTip(final String str) {

        if(str!=null) {
            Log.i(TAG,str);
            mToast.setText(str);
            mToast.show();
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };


    /**
     * 听写UI监听器 科大讯飞
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {

            printResult(results);
            if (isLast) {
                sendMessage();
            }
        }

        private void printResult(RecognizerResult results) {
            String text = JsonParser.parseIatResult(results.getResultString());

            String sn = null;
            // 读取json结果中的sn字段
            try {
                JSONObject resultJson = new JSONObject(results.getResultString());
                sn = resultJson.optString("sn");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            mIatResults.put(sn, text);

            StringBuffer resultBuffer = new StringBuffer();
            for (String key : mIatResults.keySet()) {
                resultBuffer.append(mIatResults.get(key));
            }
            showMicText(resultBuffer.toString());
            inputMessage.setText(resultBuffer.toString());


        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            if (error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

    };

    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
//            showTip("开始播放");
            Log.i(TAG,"开始播放");
        }

        @Override
        public void onSpeakPaused() {
//            showTip("暂停播放");
            Log.i(TAG,"暂停播放");

        }

        @Override
        public void onSpeakResumed() {
//            showTip("继续播放");
            Log.i(TAG,"继续播放");
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
            mPercentForBuffering = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
//            Log.i(TAG,String.format(getString(R.string.tts_toast_format),mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;

        }

        @Override
        public void onCompleted(SpeechError error) {
            if (error == null) {
//                showTip("播放完成");
                if(playList.size()>0){
                    playText();
                }
            } else if (error != null) {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    protected void startSpeeker(String text) {

        // text.replace("No","number");
        // 设置参数
        setParam();
        boolean isSSML=true;
        int code  =0;
        if(isSSML){
            mTts.setParameter("ttp", "cssml");

            mTts.setParameter(SpeechConstant.TEXT_ENCODING, "GB2312");

          text =   IflyUtil.convertText(text);
//            text="<?xml version=\"1.0\" encoding=\"GB2312\"?><speak><sayas type=\"number:digits\">"+text+"</sayas></speak>";
            code = mTts.startSpeaking(text, mTtsListener);
            Log.i(TAG,text);
        }else{
            code = mTts.startSpeaking(text, mTtsListener);
        }

//			/**
//			 * 只保存音频不进行播放接口,调用此接口请注释startSpeaking接口
//			 * text:要合成的文本，uri:需要保存的音频全路径，listener:回调接口
//			*/
//			String path = Environment.getExternalStorageDirectory()+"/tts.pcm";
//			int code = mTts.synthesizeToUri(text, path, mTtsListener);

        if (code != ErrorCode.SUCCESS) {
            if (code == ErrorCode.ERROR_COMPONENT_NOT_INSTALLED) {
                //未安装则跳转到提示安装页面
            } else {
//                showTip("语音合成失败,错误码: " + code);
                Log.e(TAG,""+"语音合成失败,错误码: " + code);
            }
        }
    }

    /**
     * 科大 语音合成参数设置
     *
     * @return
     */
    private void setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
//        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        mTts.setParameter(SpeechConstant.VOICE_NAME, voicer);
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "70");//本地0-100 ，在线0-200 默认50
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");


//        }else {
//            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
//            // 设置本地合成发音人 voicer为空，默认通过语记界面指定发音人。
//            mTts.setParameter(SpeechConstant.VOICE_NAME, "");
//            /**
//             * TODO 本地合成不设置语速、音调、音量，默认使用语记设置
//             * 开发者如需自定义参数，请参考在线合成参数设置
//             */
//        }
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }


    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {
        if (mAdapter.getItemCount() > 1) {
            //   recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, mAdapter.getItemCount() );
            recyclerView.scrollToPosition(mAdapter.getItemCount()-1 );
            //     recyclerView.smoothScrollToPosition(mAdapter.getItemCount()-1 );

        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    private  void  playText(){
        if(playList.size()>0) {
            String text = playList.get(0);
            playList.remove(0);
            stopSpeeking();
            startSpeeker(text);
        }
    }

    /**
     * 点击是播放录音
     * @param text
     */
    private void onclickPaly(String text){
        playList.clear();
        stopSpeeking();
        startSpeeker(text);

    }

    private  void stopSpeeking(){

        mTts.stopSpeaking();
    }

    //获取手机的唯一标识
    public String getPhoneSign() {
        StringBuilder deviceId = new StringBuilder();
        // 渠道标志
        deviceId.append("a");
        try {
            //IMEI（imei）
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_PHONE_STATE},
                        DEVICE_REQUEST_CODE);
            }
            String imei = tm.getDeviceId();
            if(!TextUtils.isEmpty(imei)){
                deviceId.append("imei");
                deviceId.append(imei);
                return deviceId.toString();
            }
            //序列号（sn）
            String sn = tm.getSimSerialNumber();
            if(!TextUtils.isEmpty(sn)){
                deviceId.append("sn");
                deviceId.append(sn);
                return deviceId.toString();
            }
            //如果上面都没有， 则生成一个id：随机码
            String uuid = getUUID();
            if(!TextUtils.isEmpty(uuid)){
                deviceId.append("id");
                deviceId.append(uuid);
                return deviceId.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            deviceId.append("id").append(getUUID());
        }
        return deviceId.toString();
    }
    /**
     * 得到全局唯一UUID
     */
    private String uuid;
    public String getUUID(){
        SharedPreferences mShare = getSharedPreferences("uuid",MODE_PRIVATE);
        if(mShare != null){
            uuid = mShare.getString("uuid", "");
        }
        if(TextUtils.isEmpty(uuid)){
            uuid = UUID.randomUUID().toString();
            mShare.edit().putString("uuid",uuid).commit();
        }
        return uuid;
    }




    public void initPhonedialog(){

            View view = LayoutInflater.from(this).inflate(R.layout.main_menu_popup, null);
            phoneDialog= new Dialog(this, R.style.main_menu_animstyle);
//            dialog.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                    ViewGroup.LayoutParams.WRAP_CONTENT));
             phoneDialog.setContentView(view);
            Window window = phoneDialog.getWindow();
            // 设置显示动画
//            window.setWindowAnimations(R.style.main_menu_animstyle);
            WindowManager.LayoutParams wl = window.getAttributes();
            wl.x = 100;
            wl.y = 100;
            // 以下这两句是为了保证按钮可以水平满屏
//            wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            // 设置显示位置
            phoneDialog.onWindowAttributesChanged(wl);
            // 设置点击外围消失
         phoneDialog.setCanceledOnTouchOutside(true);


    }
    public void initDb(){
        DbManager.DaoConfig daoConfig = new DbManager.DaoConfig()
                .setDbName("callcenter.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
//            .setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
                .setDbVersion(2)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                });
        dbManager = x.getDb(daoConfig);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("life'","onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("life'","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("life'","onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("life'"," onStart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("life'","onDestroy");
    }


    private void intiDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.chat_item_dynamic_choice, null);
        View bt_cancell = view.findViewById(R.id.main_dialog_cancell);

        functionDialog = new Dialog(this, R.style.transparentFrameWindowStyle);
        bt_cancell.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                functionDialog .dismiss();
            }
        });
        functionDialog .setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window =  functionDialog .getWindow();
        // 设置显示动画
        window.setWindowAnimations(R.style.main_buttom_dialog_animstyle);
//        WindowManager.LayoutParams wl = window.getAttributes();
//        wl.x = 0;
////        getWindowManager().getDefaultDisplay().getSize();
//        wl.y = getWindowManager().getDefaultDisplay().getHeight();
//        // 以下这两句是为了保证按钮可以水平满屏
//        wl.width = ViewGroup.LayoutParams.MATCH_PARENT;
//        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        // 设置显示位置
//        functionDialog .onWindowAttributesChanged(wl);
        
        // 设置点击外围消失
        functionDialog .setCanceledOnTouchOutside(true);
    }

    public void initBottionScrollView(final  String[] item, final  OnClickListener onClickListener){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                buttonScrollView.setVisibility(View.VISIBLE);
                LinearLayout linearLayout = (LinearLayout) buttonScrollView.getChildAt(0);

                LinearLayout.LayoutParams textViewParams=  new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);


                for(String title : item){
                    TextView textView = new TextView(SimpleMessagActivity.this);
                    textViewParams.setMargins(5,0,5,0);
                    textView.setLayoutParams(textViewParams);
                    textView.setTextSize(18);

                    textView.setBackground(getResources().getDrawable(R.drawable.shape_corner));
                    textView.setText(title);
                    textView.setOnClickListener(onClickListener);
                    linearLayout.addView(textView);
                }
            }
        });


    }

    private void initInputDialog(){
//        View view = LayoutInflater.from(this).inflate(R.layout.dialog_chat_input, null);
//        View bt_cancell = view.findViewById(R.id.main_dialog_cancell);
//
//        dialogInput = new Dialog(this, R.style.transparentFrameWindowStyle);
//
//        Window dialogWindow = dialogInput.getWindow();
//         WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.CENTER);
//        lp.x = 100; // 新位置X坐标
//        lp.y = 100; // 新位置Y坐标
//        lp.width = 300; // 宽度
//        lp.height = 300; // 高度
//        lp.alpha = 0.7f; // 透明度
//       dialogWindow.setAttributes(lp);


        AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        builder.setIcon(R.mipmap.ic_launcher);
//        builder.setTitle("title");

        /**
         * 设置内容区域为自定义View
         */
        LinearLayout loginDialog= (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_chat_input,null);
        builder.setView(loginDialog);

        builder.setCancelable(true);
        dialogInput=builder.create();
//        dialog.show();

    }

    private void showFunctionScollor(){
       Object oshow =  watsonClient.getConversationContext().get(Constant.Show);
       if(oshow!=null){
           String sshow = oshow.toString();
           int showNumber= 0;
           try {
              showNumber  =Integer.valueOf(sshow);
           }catch (Exception e){

               return;
           }

           switch (showNumber){
               case 1:
                   String[] title = {"保修","不保修"};
                   initBottionScrollView(title,this);
                   break;
               case 2:
                   String[] title1 = {"联系客服人员"};
                   initBottionScrollView(title1, new OnClickListener() {
                       @Override
                       public void onClick(View v) {
//                           phoneDialog.show();
                           popupMenu.showAsDropDown( call_phone);
                       }
                   });
                   break;
               case  3:
                   String[] title3 = {"电话号码查询","工单单号查询","机器型号序列号查询"};

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           buttonScrollView.setVisibility(View.VISIBLE);
                           LinearLayout linearLayout = (LinearLayout) buttonScrollView.getChildAt(0);

                           LinearLayout.LayoutParams textViewParams=  new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT,
                                   LinearLayout.LayoutParams.WRAP_CONTENT);

                               TextView textView = new TextView(SimpleMessagActivity.this);
                               textViewParams.setMargins(5,0,5,0);
                               textView.setLayoutParams(textViewParams);
                               textView.setTextSize(18);

                               textView.setBackground(getResources().getDrawable(R.drawable.shape_corner));
                               textView.setText("电话号码查询");
                               textView.setOnClickListener(new OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                            showDialogInputPhone(1);
                                   }
                               });
                               linearLayout.addView(textView);
//*********************************************************************

                           textView = new TextView(SimpleMessagActivity.this);
                           textViewParams.setMargins(5,0,5,0);
                           textView.setLayoutParams(textViewParams);
                           textView.setTextSize(18);
                           textView.setBackground(getResources().getDrawable(R.drawable.shape_corner));
                           textView.setText("工单单号查询");
                           textView.setOnClickListener(new OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   showDialogInputPhone(2);
                               }
                           });
                           linearLayout.addView(textView);



                           textView = new TextView(SimpleMessagActivity.this);
                           textViewParams.setMargins(5,0,5,0);
                           textView.setLayoutParams(textViewParams);
                           textView.setTextSize(18);
                           textView.setBackground(getResources().getDrawable(R.drawable.shape_corner));
                           textView.setText("机器序列号查询");
                           textView.setOnClickListener(new OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   showDialogInputPhone(3);
                               }
                           });
                           linearLayout.addView(textView);


                       }
                   });
                   break;
               case 5:
                   String[] title5 = {"存在","不存在"};
                   initBottionScrollView(title5,this);
                   break;
               case 6:
                   String[] title6= {"如何查看报错代码"};
                   initBottionScrollView(title6,this);
                   break;
           }
       }
    }

    @Override
    public void onClick(View v) {
        TextView tv = (TextView) v;
        String text  = tv.getText().toString();
        sendMessage(text);
    }

    /**
     * 初始化 dialog并 显示
     * @param  type 1 = phone ,2 = callno 3 = machinType+Sn
     */
    private void showDialogInputPhone(int type){
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        /**
         * 设置内容区域为自定义View
         */
        LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_chat_input,null);
        View bt_sure = linearLayout.findViewById(R.id.dialog_sure);

        if(type==1){
            View phone_line = linearLayout.findViewById(R.id.dialog_line_phone);
            phone_line.setVisibility(View.VISIBLE);
            bt_sure.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewParent =  v.getRootView();
                    TextView  textView = (TextView) viewParent.findViewById(R.id.dialog_et_phone);
                    String strPhone  = textView.getText().toString();
                    MessageFilter.MatcherResulter resulter =  filter.isMatcherCallInfoPhone(strPhone);
                    if(!resulter.isMatcher){
                         TextView tv_reson = (TextView) viewParent.findViewById(R.id.dialog_tx_reson);
                         tv_reson.setVisibility(View.VISIBLE);
                         tv_reson.setText("电话号码输入有误");
                    }else {
                        sendMessage(resulter.matcherString);
                        dialogInput.dismiss();
                    }
                }
            });
        }else if(type == 2){
            View phone_line = linearLayout.findViewById(R.id.dialog_line_callno);
            phone_line.setVisibility(View.VISIBLE);
            bt_sure.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewParent =  v.getRootView();
                    TextView  textView = (TextView) viewParent.findViewById(R.id.dialog_et_callno);
                    String strCallNo  = textView.getText().toString();
                    MessageFilter.MatcherResulter resulter =  filter.isMatcherCallNo(strCallNo );
                    if(!resulter.isMatcher){
                        TextView tv_reson = (TextView) viewParent.findViewById(R.id.dialog_tx_reson);
                        tv_reson.setVisibility(View.VISIBLE);
                        tv_reson.setText("工单信息输入有误");
                    }else {
                        sendMessage(resulter.matcherString);
                        dialogInput.dismiss();
                    }
                }
            });
        }else if(type == 3){
            View machinTypeLine = linearLayout.findViewById(R.id.dialog_line_machin_type);
            machinTypeLine.setVisibility(View.VISIBLE);

            View machinSNLine = linearLayout.findViewById(R.id.dialog_line_machin_sn);
            machinSNLine.setVisibility(View.VISIBLE);


            bt_sure.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    View viewParent =  v.getRootView();
                    TextView  tv_machinType = (TextView) viewParent.findViewById(R.id.dialog_et_machin_type);
                    String machinType   = tv_machinType.getText().toString();



                    TextView  tv_machinSN = (TextView) viewParent.findViewById(R.id.dialog_et_machin_sn);
                    String machinSn   =  tv_machinSN .getText().toString();

                    sendMessage(machinType+"-"+machinSn);
                    dialogInput.dismiss();
                }
            });


        }


        View bt_cancel = linearLayout.findViewById(R.id.dialog_cancel);
        bt_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogInput.dismiss();
            }
        });

        builder.setView(linearLayout );
        builder.setCancelable(true);
        dialogInput=builder.create();
        dialogInput.show();
    }
}
