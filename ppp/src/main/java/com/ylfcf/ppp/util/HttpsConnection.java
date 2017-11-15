package com.ylfcf.ppp.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2017/11/8.
 */

public class HttpsConnection {

    private static TrustManager myX509TrustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

    };

    public static String getConnection(String paramURL) throws Exception{
        return null;
    }

    public static String postConnection(String url,String param,String data) throws Exception{
        String result = null;


        //ʹ�ô˹��߿��Խ���ֵ�Ա����"Key=Value&amp;Key2=Value2&amp;Key3=Value3&rdquo;��ʽ���������
        String requestParam = encryptPrams(param);
        try {
            //����SSLContext
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, new TrustManager[]{myX509TrustManager}, null);

            //������
            //Ҫ���͵�POST����url?Key=Value&amp;Key2=Value2&amp;Key3=Value3����ʽ
            URL requestUrl = new URL(url + "?" + requestParam);
            HttpsURLConnection httpsConn = (HttpsURLConnection)requestUrl.openConnection();

            //�����׽ӹ���
            httpsConn.setSSLSocketFactory(sslcontext.getSocketFactory());

            //��������
            httpsConn.setRequestMethod("POST");
            httpsConn.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(
                    httpsConn.getOutputStream());
            if (data != null)
                out.writeBytes(data);

            out.flush();
            out.close();

            //��ȡ������
            BufferedReader in = new BufferedReader(new InputStreamReader(httpsConn.getInputStream()));
            int code = httpsConn.getResponseCode();
            if (HttpsURLConnection.HTTP_OK == code){
                String temp = in.readLine();
                /*���ӳ�һ���ַ���*/
                while (temp != null) {
                    if (result != null)
                        result += temp;
                    else
                        result = temp;
                    temp = in.readLine();
                }
            }
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * ���������м��ܴ���
     * @param params
     * @return
     */
    private static String encryptPrams(String params) throws Exception{
        if(params == null || (params.contains("version_code") & params.contains("os_type"))){
            return params;
        }
        StringBuffer sb = new StringBuffer();
        String[] parmsStrArr1 = params.split("&");
        for (int i=0;i<parmsStrArr1.length;i++) {
            String[] keyvaluesArr = parmsStrArr1[i].split("=");
            if(keyvaluesArr[0].startsWith("_") && keyvaluesArr[0].endsWith("_") && !"_URL_".equals(keyvaluesArr[0])){
                sb.append(keyvaluesArr[0]).append("=").append(keyvaluesArr[1]);
            }else{
                sb.append(keyvaluesArr[0]).append("=").append(URLEncoder.encode(SimpleCrypto.encryptAES(keyvaluesArr[1],"akD#K2$k=s2kh?DL"),"utf-8")).append("&");
            }
        }
        sb.append("_FROM_=").append(URLEncoder.encode(SimpleCrypto.encryptAES("android","akD#K2$k=s2kh?DL"),"utf-8"));
        return sb.toString();
    }
}
