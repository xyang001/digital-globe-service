/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.gbdx;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class GBDxCatalogQuery {

    private static final Logger logger = LoggerFactory.getLogger(GBDxCatalogQuery.class);
    private static OkHttpClient client = new OkHttpClient();

    {
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
    }

    public static boolean isCatIdAvailable(String catid, String authorization) {Logger logger = LoggerFactory.getLogger(GBDxCatalogQuery.class);
        logger.trace("Entering isCatIdAvailable(catid = {})",catid);
        
        Request request = new Request.Builder()
                .url("https://geobigdata.io/catalog/v1/record/" + catid)
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("authorization", authorization)
                .build();
        boolean available = false;
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            JSONObject obj = new JSONObject(body);
            String available_string = obj.getJSONObject("properties").getString("available");

            
            available = available_string.equalsIgnoreCase("true");
        } catch (IOException io) {
            System.out.println("ERROR!");
            System.out.println(io.getMessage());
        }
        logger.trace("Leaving isCatIdAvailable(available = {})",available);
        return available;
    }

    public static ArrayList<String> getCatIdLocation(String catid, String authorization) {
        logger.trace("Entering getCatIdLocation({})", catid);
        ArrayList<String> locations = new ArrayList<>();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody request_body = RequestBody.create(mediaType, "{\"rootRecordId\": \"" + catid + "\",\"maxdepth\": 2,\"direction\": \"both\",\"labels\": []}");
        Request request = new Request.Builder()
                .url("https://geobigdata.io/catalog/v1/traverse")
                .post(request_body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", authorization)
                .build();
        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();
            //System.out.println(body);
            JSONObject obj = new JSONObject(body);

            JSONArray arr = obj.getJSONArray("results");

            for (int i = 0; i < arr.length(); i++) {
                JSONObject result = arr.getJSONObject(i);
                JSONObject properties = result.getJSONObject("properties");
                if (properties.has("objectIdentifier")) {
                    String object_identifier = properties.getString("objectIdentifier");
                    if (object_identifier.endsWith(".TIF")) {
                        String S3bucket = properties.getString("bucketName");
                        locations.add("http://" + S3bucket + ".s3.amazonaws.com/" + object_identifier);
                    }
                }
            }
           
        } catch (IOException io) {
            System.out.println("ERROR!");
            System.out.println(io.getMessage());
        }

        logger.trace("Leaving getCatIdLocation({})", catid);
        return locations;
    }

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        Request request = new Request.Builder()
                .url("https://geobigdata.io/catalog/v1/record/105041001281F200")
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("authorization", GBDxCredentialManager.getAuthorizationHeader())
                .build();

        try {
            Response response = client.newCall(request).execute();
            String body = response.body().string();

            System.out.println(body);

//            PrintWriter out = new PrintWriter("catalog.json");
//            out.println(body);
//            out.flush();
//            out.close();
            JSONObject obj = new JSONObject(body);
            String available = obj.getJSONObject("properties").getString("available");
            System.out.println(available);
//            JSONArray arr = obj.getJSONArray("posts");
//            for (int i = 0; i < arr.length(); i++) {
//                String post_id = arr.getJSONObject(i).getString("post_id");
//                ......
//            }

            MediaType mediaType = MediaType.parse("application/json");
            RequestBody request_body = RequestBody.create(mediaType, "{  \n    \"startDate\":null,\n    \"endDate\": null,\n    \"searchAreaWkt\":null,\n    \"tagResults\": false,\n    \"filters\": [\"identifier = '105041001281F200'\"],\n    \"types\":[\"Acquisition\"]\n}");
            Request search_request = new Request.Builder()
                    .url("https://alpha.geobigdata.io/catalog/v1/search")
                    .post(request_body)
                    .addHeader("content-type", "application/json")
                    .addHeader("authorization", GBDxCredentialManager.getAuthorizationHeader())
                    .build();

            response = client.newCall(search_request).execute();

            body = response.body().string();

            System.out.println(body);

//            PrintWriter out = new PrintWriter("search_result.json");
//            out.println(body);
//            out.flush();
//            out.close();
            mediaType = MediaType.parse("application/json");
            request_body = RequestBody.create(mediaType, "{\n            \"rootRecordId\": \"105041001281F200\",\n            \"maxdepth\": 2,\n            \"direction\": \"both\",\n            \"labels\": []\n        }");
            request = new Request.Builder()
                    .url("https://alpha.geobigdata.io/catalog/v1/traverse")
                    .post(request_body)
                    .addHeader("content-type", "application/json")
                    .addHeader("authorization", GBDxCredentialManager.getAuthorizationHeader())
                    .build();

            response = client.newCall(request).execute();

            body = response.body().string();
            System.out.println(body);

            PrintWriter out = new PrintWriter("traverse_result.json");
            out.println(body);
            out.flush();
            out.close();

        } catch (IOException io) {
            System.out.println("ERROR!");
            System.out.println(io.getMessage());
        }
    }
}
