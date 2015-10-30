/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.gbdx;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.Properties;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.json.JSONObject;

/**
 *
 * @author jthiel
 */
public class GBDxS3Credentials {
    
    
    
    public static Properties getS3Credentials(String auth_header) {
        Properties creds = new Properties();
        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        Request request = new Request.Builder()
                .url("https://geobigdata.io/s3creds/v1/prefix")
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("authorization", auth_header)
                .build();
        
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            
            creds = parseResponse(body);

            
        } catch (IOException io) {
            System.out.println("ERROR!");
            System.out.println(io.getMessage());
        }
        
        return creds;
    }
    
    private static Properties parseResponse(String json) {
            Properties creds = new Properties();
            
            JSONObject obj = new JSONObject(json);
            
            String access_key = obj.getString("S3_access_key");
            creds.put("S3_access_key",access_key);
            //System.out.println("S3_access_key: " + access_key);
            
            String secret_key = obj.getString("S3_secret_key");
            creds.put("S3_secret_key",secret_key);
            //System.out.println("S3_secret_key: " + secret_key);
            
            String session_token = obj.getString("S3_session_token");
            creds.put("S3_session_token",session_token);
            //System.out.println("S3_session_token: " + session_token);
            
            String bucket = obj.getString("bucket");
            creds.put("bucket",bucket);
            //System.out.println("bucket: " + bucket);
            
            String prefix = obj.getString("prefix");
            creds.put("prefix",prefix);
            //System.out.println("prefix: " + prefix);                                   
            
            return creds;
            
    }
    
    public static void main(String[] args) {
        String auth_token = "Bearer X7jJnsxBq5IPg5Fxtqp9fTjRFPRdi9";
        Properties s3_creds = getS3Credentials(auth_token);
        System.out.println(s3_creds.toString());
//        parseResponse("{\n"
//                + "  \"S3_secret_key\": \"FxKBPWyZgIDfra/oMT0X3jeKb5F7S6c9mk51altD\",\n"
//                + "  \"prefix\": \"b265b97f-30f2-48bd-9bc5-84c7c7eb0e06\",\n"
//                + "  \"bucket\": \"gbd-customer-data\",\n"
//                + "  \"S3_access_key\": \"ASIAJY5QWJEMHXV26JZQ\",\n"
//                + "  \"S3_session_token\": \"AQoDYXdzEB4a0AMgQ79BhEGFhZu2C+4Kh7Dfb49evauABgW2ebKLHuqCClgYA3TvN0fTtwussepj6SLc17M3h7jNuYxQrGR0VRICjm9QYqN8zX90Z0e4Ehg6Uz6hugJmYy4VrdaMG7fTTAjDjTmeZI331hxDIN4PTqt5nv7SUKvVRtXaZbzsz1PJga18n8U+xH3BPT+I3e1h3rihUYuFxGnmiuCHncSKdgwsqYuIq7ZCprAPZXu8Er4Bes36OtUVcStTaFLAV3hNEShRmsZi6X0xSSwkR9TJ0e5Wg5kFva8nISLPnsFQ8xyebFY8pIvQFXtWZwhtAoSRRk1H3LeZThDWG1kydyxWC2yg8yKyVTUX96P9uHwWwwTscVgAnKVFAgomjFGNWMJUJ+CuNYm/ujOqxTR1yY1c/P8fhb9UgV9/7FO8NOUoYgsi1kEXrX2l7ZiygewhfrjycT0L7QQNiAB/Z38ZER6995TCSACo5JknSjJoEUzQTjf7OccMbt6Fup11NhA0hFKe0TOd1kNFxLjvP6js0V8jWq5mgplxAxGPZ/Th/G+2KUC0Mk7tuJUze46/cIbyN1iwDZcKXibgLqgyyL8OizyRPCsW+cpB8mG2MxGC2B1vDLyBACCV44muBQ==\"\n"
//                + "}");
    }
}
