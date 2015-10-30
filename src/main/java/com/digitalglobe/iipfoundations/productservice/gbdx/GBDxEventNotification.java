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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author jthiel
 */

/*
 As far as I know right now GBDx does not send events for workflow or order completion. 
 The only way to know when your workflow is complete is to check the status endpoint periodically
 If GBDx at some point sends events this class will be changed to listen for events instead
 of polling for completion
 */
public class GBDxEventNotification {

    public synchronized void notifyWorkflowComplete(String workflow_id) {

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://geobigdata.io/workflows/v1/workflows/" + workflow_id)
                .get()
                .addHeader("content-type", "application/json")
                .addHeader("authorization", GBDxCredentialManager.getAuthorizationHeader())
                .build();

        try {
            // convert to thread, but for now use a loop and sleep for testing
            boolean isComplete = false;
            while (!isComplete) {
                Response response = client.newCall(request).execute();
                String body = response.body().string();
                System.out.println(body);

                JSONObject obj = new JSONObject(body);
                String state = obj.getJSONObject("state").getString("state");
                System.out.println(state);
                if(state.equalsIgnoreCase("complete")){
                    isComplete = true;
                }else{
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {                        
                    }
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }

    }

    public static void main(String[] args) {
        GBDxEventNotification gen = new GBDxEventNotification();
        gen.notifyWorkflowComplete("4129496493626069858");
    }
}
