package com.example.vmac.callCenter.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2018/1/19.
 */

public class TulIngRobotUtil {
    public static JsonParser jsonParser =new JsonParser() ;
    public static String sendMessage(String msg) throws Exception{
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        OkHttpClient client = null;
        client = new OkHttpClient();
        Gson gson=new Gson();
        JsonObject json = new JsonObject();
        json.addProperty("key", "c28a0b90b97a42f7956c53280f26b456");
        json.addProperty("info", msg);
//        json.addProperty("loc", "");
        json.addProperty("userid", "chen104");

//		“key”: “APIKEY”,
//		“info”: “今天天气怎么样”，
//		“loc”：“北京市中关村”，
//		“userid”：“123456”

        RequestBody body = RequestBody.create(JSON, json.toString());
        Request request = new Request.Builder()
                .url("http://www.tuling123.com/openapi/api")
                .post(body)
                .build();
        Response response = client.newCall(request).execute();
//		  System.out.println( response);
        if(response!=null&&response.isSuccessful()){

            String re =response.body().string();
             JsonObject jsonObject =   jsonParser.parse(re).getAsJsonObject();
             if(jsonObject.get("code").getAsInt()==100000) {
                 String text = jsonObject.get("text").getAsString();
                 return text;
             }
        }

        return  null;
    }

    public static void main(String[] args) {

    }
}