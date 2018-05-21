package com.example.vmac.callCenter.view;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vmac.callCenter.R;
import com.example.vmac.callCenter.app.WatsonApplication;
import com.example.vmac.callCenter.rcms.CallInfoEntity;
import com.example.vmac.callCenter.rcms.CallInfoKey;
import com.example.vmac.callCenter.rcms.RcmsConstant;
import com.example.vmac.callCenter.util.AutoCompleteShareUtil;
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
import java.util.Map;


public class AddCallInfoActivity extends AppCompatActivity {
    String TAG = AddCallInfoActivity.class.getSimpleName();
    private boolean isspeaking= false;

    AutoCompleteShareUtil autoCompleteShareUtil;
    public  final  static  String SUCCES="succes";
    public  final  static  String FALSE="false";
    public final  static  String Exception="Exception";
    private int resultCode=0;
    Button bt_submit;
    Button bt_cansole;
    Button bt_save;
    WatsonApplication watsonApplication ;
    MyTask myTask;
    SharedPreferences   sharedPreferences ;
    SharedPreferences.Editor editor;
    Gson gson=new Gson();
    private int machineStatus=0;
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
//    EditText dt_callno;
//    EditText dt_custom_no;
    AutoCompleteTextView at_tv_custom_name;
    AutoCompleteTextView dt_custom_address;
    AutoCompleteTextView dt_telephone;
    AutoCompleteTextView dt_contacts;
    AutoCompleteTextView dt_machine_type;
    AutoCompleteTextView dt_machine_sn;
//    EditText dt_engineer;
//    EditText dt_engineer_telephone;

    Spinner dt_call_level;
    AutoCompleteTextView dt_fault_detail;
    AutoCompleteTextView dt_call_remark;
    AutoCompleteTextView dt_part;
    TextView tv_addinfo_fail_reson;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autoCompleteShareUtil =new AutoCompleteShareUtil(this);

        setContentView(R.layout.activity_callinfo_add);
        bt_submit = (Button) findViewById(R.id.bt_callinfo_submit);
        bt_cansole = (Button) findViewById(R.id.bt_callinfo_cansole);
        bt_save = (Button) findViewById(R.id.bt_callinfo_save);

        tv_addinfo_fail_reson = (TextView) findViewById(R.id.tv_addcallinfo_fail_reason);
        watsonApplication=(WatsonApplication) this.getApplication();
        sharedPreferences = getSharedPreferences("callInfo",MODE_PRIVATE);
        editor=sharedPreferences.edit();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        bt_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               addCallInfo();


            }
        });




        bt_cansole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(myTask!=null){
//                    if(!myTask.isCancelled()){
//                        myTask.cancel(true);
//
//                    }
//                }
            //    onBackPressed();
                if(sharedPreferences!=null) {


                    editor.clear();
                    editor.commit();
                }
                Intent mIntent = new Intent();
                mIntent.putExtra("send","取消了");
                // 设置结果，并进行传送
                AddCallInfoActivity.this.setResult(Constant.resultCode_no_call, mIntent);
                onBackPressed();
            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCallInfo();
                Intent mIntent = new Intent();
//                mIntent.putExtra(Constant.conversatin_new_callNo_key, entity.getCallNo());
//                mIntent.putExtra(Constant.conversatin_call_status_key,Constant.conversatin_call_status_hasCall);
                mIntent.putExtra("send","保存");
                // 设置结果，并进行传送
                AddCallInfoActivity.this.setResult(Constant.resultCode_no_call, mIntent);
                onBackPressed();
            }
        });
        init();
        initTile();
    }



    public void init(){
//         dt_callno= (EditText) findViewById(R.id.dt_callinfo_call_no);
//        String callNO=sharedPreferences.getString(CallInfoKey.callNumberKey,"");
//        dt_callno.setText(callNO);

//         dt_custom_no= (EditText) findViewById(R.id.dt_callinfo_custom_no);
//        String custonNo=sharedPreferences.getString(CallInfoKey.customerNumberKey,"");
//         dt_custom_no.setText(custonNo);


         at_tv_custom_name= (AutoCompleteTextView) findViewById(R.id.dt_callinfo_custom_name);
         String[]  mSearchHistoryArray =new String[]{"移动","招商","电信","中兴","联通"};
         ArrayAdapter<String> mSearchAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                mSearchHistoryArray
        );
        ArrayAdapter customNameAdapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.CustomerName);
        at_tv_custom_name.setAdapter(customNameAdapter);

//        String custom_name=sharedPreferences.getString(CallInfoKey.customerNameKey,"");
//        dt_custom_name.setText(custom_name);

         dt_custom_address= (AutoCompleteTextView) findViewById(R.id.dt_callinfo_custom_address);
        String custom_address=sharedPreferences.getString(CallInfoKey.customAddressKey,"");
        dt_custom_address.setText(custom_address);
        ArrayAdapter custom_adress_adapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.CustomerAddress);
        dt_custom_address.setAdapter(custom_adress_adapter);







         dt_telephone= (AutoCompleteTextView) findViewById(R.id.dt_callinfo_telephone);
        String  telephone=sharedPreferences.getString(CallInfoKey.telephoneKey,"");
        dt_telephone.setText(telephone);
        ArrayAdapter telephoneAdapter_adapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.Telephone);
        dt_telephone.setAdapter(telephoneAdapter_adapter );



        dt_contacts= (AutoCompleteTextView) findViewById(R.id.dt_callinfo_contacts);
        String  contacts=sharedPreferences.getString(CallInfoKey.contactsKey,"");
        dt_contacts.setText(contacts);
        ArrayAdapter contactAdapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.Contacts);
        dt_contacts.setAdapter(contactAdapter );


         dt_machine_type= (AutoCompleteTextView) findViewById(R.id.dt_callinfo_machine_type);
        String  machine_type=sharedPreferences.getString(CallInfoKey.typeKey,"");
        dt_machine_type.setText(machine_type);
        ArrayAdapter machicnTypeAdapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.MachineType);
        dt_machine_type.setAdapter( machicnTypeAdapter);

//        dt_machine_type.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus) {
//                    AvaliableMachine();
//                }
//            }
//        });


         dt_machine_sn= (AutoCompleteTextView) findViewById(R.id.dt_callinfo_machine_sn);
        String  machine_sn=sharedPreferences.getString(CallInfoKey.serialKey,"");
        dt_machine_sn.setText(machine_sn);
        ArrayAdapter machicnSNAdapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.MachineSn);
        dt_machine_sn.setAdapter( machicnSNAdapter);
//        dt_machine_sn.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus) {
//                    AvaliableMachine();
//                }
//            }
//        });

//         dt_engineer= (EditText) findViewById(R.id.dt_callinfo_engineer);
//        String  engineer=sharedPreferences.getString(CallInfoKey.sendEngineerKey,"");
//        dt_engineer.setText(engineer);
//
//         dt_engineer_telephone= (EditText) findViewById(R.id.dt_callinfo_engineer_telephone);
//        String  engineer_telephone=sharedPreferences.getString(CallInfoKey.engineerPhoneKey,"");
//        dt_engineer_telephone.setText(engineer_telephone);


       dt_call_level = (Spinner) findViewById(R.id.dt_callinfo_level);
       LevelAdapter levelAdapter =new LevelAdapter(this);
       dt_call_level.setAdapter(levelAdapter);
//        String  level=sharedPreferences.getString(CallInfoKey.callLevelKey,"");
//        dt_call_level.setText(level);


       dt_fault_detail =(AutoCompleteTextView) findViewById(R.id.dt_callinfo_fault_detail);
        String  fault_detail=sharedPreferences.getString(CallInfoKey.faultDetailKey,"");
        dt_fault_detail.setText(fault_detail);
        ArrayAdapter faultDetailAdapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.FaultDetail);
        dt_fault_detail.setAdapter( faultDetailAdapter );



        dt_call_remark=(AutoCompleteTextView) findViewById(R.id.dt_callinfo_remark);
        String  call_remark=sharedPreferences.getString(CallInfoKey.remarkeKey,"");
        dt_call_remark.setText(call_remark);
        ArrayAdapter remarkAdapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.Remark);
        dt_call_remark.setAdapter( remarkAdapter);


        dt_part=(AutoCompleteTextView) findViewById(R.id.dt_callinfo_fault_part);
        String  faultPart=sharedPreferences.getString(CallInfoKey.faultPartKey,"");
        dt_part.setText(faultPart);
        ArrayAdapter partAdapter = autoCompleteShareUtil.getStringAdapter(autoCompleteShareUtil.FaultPart);
        dt_part.setAdapter( partAdapter );

    }
    public void saveCallInfo(){
//        SharedPreferences.Editor edit=sharedPreferences.edit();

//        String string = dt_callno.getText().toString();
//        edit.putString(CallInfoKey.callNumberKey,string);

//
//        String  string =  dt_custom_no.getText().toString();
//        editor.putString(CallInfoKey.customerNumberKey,string );



        String    string= at_tv_custom_name.getText().toString();
        editor.putString(CallInfoKey.customerNameKey,string);


       string= dt_custom_address.getText().toString();
       editor.putString(CallInfoKey.customAddressKey,string);


//      string=  dt_custom_enduser.getText().toString();
//         editor.putString(CallInfoKey.enduserKey,string);


       string= dt_telephone.getText().toString();
         editor.putString(CallInfoKey.telephoneKey,string);


       string= dt_contacts.getText().toString();
        editor.putString(CallInfoKey.contactsKey,string);


       string= dt_machine_type.getText().toString();
         editor.putString(CallInfoKey.typeKey,string);


       string= dt_machine_sn.getText().toString();
         editor.putString(CallInfoKey.serialKey,string);


//      string=  dt_engineer.getText().toString();
//        editor.putString(CallInfoKey.sendEngineerKey,string);
//
//
//       string= dt_engineer_telephone.getText().toString();
//        editor.putString(CallInfoKey.engineerPhoneKey,string);

//        EditText dt_call_level;
//        EditText dt_fault_detail;
//        EditText dt_call_remark;
//        EditText dt_part;
       string =dt_call_level.getSelectedItem().toString();
         editor.putString(CallInfoKey.callLevelKey,string);

        string =dt_fault_detail.getText().toString();
         editor.putString(CallInfoKey.faultDetailKey,string);

        string =dt_call_remark.getText().toString();
         editor.putString(CallInfoKey.remarkeKey,string);

        string =dt_part.getText().toString();
         editor.putString(CallInfoKey.faultPartKey,string);

         editor.commit();
    }

    private void addCallInfo(){
//        AvaliableMachine();
//        if(machineStatus==1){
//            Toast.makeText(this,"机器 没找到 ",Toast.LENGTH_LONG).show();
//            return;
//        }else if(machineStatus==2){
//            Toast.makeText(this,"机器 不在保 ",Toast.LENGTH_LONG).show();
//            return;
//        }

        CallInfoEntity entity =new CallInfoEntity();
        {

//            String string = dt_callno.getText().toString();
//            if (TextUtils.isEmpty(string)) {
//                Toast.makeText(this, "call no 不能为空", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            entity.setCallNo(string);

//            String string =dt_custom_no.getText().toString();
//            if(TextUtils.isEmpty(string)){
//                Toast.makeText(this,"客户编号不能为空",Toast.LENGTH_SHORT).show();
//                return  ;
//            }
//            entity.setCustomerNumber(string);

           String string =at_tv_custom_name.getText().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"客户名称不能为空",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setCustomerName(string);


            string =dt_custom_address.getText().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"机器所在城市不能为空",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setCustomerAddress(string);

//            string =dt_custom_enduser.getText().toString();
//            if(TextUtils.isEmpty(string)){
//                Toast.makeText(this,"enduser 不能为空",Toast.LENGTH_SHORT).show();
//                return  ;
//            }
//            entity.setEnduser(string);

            string =dt_contacts.getText().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"联系人 不能为空",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setContacts(string);


            string =dt_telephone.getText().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"联系电话不能为空",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setTelephone(string);


            string =dt_machine_type.getText().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"机器类型 不能为空",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setMachineType(string);

            string =dt_machine_sn.getText().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"机器序列号不能为空 ",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setMachineSn(string);

            string =dt_call_level.getSelectedItem().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"紧急程度不能为空 ,且只能是 1,2,3 ",Toast.LENGTH_SHORT).show();
                return  ;
            }else if(!("1".equals(string)||"2".equals(string)||"3".equals(string))){
                Toast.makeText(this,"紧急程度不能为空 ,且只能是 1,2,3 ",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setLevel(Integer.valueOf(string));

            string =dt_fault_detail.getText().toString();
            if(TextUtils.isEmpty(string)){
                Toast.makeText(this,"故障说明不能为空 ",Toast.LENGTH_SHORT).show();
                return  ;
            }
            entity.setFaultDetail(string);

            string = dt_call_remark.getText().toString();
           entity.setRemark(string);

           string = dt_part.getText().toString();
           entity.setFaultPart(string);


//            string =dt_engineer.getText().toString();
//            if(TextUtils.isEmpty(string)){
//                Toast.makeText(this,"工程师 不能为空 ",Toast.LENGTH_SHORT).show();
//                return  ;
//            }
//            entity.setIbmEngineer(string);

//            string =dt_engineer_telephone.getText().toString();
//            if(TextUtils.isEmpty(string)){
//                Toast.makeText(this,"工程师联系电话 不能为空 ",Toast.LENGTH_SHORT).show();
//                return  ;
//            }
//            entity.setEngineerTelephone(string);
            bt_submit.setEnabled(false);
            myTask =new MyTask();
            myTask.execute(entity);


        }
    }

    private  void initTile(){
        TextView machine_type_title = (TextView) findViewById(R.id.dt_callinfo_machine_type_title);
        TextView machine_sn_title = (TextView) findViewById(R.id.dt_callinfo_machine_sn_title);
        TextView contacts_title = (TextView) findViewById(R.id.dt_callinfo_contacts_title);
        TextView telephone_title= (TextView) findViewById(R.id.dt_callinfo_telephone_title);
        TextView address_title = (TextView) findViewById(R.id.dt_callinfo_custom_address_title);
        TextView name_title = (TextView) findViewById(R.id.dt_callinfo_custom_name_title);
        TextView level_title = (TextView) findViewById(R.id.dt_callinfo_level_title);
        TextView detail_title = (TextView) findViewById(R.id.dt_callinfo_fault_detail_title);

        initTextColor(machine_type_title);
        initTextColor(machine_sn_title);
        initTextColor(contacts_title );
        initTextColor(telephone_title);
        initTextColor(address_title);
        initTextColor(name_title);
        initTextColor(level_title);
        initTextColor(detail_title );

    }




    private class MyTask extends AsyncTask<CallInfoEntity, Integer,JsonObject> {
        @Override
        protected JsonObject doInBackground(CallInfoEntity... callInfoEntities) {


            CallInfoEntity callInfoEntity = callInfoEntities[0];
            Message msg = new Message();

            JsonObject jsonObject = new JsonObject();
            Gson Gson = new Gson();
            String str = Gson.toJson(callInfoEntity);
            JsonParser jsonParser = new JsonParser();
            JsonElement jsonElement = jsonParser.parse(str);
            jsonObject.addProperty(RcmsConstant.intentKey, RcmsConstant.IntentAddCall);
            jsonObject.add(RcmsConstant.Data, jsonElement);
            String re = null;
            try {
                Socket socket = new Socket(RcmsConstant.rcmsHostName, RcmsConstant.hostPort);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")));
                writer.write(jsonObject.toString());
                writer.write("\n");
                writer.flush();


                String result = bufferedReader.readLine();

                if (result != null && result.length() > 0) {

                    jsonElement = jsonParser.parse(result);
                    JsonObject resultJson = jsonElement.getAsJsonObject();
                    Boolean stat = resultJson.get(RcmsConstant.stat).getAsBoolean();
                    try {
                        if (stat) {
                            JsonObject callinfo = resultJson.get(RcmsConstant.Data).getAsJsonObject();
                            CallInfoEntity entity = gson.fromJson(callinfo, CallInfoEntity.class);
                            saveHestory(entity);
                        }
                    }catch (Exception e){
                        Log.e(TAG,e.getMessage());
                    }

                     return  resultJson;

                } else {
                    return null;
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
            return null;
        }

        @Override
        protected void onPostExecute(JsonObject s) {
            Map<String,Object> conversationContext= watsonApplication.getWatsonClient().getConversationContext();
            super.onPostExecute(s);

            if (s!=null) {

                Boolean stat = s.get(RcmsConstant.stat).getAsBoolean();
                if(stat){
                    Toast.makeText(watsonApplication, "添加成功", Toast.LENGTH_LONG).show();
                    editor.clear();
                    editor.commit();
                   JsonObject callinfo =  s.get(RcmsConstant.Data).getAsJsonObject();
                 CallInfoEntity entity =  gson.fromJson(callinfo,CallInfoEntity.class);
                    if(conversationContext!=null) {
                        conversationContext.put(Constant.conversatin_new_callNo_key, entity.getCallNo());
                        conversationContext.put(Constant.conversatin_call_status_key, Constant.conversatin_call_status_hasCall);
                    }
                    Intent mIntent = new Intent();
                    mIntent.putExtra(Constant.conversatin_new_callNo_key, entity.getCallNo());
                    mIntent.putExtra(Constant.conversatin_call_status_key,Constant.conversatin_call_status_hasCall);

                    mIntent.putExtra("send", "已保存");
                    // 设置结果，并进行传送
                    AddCallInfoActivity.this.setResult(Constant.resultCode_new_call, mIntent);
                    onBackPressed();
                }else {
                    JsonElement jreson = s.get(RcmsConstant.reson);
                    if(jreson!=null){
                        String reson = jreson.getAsString();
                        tv_addinfo_fail_reson.setText(reson);
                        tv_addinfo_fail_reson.setVisibility(View.VISIBLE);
                        Log.i(TAG,reson);
                    }
                    Toast.makeText(watsonApplication, "添加失败", Toast.LENGTH_LONG).show();

                }




//                else if (FALSE.equals(s)) {
//
//                }
            }  else if (s==null) {
                Toast.makeText(watsonApplication, "服务器开小差ing ……", Toast.LENGTH_LONG).show();
                tv_addinfo_fail_reson.setText(R.string.not_connection_service);
                tv_addinfo_fail_reson.setVisibility(View.VISIBLE);
            }

            bt_cansole.setEnabled(true);
        }


        private class UpdateCallInfoTask extends AsyncTask<CallInfoEntity, Integer, JsonObject > {
            @Override
            protected JsonObject doInBackground(CallInfoEntity... callInfoEntities) {


                CallInfoEntity callInfoEntity = callInfoEntities[0];
                Message msg = new Message();

                JsonObject jsonObject = new JsonObject();
                Gson Gson = new Gson();
                String str = Gson.toJson(callInfoEntity);
                JsonParser jsonParser = new JsonParser();
                JsonElement jsonElement = jsonParser.parse(str);
                jsonObject.addProperty(RcmsConstant.intentKey, RcmsConstant.IntentUpdateCallByNumber);
                jsonObject.add(RcmsConstant.Data, jsonElement);
                String re = null;
                try {
                    Socket socket = new Socket(RcmsConstant.rcmsHostName, RcmsConstant.hostPort);
                    InputStream inputStream = socket.getInputStream();
                    OutputStream outputStream = socket.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")));
                    writer.write(jsonObject.toString());
                    writer.write("\n");
                    writer.flush();


                    String result = bufferedReader.readLine();

                    if (result != null && result.length() > 0) {

                        jsonElement = jsonParser.parse(result);
                        JsonObject resultJson = jsonElement.getAsJsonObject();
                        Boolean stat = resultJson.get(RcmsConstant.stat).getAsBoolean();
                        return  resultJson;
                    }
                    jsonObject=new JsonObject();
                    jsonObject.addProperty(RcmsConstant.stat,false);
                    jsonObject.addProperty("reson","返回null");
                } catch (IOException e) {
                    e.printStackTrace();
                    jsonObject.addProperty(RcmsConstant.stat,false);
                    jsonObject.addProperty("reson",e.getMessage());
                }
                return  jsonObject;

            }

            @Override
            protected void onPostExecute(JsonObject s) {
                Map<String,Object> conversationContext= watsonApplication.getWatsonClient().getConversationContext();
                super.onPostExecute(s);
//                if (SUCCES.equals(s)) {
//                    Toast.makeText(watsonApplication, "添加成功", Toast.LENGTH_LONG).show();
//                } else if (FALSE.equals(s)) {
//                    Toast.makeText(watsonApplication, "添加失败", Toast.LENGTH_LONG).show();
//                } else if (Exception.equals(s)) {
//                    Toast.makeText(watsonApplication, "服务器开小差ing ……", Toast.LENGTH_LONG).show();
//                }
                if(s.getAsJsonObject(RcmsConstant.stat).getAsBoolean()){
                    Toast.makeText(watsonApplication, "更新成功 ", Toast.LENGTH_LONG).show();



                }else {
                    if(s.get("reson")!=null){
                        Toast.makeText(watsonApplication, "更新失败 "+s.get("reson").toString(), Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(watsonApplication, "更新失败 ", Toast.LENGTH_LONG).show();
                    }
                }

            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish(); // back button
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initTextColor(TextView textView){
        SpannableStringBuilder builder = new SpannableStringBuilder(textView.getText().toString());
        ForegroundColorSpan redSpan = new ForegroundColorSpan(Color.RED);
        builder.setSpan(redSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(builder);
    }

    private void AvaliableMachine(){
      String machinesn=  dt_machine_sn.getText().toString();
      String machineType =dt_machine_type.getText().toString();
      if(machinesn.length()==7&&machineType.length()==4){
          AvaliableMachineTask task =new AvaliableMachineTask();
          task.execute(machineType + machinesn);
      }else{

      }
    }
    private class AvaliableMachineTask extends AsyncTask<String , Integer, JsonObject >{

        @Override
        protected JsonObject doInBackground(String... machineSns) {
            String machineSn= machineSns[0];
            String intentAction=RcmsConstant.IntentGetMachineState;
            com.example.vmac.callCenter.Message msg = new com.example.vmac.callCenter.Message();

            JsonObject jsonObject=new JsonObject();
            jsonObject.addProperty(RcmsConstant.intentKey,intentAction);
            jsonObject.addProperty(RcmsConstant.Data,machineSn);
            String  re=null;
            Message send =new Message();
            try {
                Socket socket =new Socket(RcmsConstant.rcmsHostName,RcmsConstant.hostPort);
                InputStream inputStream=socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
                BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
                writer.write(jsonObject.toString());
                writer.write("\n");
                writer.flush();


                String result =  bufferedReader.readLine();
                socket.close();


                if(result!=null&&result.length()>0){


                    JsonParser jsonParser =new JsonParser();
                    JsonElement jsonElement =   jsonParser.parse(result);
                    JsonObject resultJson=jsonElement.getAsJsonObject();
                    Boolean state=    resultJson.get(RcmsConstant.stat).getAsBoolean();
                    return resultJson;

                }else {

                    jsonObject=new JsonObject();
                    jsonObject.addProperty(RcmsConstant.stat,false);
                    jsonObject.addProperty(RcmsConstant.reson,"返回 null");
                }



            } catch (IOException e) {
                e.printStackTrace();

                jsonObject=new JsonObject();
                jsonObject.addProperty(RcmsConstant.stat,false);
                jsonObject.addProperty(RcmsConstant.reson,e.getMessage());
            }

            return jsonObject;
        }

        @Override
        protected void onPostExecute(JsonObject jsonObject) {
            super.onPostExecute(jsonObject);
            if(!jsonObject.get(RcmsConstant.stat).getAsBoolean()){
                JsonElement js = jsonObject.get(RcmsConstant.reson);
                Toast.makeText(watsonApplication, "没找到机器 ", Toast.LENGTH_LONG).show();

                machineStatus=1;
            }else{
                if(!jsonObject.get(RcmsConstant.isGuarantee).getAsBoolean()){

                    Toast.makeText(watsonApplication, "机器 不在保", Toast.LENGTH_LONG).show();
                    machineStatus=2;
                }else{

                }
            }
        }
    }



    private void saveHestory(CallInfoEntity entity){
        String machineType = entity.getMachineType();
        autoCompleteShareUtil.saveHestoryValue(autoCompleteShareUtil.MachineType,machineType);
        String machineSn  = entity.getMachineSn();
        autoCompleteShareUtil.saveHestoryValue(autoCompleteShareUtil.MachineSn,machineSn);
        String  connector = entity.getContacts();//联系人
        autoCompleteShareUtil.saveHestoryValue(autoCompleteShareUtil.Contacts,connector);
        String telephone = entity.getTelephone();
        autoCompleteShareUtil.saveHestoryValue(autoCompleteShareUtil.Telephone,telephone);
        String city = entity.getCustomerAddress();

        autoCompleteShareUtil.saveHestoryValue(autoCompleteShareUtil.CustomerAddress,city);
        String customName = entity.getCustomerName();
        autoCompleteShareUtil.saveHestoryValue(autoCompleteShareUtil.CustomerName,customName);

        String fault_detail = entity.getFaultDetail();
        autoCompleteShareUtil.saveHestoryValue(autoCompleteShareUtil.FaultDetail,fault_detail);

    }

}




