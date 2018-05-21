package com.example.vmac.house;

import com.google.gson.Gson;
import com.ibm.watson.developer_cloud.util.GsonSingleton;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    public void addition_isCorrect() throws Exception {
        Socket socket;
        try {
            // socket = new Socket("127.0.0.1", 7999);
            socket = new Socket("chen-104", 8081);
            OutputStream out = socket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                    out));

                writer.write("713375GM108");
                writer.write("\n");

            writer.flush();
            InputStream in = socket.getInputStream();
            BufferedReader read = new BufferedReader(new InputStreamReader(in));
            String re = read.readLine();
            System.out.println("  收到  " + re);
            socket.close();
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    @Test
    public void testDateFormat(){
        String DATE_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSX";
        SimpleDateFormat iso8601DateFormatter = new SimpleDateFormat(DATE_8601);
        Gson gson = GsonSingleton.getGson();
    }
}