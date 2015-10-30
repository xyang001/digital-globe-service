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
public class GBDxCredentialManager {

    private static final Properties s3_credentials = new Properties();
    private static final Properties gbdx_credentials = new Properties();

    static {
        s3_credentials.put("S3_secret_key", "FxKBPWyZgIDfra/oMT0X3jeKb5F7S6c9mk51altD");
        s3_credentials.put("S3_access_key", "ASIAJY5QWJEMHXV26JZQ");
        s3_credentials.put("S3_session_token", "AQoDYXdzEB4a0AMgQ79BhEGFhZu2C+4Kh7Dfb49evauABgW2ebKLHuqCClgYA3TvN0fTtwussepj6SLc17M3h7jNuYxQrGR0VRICjm9QYqN8zX90Z0e4Ehg6Uz6hugJmYy4VrdaMG7fTTAjDjTmeZI331hxDIN4PTqt5nv7SUKvVRtXaZbzsz1PJga18n8U+xH3BPT+I3e1h3rihUYuFxGnmiuCHncSKdgwsqYuIq7ZCprAPZXu8Er4Bes36OtUVcStTaFLAV3hNEShRmsZi6X0xSSwkR9TJ0e5Wg5kFva8nISLPnsFQ8xyebFY8pIvQFXtWZwhtAoSRRk1H3LeZThDWG1kydyxWC2yg8yKyVTUX96P9uHwWwwTscVgAnKVFAgomjFGNWMJUJ+CuNYm/ujOqxTR1yY1c/P8fhb9UgV9/7FO8NOUoYgsi1kEXrX2l7ZiygewhfrjycT0L7QQNiAB/Z38ZER6995TCSACo5JknSjJoEUzQTjf7OccMbt6Fup11NhA0hFKe0TOd1kNFxLjvP6js0V8jWq5mgplxAxGPZ/Th/G+2KUC0Mk7tuJUze46/cIbyN1iwDZcKXibgLqgyyL8OizyRPCsW+cpB8mG2MxGC2B1vDLyBACCV44muBQ==");

        s3_credentials.put("bucket", "gbd-customer-data");
        s3_credentials.put("prefix", "b265b97f-30f2-48bd-9bc5-84c7c7eb0e06");

        gbdx_credentials.put("authorization", "Bearer 8vcfEG5eGIX9Xpv8ex7FMLlXZ8uIym");
    }

    public static Properties getS3Credentials() {
        return s3_credentials;
    }

    public static Properties getGBDxCredentials() {
        return gbdx_credentials;
    }

    public static String getAuthorizationHeader() {
        return gbdx_credentials.getProperty("authorization");
    }

    public static boolean isAuthHeaderValid(String header) {
        String[] auth_parts = header.split(" ");
        String token = auth_parts[1];
        Properties user_props = validateUserToken(token);
        return (boolean)user_props.get("is_active");
    }

    public static Properties validateUserToken(String token) {
        Properties token_info = new Properties();

        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        Request request = new Request.Builder()
                .url("https://geobigdata.io/auth/v1/validate_token")
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("authorization", "Bearer " + token)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            token_info = parseResponse(body);

        } catch (IOException io) {
            System.out.println("ERROR!");
            System.out.println(io.getMessage());
        }

        return token_info;
    }

    private static Properties parseResponse(String json) {
        Properties creds = new Properties();

        JSONObject obj = new JSONObject(json);

        String property = obj.getString("username");
        creds.put("username", property);

        boolean is_deleted = obj.getBoolean("is_deleted");
        creds.put("is_deleted", is_deleted);

        property = obj.getString("name");
        creds.put("name", property);

        boolean is_active = obj.getBoolean("is_active");
        creds.put("is_active", is_active);

        int id = obj.getInt("id");
        creds.put("id", id);

        property = obj.getString("last_login");
        creds.put("last_login", property);

        property = obj.getString("account_id");
        creds.put("account_id", property);

        boolean is_super_user = obj.getBoolean("is_super_user");
        creds.put("is_super_user", is_super_user);

        property = obj.getString("role");
        creds.put("role", property);

        property = obj.getString("email");
        creds.put("email", property);

        return creds;

    }
}
