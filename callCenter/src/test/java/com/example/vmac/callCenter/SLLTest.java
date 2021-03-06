package com.example.vmac.callCenter;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * wflinzhiyong@163.com
 * Created by lzy on 2016/4/14.
 */
public class SLLTest {
@Test
    public void doTest() throws Exception {
        new SLLTest().Test("https://www.ibm.com/support/knowledgecenter/en/POWER8/p8ecs/p8ecs_82x_84x_parts.htm?view=embed");
    }
    public void Test(String str) throws Exception {

        URL url = new URL(str);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        SSLSocketFactory oldSocketFactory = null;
        HostnameVerifier oldHostnameVerifier = null;

        boolean useHttps = str.startsWith("https");
        if (useHttps) {
            HttpsURLConnection https = (HttpsURLConnection) connection;
            oldSocketFactory = trustAllHosts(https);
            oldHostnameVerifier = https.getHostnameVerifier();
            https.setHostnameVerifier(DO_NOT_VERIFY);
           InputStream inputStream = https.getInputStream();
           BufferedReader  in = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line;
            String result="" ;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }
    }

    /**
     * 覆盖java默认的证书验证
     */
    private static final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }};

    /**
     * 设置不验证主机
     */
    private static final HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };

    /**
     * 信任所有
     * @param connection
     * @return
     */
    private static SSLSocketFactory trustAllHosts(HttpsURLConnection connection) {
        SSLSocketFactory oldFactory = connection.getSSLSocketFactory();
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory newFactory = sc.getSocketFactory();
            connection.setSSLSocketFactory(newFactory);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oldFactory;
    }

}