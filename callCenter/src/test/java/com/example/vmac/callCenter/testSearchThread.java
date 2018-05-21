package com.example.vmac.callCenter;

import com.example.vmac.callCenter.util.HTMLPaser;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.iflytek.speech.SynthesizeToUrlListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

/**
 * Created by Administrator on 2017/10/9.
 */

public class testSearchThread {

//   public void doPaser(){
//        CallInfoRunable ca=new CallInfoRunable(null,null);
//        String str="{\"hasCall\":\"True\",\"callNumber\":\"12312\",\"accountname\":\"移动\",\"customernumber\":\"00812724\",\"Serial\":\"82040600832E4\",\"type\":\"E8A\",\"startTime\":\"2017/09/1121:05\",\"callByNumber\":\"020-13802881468\",\"callByPeople\":\"SSR王少波/MS杨008\",\"sendEngineer\":\"SHAOBOWANGBE\",\"EngineerPhone\":\"18620800210\",\"faultDesc\":\"77P6498*2(DIMM)\",\"activitySummary\":\"ssronsite\",\"partNo\":\"0000077P6498\",\"partDesc\":\"1GBDIMM\"}\n";
//        JsonObject js= new JsonParser().parse(str).getAsJsonObject();
//
//        System.out.println(ca.getStr(js));
//    }

    public void testIp(){
//        JsonObject jsonObject=new JsonObject();
//        jsonObject.addProperty(RcmsConstant.intentKey,RcmsConstant.IntentGetMachineState);
//        jsonObject.addProperty(RcmsConstant.Data,"");
//        try {
//            Socket socket =new Socket(RcmsConstant.rcmsHostName,RcmsConstant.hostPort);
//
//            InputStream inputStream=socket.getInputStream();
//            OutputStream outputStream = socket.getOutputStream();
//            BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
//            BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
//            writer.write(jsonObject.toString());
//            writer.write("\n");
//            writer.flush();
//
//
//            String result =  bufferedReader.readLine();
//            socket.close();
//            System.out.println();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        InputStream inputStream=socket.getInputStream();
//        OutputStream outputStream = socket.getOutputStream();
//        BufferedWriter writer =new BufferedWriter(new OutputStreamWriter(outputStream));
//        BufferedReader bufferedReader =new BufferedReader(new InputStreamReader(inputStream));
//        writer.write(jsonObject.toString());
//        writer.write("\n");
//        writer.flush();
    }

    public void testString(){
        System.out.println("020340234".substring(0,4));
        System.out.println("020340234".substring(4));
        StringBuilder sb =new StringBuilder("020340234");
        System.out.println(sb.delete(2,4));
    }
    @Test

    public void TestHtml(){
        File  file = new File("page.html");
        String str="";
        try  {
            FileInputStream fileInputStream = new FileInputStream(file);
            int a=   fileInputStream.available();
            byte[] tmp=new byte[a];
            fileInputStream.read(tmp,0,a);
            str=new String(tmp,"utf-8");
            fileInputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(str);
      // str= HTMLPaser.sendGet("https://www.ibm.com/support/knowledgecenter/en/POWER6/arecs/arecsareanparts.htm?view=embed","");

 //       str= HTMLPaser.sendGet("https://www.baidu.com","");

        System.out.println(str);
        if(str!=null&&!"".equals(str)){
            try {
                if(!file.exists()){
                    file.createNewFile();
                }
                FileOutputStream outputStream =new FileOutputStream(file);
                outputStream.write(str.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        Document doc = Jsoup.parse(str);
        try {
          //  doc  =   Jsoup.connect("https://www.ibm.com/support/knowledgecenter/en/POWER8/p8ecs/p8ecs_82x_84x_parts.htm?view=embed").get();

          String html=  doc.html();
            html=HTMLPaser.sendGet("https://www.ibm.com/support/knowledgecenter/en/POWER8/p8ecs/p8ecs_82x_84x_parts.htm?view=embed","");
            FileOutputStream outputStream =new FileOutputStream(file);
            outputStream.write(html.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elements=  doc.select("div[role=main]");
        System.out.println(elements.select("span.keyword"));
    }

}
