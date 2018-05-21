package com.example.vmac.callCenter.rcms;

import android.content.Context;

import com.example.vmac.callCenter.util.ValidateResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/18.
 */

public class RcmsClient {
    private Context context;
    private String hostName="127.0.0.1";
    private int port = 8001;
    private Socket socket ;
    public JsonObject getMsg(String typeModel){
        JsonObject jsonObject=new JsonObject();
        JsonParser jsonParser=new JsonParser();

//        try {
          //socket=new Socket(hostName,port);
          //BufferedReader reader=new BufferedReader( new InputStreamReader( socket.getInputStream()));
        //  BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        //  writer.write(typeModel);
        //  writer.flush();
          //String jsStr =reader.readLine();
            String jsStr ="{\"Model\":\"D40\",\"Instal\":\"05/01/30\",\"Warranty\":\"06/01/30\",\"Start\":\"14/09/01\",\"Stop\":\"99/99/99\",\"Customer\":\"0811501 \",\"Type\":\"PerCall\",\"Service\":\"N/A\",\"Number\":\"      \"}\n";
            JsonElement jsonElement= jsonParser.parse(jsStr);
          jsonObject=  jsonElement.getAsJsonObject();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return  jsonObject;
    } ;
//{"Model":"D40","Instal":"05/01/30","Warranty":"06/01/30","Start":"14/09/01","Stop":"99/99/99","Customer":"0811501 ","Type":"PerCall","Service":"N/A","Number":"      "}

    public ValidateResult ValidateSN(String SN){
        ValidateResult result=ValidateResult.getInstance();
        if(SN==null||SN.length()!=4){
            result.resulte=false;
            result.tipMsg="序列号格式不对";
        }else{
            result.resulte=true;
        }
        return  result;
    }

    public ValidateResult ValidateType(String type){
        ValidateResult result=ValidateResult.getInstance();
        if(type==null||type.length()!=4){
            result.resulte=false;
            result.tipMsg="类型格式不对";
        }else{
            result.resulte=true;
        }
        return  result;
    }

    public String checkMachinStatueBySerial(String serial){
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty(RcmsConstant.intentKey,RcmsConstant.IntentGetMachineState);
        jsonObject.addProperty(RcmsConstant.Data,serial);
        String  re=null;
        try {
            Socket socket =new Socket(hostName,port);
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
            writer.write(jsonObject.toString());
            writer.write("\n");
            writer.flush();


          String result =  bufferedReader.readLine();
            if(result!=null&&result.length()>25){
                JsonParser jsonParser =new JsonParser();
                JsonElement jsonElement =   jsonParser.parse(result);
                JsonObject resultJson=jsonElement.getAsJsonObject();
                String machinStopStatue=  resultJson.get(RcmsConstant.machinStop).getAsString();

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
