/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.orderservice;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import java.io.IOException;
import java.util.LinkedList;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private static final String username = "geobigdata";
    private static final String password = "ge0bigd@t@";

    /**
     * 
     * @param cat_id
     * @param auth_token
     * @return - the order_id
     * @throws IOException
     * @throws OrderServiceException 
     */
    public static String order1b(String cat_id, String auth_token) throws IOException, OrderServiceException {
        String request = generateOrder1bRequestBody(cat_id);
        System.out.println(request);
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody request_body = RequestBody.create(mediaType, request);

        //Properties gbdx = GBDxCredentialManager.getGBDxCredentials();

        Request search_request = new Request.Builder()
                .url("http://orders.iipfoundations.com/order/v1")
                .post(request_body)
                .addHeader("content-type", "application/json") 
                .addHeader("authorization", "Basic " + auth_token)
                .addHeader("username", username)
                .addHeader("password", password)
                .build();

        OkHttpClient client = new OkHttpClient();
        System.out.println(search_request.toString());
        Response response = client.newCall(search_request).execute();
        if (200 == response.code()) {
            String body = response.body().string();
            System.out.println(body);

            JSONObject obj = new JSONObject(body);
            JSONArray orders = obj.getJSONArray("orders");
            JSONObject order = orders.getJSONObject(0);
            int id = order.getInt("id");
            return Integer.toString(id);
        } else {
            System.out.println(response.body().string());
            logger.error(response.message());
            throw new OrderServiceException(response.message());
        }
        
        
    }

    private static String generateOrder1bRequestBody(String cat_id) {
        JSONObject json = new JSONObject();
        LinkedList cat_ids = new LinkedList();
        cat_ids.add(cat_id);
        json.put("catalog_ids", cat_ids);
        return json.toString();
    }

    public static void main(String[] args) {
        try {
            try {
                OrderService.order1b("105041001281F200", "Z2VvYmlnZGF0YTpnZTBiaWdkQHRA");
            } catch (OrderServiceException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            System.exit(0);
        }
    }
}
