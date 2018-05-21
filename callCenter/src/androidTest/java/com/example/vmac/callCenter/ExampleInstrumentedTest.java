package com.example.vmac.house;

import android.content.Context;
import android.os.Message;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Xml;

import com.example.vmac.callCenter.rcms.RcmsConstant;
import com.example.vmac.callCenter.watson.conversation.Constant;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.ibm.watson.developer_cloud.util.GsonSingleton;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    String DATE_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    SimpleDateFormat iso8601DateFormatter = new SimpleDateFormat(DATE_8601);
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();
        String DATE_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
        SimpleDateFormat iso8601DateFormatter = new SimpleDateFormat(DATE_8601);
        Gson gson = GsonSingleton.getGson();
        assertEquals("com.example.vmac.chatbot", appContext.getPackageName());
    }


    public void queryCall () throws  Exception{
        Message msg =new Message();
        String intentAction=    RcmsConstant.IntentGetCallByNumber;
        String data="p4jjgwl";
        JsonObject jsonObject=new JsonObject();
        jsonObject.addProperty(RcmsConstant.intentKey,intentAction);
        jsonObject.addProperty(RcmsConstant.Data,data);
        String  re=null;

        Socket socket =new Socket();
        InetSocketAddress inetSocketAddress = new InetSocketAddress("192.168.43.166", 8081);

        socket.connect(inetSocketAddress,5000);
            InputStream inputStream=socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();
            BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")));
            writer.write(jsonObject.toString());
            writer.write("\n");
            writer.flush();
            String result =  bufferedReader.readLine();
            System.out.println(""+result);
        socket.close();

    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body).header("X-ROBOSALES-AUTHORIZATION", "35171262c24748f2bd825537cd87441f")
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public void testRequest() throws IOException{
        String jsonString="{ \"input\" : { \"id\" : \""+System.currentTimeMillis()+"\", \"source\" : \"Robot\", \"type\" : \"text\", \"text\" : \"你好\" }, \"user_id\" : \"sanbot_abc\" }";
        String url="https://crl.ptopenlab.com:8800/robosales/robot-proxy/api/bot/message";
        String reString = post(url, jsonString);
        System.out.println(reString);

    }

    public void testPoll() throws IOException {
        String jsonString="{ \"poll\" : { \"id\" : \"1484274661654\", \"source\" : \"Robot\" ,\"create_time\":\""+iso8601DateFormatter.format(new Date())+"\"}, \"user_id\" : \"sanbot_abc\" }";
        String url="https://crl.ptopenlab.com:8800/robosales/robot-proxy/api/bot/poll";
        String reString = post(url,jsonString);
        System.out.println(reString);
    }
    @Test
    public void  testuserprofile() throws IOException {
      String url="http://114.215.171.228:3080/api/robosales/set/userprofile?data={\"age\":31,\"age_confidence\":0.99,\"gender\":\"male\",\"gender_confidence\":0.99}";
        String reString = runget(url);
        System.out.println(reString);
    }


    String runget(String url) throws IOException {

        Request request = new Request.Builder()
                .url(url).header("X-ROBOSALES-AUTHORIZATION", "35171262c24748f2bd825537cd87441f")
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

}
