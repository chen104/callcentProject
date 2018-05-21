package com.example.vmac.callCenter;

import android.Manifest;
import android.content.Context;

import android.content.Intent;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Toast;

import com.example.vmac.callCenter.app.WatsonApplication;
import com.example.vmac.callCenter.iflytek.result.speech.util.JsonParser;

import com.example.vmac.callCenter.rcms.CallInfoRunable1;

import com.example.vmac.callCenter.rcms.MachinStatueRunable1;

import com.example.vmac.callCenter.rcms.PowerLinkRunable1;
import com.example.vmac.callCenter.rcms.RcmsConstant;
import com.example.vmac.callCenter.src.SrcRunable;
import com.example.vmac.callCenter.view.AddCallInfoActivity;
import com.example.vmac.callCenter.view.KeyboardChangeListener;
import com.example.vmac.callCenter.watson.conversation.Constant;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;


public class MainActivity extends AppCompatActivity implements  OnClickCallBack , KeyboardChangeListener.KeyBoardListener {
    String intentAction = RcmsConstant.IntentGetPowerLocal;
    private List<String> playList=new ArrayList<>();
    private Toast mToast;
    // 初始化听写Dialog，如果只使用有UI听写功能，无需创建SpeechRecognizer
    // 使用UI听写功能，请根据sdk文件目录下的notice.txt,放置布局文件和图片资源
    private RecognizerDialog mIatDialog;
    private Map<String, String> mIatResults = new LinkedHashMap<String, String>();//科大讯飞返回处理缓存
    private KeyboardChangeListener keyboardChangeListener;


    private RecyclerView recyclerView;
    private CallCenterChatAdapter mAdapter;
    private ArrayList<Message> messageArrayList;
    private EditText inputMessage;
    private ImageButton btnSend;
    private ImageButton btnRecord;
    private boolean isSendMsg = false;

    private boolean initialRequest;
    private boolean permissionToRecordAccepted = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String TAG = "AddCallInfoActivity";
    private static final int RECORD_REQUEST_CODE = 101;
    private static final int DEVICE_REQUEST_CODE = 102;
    private boolean listening = false;

    private Logger myLogger;
    private WatsonApplication watsonApplication;
    private String analytics_APIKEY;


    private WatsonClient watsonClient;
    // 语音合成对象
    private SpeechSynthesizer mTts;

    // 默认发音人
    private String voicer = "xiaoyan";
    // 缓冲进度
    private int mPercentForBuffering = 0;
    // 播放进度
    private int mPercentForPlaying = 0;
    //    private SharedPreferences mSharedPreferences;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(final android.os.Message msag) {
            super.handleMessage(msag);
            final Message message = (Message) msag.obj;
            final String sendStr = message.getMessage();

            switch (msag.what) {
                case Constant.handler_machinStatue:

                    messageArrayList.add(message);

                    Thread thread = new Thread(new Runnable() {
                        public void run() {
                            List<Message> list = watsonClient.sendMessage(sendStr);
                            if (list.size() == 0) {
                                return;
                            }
                            messageArrayList.addAll(list);
                            changeData();
                        }
                    });
                    thread.start();
                    break;
                case Constant.handler_exception:

                    messageArrayList.add(message);
                    changeData();
                    break;
                case Constant.handler_callInfo:

                    messageArrayList.add(message);
                    changeData();
                    sendStrMessage("找到");
                    break;
                case Constant.handler_add_message:
                    messageArrayList.add(message);
                    changeData();
                    break;
                case Constant.handler_add_send_message:
                    messageArrayList.add(message);
                    changeData();
                    sendStrMessage("完成");
                    break;
                case Constant.handler_show_rsc_result:
                    Thread thread1 = new Thread(new Runnable() {
                        public void run() {
                            List<Message> list = watsonClient.sendMessage("存在");
                            if (list.size() == 0) {
                                return;
                            }
                            Object src_stat = watsonClient.getConversationContext().get("src_status");

                            if ("存在".equals(src_stat)) {
                                Message msg = new Message();
                                msg.msgType = Constant.chat_item_src_img;
                                msg.setMessage("https://www.ibm.com/support/knowledgecenter/en/POWER7/p7eai/11001510.htm?view=embed");
                                messageArrayList.add(msg);
                            }
                            messageArrayList.addAll(list);
                            changeData();

                        }
                    });
                    thread1.start();
                    break;
                default:
                    break;
            }
        }
    };

    public void sendStrMessage(final String str) {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                String input=str;
                if(input==null){
                    input="";
                }
                List<Message> list = watsonClient.sendMessage( input);
                if (list.size() == 0) {
                    return;
                }
                messageArrayList.addAll(list);
                copyMessage(list,playList);

                changeData();

            }
        });
        thread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       ActionBar actionBar = getSupportActionBar();
        actionBar.setLogo(R.mipmap.watson_title);
        actionBar.setDisplayUseLogoEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        int color = Color.parseColor("#00b0da");

        ColorDrawable drawable = new ColorDrawable(color);
        actionBar.setBackgroundDrawable(drawable);




        mToast = Toast.makeText(this, "测试", Toast.LENGTH_LONG);

        watsonApplication = (WatsonApplication) getApplicationContext();
        watsonClient = watsonApplication.getWatsonClient();

//        mSharedPreferences = getSharedPreferences(this.getPackageName(), MODE_PRIVATE);


        //Analytics.send();
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
//        {
//            Message message = new Message();
//            message.msgType =Constant.show_callNo;
//            message.message="显示查询";
//            Log.i(TAG,"添加message");
//            messageArrayList.add(message);
//        }
        mAdapter = new CallCenterChatAdapter(messageArrayList, this, getApplicationContext(), this);
        mAdapter.setHandler(handler);
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

        {
//            Message msg = new Message();
//            msg.setMessage("显示评分");
//            msg.msgType = Constant.chat_item_show_ratingbar;
          //  messageArrayList.add(msg);
//            Message msg1 = new Message();
//            msg1.setMessage("拨打电话");
//            msg1.msgType = Constant.chat_item_show_call_or_send;
//            messageArrayList.add(msg1);
        }

        //发送信息
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInternetConnection()) {
                    sendMessage();
                }
            }
        });

        //录音按钮 点击录音
        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  recordMessage();
                stopSpeeking();
                ikfyRecordeMessage();
            }
        });


//        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + watsonApplication.getString(R.string.iflytekAppId));
        mIatDialog = new RecognizerDialog(MainActivity.this, new InitListener() {
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
        mTts = SpeechSynthesizer.createSynthesizer(MainActivity.this,
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


        //获取手机唯一标识
//      String sign=   getPhoneSign();
//      watsonApplication.uuid = sign;
//      Log.i(TAG,"手机唯一标识:"+sign);
    }

    ;

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
            Toast.makeText(getApplicationContext(), "Tap on the message for Voice", Toast.LENGTH_LONG).show();

        }

        this.inputMessage.setText("");
        changeData();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                List<Message> list = watsonClient.sendMessage(inputmessage);

                if (list.size() == 0) {
                    return;
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
        Log.i(TAG,"调用 oncallBack "+actionType+"  "+callBackmessage);
        if (actionType == 1) {
            inputMessage.setText(callBackmessage);
            sendMessage();
        } else if (Constant.start_speeker == actionType) {
            playText(callBackmessage);

        } else if (Constant.check_type_SN == actionType) {//通过线程去查询RCMS查询信息

            changeData();

            //     SearchThread T = new SearchThread(AddCallInfoActivity.this.getApplicationContext(), AddCallInfoActivity.this, messageArrayList, callBackmessage);
//              TestThread       T=  new TestThread(AddCallInfoActivity.this.getApplicationContext(),AddCallInfoActivity.this,messageArrayList,callBackmessage);
//            T.setHandler(handler);
//            T.setConversationContext(watsonClient.getConversationContext());
//            T.start();
            MachinStatueRunable1 machinStatueRunable = new MachinStatueRunable1(callBackmessage, messageArrayList, watsonClient.getConversationContext(), handler);
            Thread thread = new Thread(machinStatueRunable);
            thread.start();
        } else if (Constant.ListDataChang == actionType) {
            changeData();
        } else if (Constant.ListDataChang == actionType) {
            changeData();
        }

//        else if (Constant.check_callInfo_callNo == actionType) {
//            Message message = new Message();
//            message.setMessage("查询Call No:" + callBackmessage);
//            message.msgType = Constant.customType;
//            messageArrayList.add(message);
//            message = new Message();
//            message.msgType = Constant.watsonType;
//            message.setMessage("正在为你查询Call的信息……");
//            messageArrayList.add(message);
//            changeData();
//            CallInfoRunable1 callInfoRunable = new CallInfoRunable1(RcmsConstant.IntentGetCallfo, callBackmessage, watsonClient.getConversationContext(), handler);
//            callInfoRunable.uuid=watsonApplication.uuid;
//            Thread thread = new Thread(callInfoRunable);
//            thread.start();
//
//        } else if (Constant.check_callInfo_serial == actionType) {
//            Message message = new Message();
//            message.setMessage("查询序列号" + callBackmessage + " Call的信息");
//            message.msgType = Constant.customType;
//            messageArrayList.add(message);
//            message = new Message();
//            message.msgType = Constant.watsonType;
//            message.setMessage("正在为你查询Call的信息……");
//            messageArrayList.add(message);
//            changeData();
//            CallInfoRunable1 callInfoRunable = new CallInfoRunable1(RcmsConstant.IntentGetCallfo, callBackmessage, watsonClient.getConversationContext(), handler);
//            Thread thread = new Thread(callInfoRunable);
//            callInfoRunable.uuid=watsonApplication.uuid;
//            thread.start();
//            changeData();
//            //     sendStrMessage(callBackmessage);
//        }
//        else if (Constant.check_callInfo_byphone == actionType) {
//            Message message = new Message();
//            message.setMessage("查询序列号" + callBackmessage + " Call的信息");
//            message.msgType = Constant.customType;
//            messageArrayList.add(message);
//            message = new Message();
//            message.msgType = Constant.watsonType;
//            message.setMessage("正在为你查询Call的信息……");
//            messageArrayList.add(message);
//            changeData();
//            CallInfoRunable1 callInfoRunable = new CallInfoRunable1(RcmsConstant.IntentGetCallfo, callBackmessage, watsonClient.getConversationContext(), handler);
//            Thread thread = new Thread(callInfoRunable);
//            callInfoRunable.uuid=watsonApplication.uuid;
//            thread.start();
//            changeData();
//            //     sendStrMessage(callBackmessage);
//        }

        else if (Constant.check_callInfo_query== actionType) {
            Message message = new Message();
            message.setMessage("查询序" + callBackmessage + " Call的信息");
            message.msgType = Constant.customType;
            messageArrayList.add(message);
            message = new Message();
            message.msgType = Constant.watsonType;
            message.setMessage("正在为你查询Call的信息……");
            messageArrayList.add(message);
            changeData();
            CallInfoRunable1 callInfoRunable = new CallInfoRunable1(RcmsConstant.IntentGetCallfo, callBackmessage, watsonClient.getConversationContext(), handler);
            Thread thread = new Thread(callInfoRunable);
            callInfoRunable.uuid=watsonApplication.uuid;
            thread.start();
            changeData();
            //     sendStrMessage(callBackmessage);
        }


        else if (Constant.send_wansot_massge == actionType) {
            changeData();
            sendStrMessage(callBackmessage);
        } else if (Constant.find_link == actionType) {
            Message message = new Message();
            message.setMessage(callBackmessage);
            message.msgType = Constant.customType;
            messageArrayList.add(message);
            message = new Message();
            message.msgType = Constant.watsonType;
            message.setMessage("正在为你查询……");
            messageArrayList.add(message);
            changeData();

            PowerLinkRunable1 powerLinkRunable = new PowerLinkRunable1(callBackmessage, watsonClient.getConversationContext(), handler, mAdapter.getIntent());

            Thread thread = new Thread(powerLinkRunable);
            thread.start();
            changeData();
        } else if (Constant.callBack_start_web == actionType) {

            Intent intent = new Intent(this, WebVewActivity.class);
            intent.putExtra("url", callBackmessage);
            startActivity(intent);
        } else if (Constant.show_chat_srcbox == actionType) {
            String url = "https://www.ibm.com/support/knowledgecenter/en/POWER7/p7eai/11001510.htm?view=embed";
//            Intent intent = new Intent(this, WebVewActivity.class);
//            intent.putExtra("url",url);
//            startActivity(intent);
            SrcRunable srcRunable = new SrcRunable(callBackmessage, "", watsonClient.getConversationContext(), handler);
            srcRunable.start();

        } else if (Constant.callbackTocallphone == actionType) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            Uri data = Uri.parse("tel:" + callBackmessage);
            intent.setData(data);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(this,"没有权限",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, RECORD_REQUEST_CODE);
                return;
            }
            sendStrMessage("拨打电话");
            startActivity(intent);

        }else if (Constant.show_chat_item_show_callinfoActivity== actionType) {

            Intent intent = new Intent(this,AddCallInfoActivity.class);
            startActivityForResult(intent,1);
        }





    }

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

           Object callnumber= watsonApplication.getWatsonClient().getConversationContext().get(Constant.conversatin_new_callNo_key);


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
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
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
            Log.i(TAG,String.format(getString(R.string.tts_toast_format),mPercentForBuffering, mPercentForPlaying));
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
            mPercentForPlaying = percent;
//            showTip(String.format(getString(R.string.tts_toast_format),
//                    mPercentForBuffering, mPercentForPlaying));
            Log.e(TAG,String.format(getString(R.string.tts_toast_format),
                    mPercentForBuffering, mPercentForPlaying));
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
        StringBuilder  sb =new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"GB2312\"?><speak>");
       sb.append(text);
       sb.append("</speak>");
       // text.replace("No","number");
        // 设置参数
        setParam();
        int code = mTts.startSpeaking(sb.toString(), mTtsListener);
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

        mTts.setParameter("ttp", "cssml");

        mTts.setParameter(SpeechConstant.TEXT_ENCODING, "GB2312");
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

    private void copyMessage(List<Message> listMessage,List<String> playList){
        for(Message msg:listMessage){
            if(msg.msgType == Constant.watsonType){
                if(TextUtils.isEmpty(msg.message)){continue;}
                playList.add(msg.message);
            }
        }
    }
    private  void  playText(){
        if(playList.size()>0) {
            String text = playList.get(0);
            playList.remove(0);
            stopSpeeking();
            startSpeeker(text);
        }
    }
    private  void  playText(String text){
            stopSpeeking();
            playList.clear();
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
}



