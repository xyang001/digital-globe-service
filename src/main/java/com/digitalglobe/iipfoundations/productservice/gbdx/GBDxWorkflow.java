/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.gbdx;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import java.util.Properties;
import com.squareup.okhttp.Response;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class GBDxWorkflow {
    
    private static final Logger logger = LoggerFactory.getLogger(GBDxWorkflow.class);
    
    public static String status(String wf_id, String authorization) throws Exception {
        logger.trace("Entering Status - workflow id: {}", wf_id);
        String status = "";
        
        Properties gbdx = GBDxCredentialManager.getGBDxCredentials();
        
        Request workflow_status_request = new Request.Builder()
                .url("https://geobigdata.io/workflows/v1/workflows/" + wf_id)
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("authorization", authorization)
                .build();
        OkHttpClient client = new OkHttpClient();
        String body = null;
        try {
            Response response = client.newCall(workflow_status_request).execute();
            body = response.body().string();
            
            JSONObject json = new JSONObject(body);
            JSONObject state = json.getJSONObject("state");
            status = state.getString("state");
            
        } catch (JSONException e) {
            logger.warn("Unable to parse response for workflow id: {}, Error: {}, Body: {}", wf_id, e.getMessage(), body);
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        
        logger.trace("Leaving Status - status: {}", status);
        return status;
    }
}
