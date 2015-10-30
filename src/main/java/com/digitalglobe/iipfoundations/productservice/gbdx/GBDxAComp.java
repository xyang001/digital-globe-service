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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Properties;
import org.json.JSONObject;

/**
 *
 * @author jthiel
 */
public class GBDxAComp {

    public static void main(String[] args) {
        // request the workflow
        generateACompRequestBody("http://receiving-dgcs-tdgplatform-com.s3.amazonaws.com/054349800010_01_003", "http://gbd-customer-data.s3.amazonaws.com/b265b97f-30f2-48bd-9bc5-84c7c7eb0e06/054349800010_01_003");

    }

    public static String requestACompWorkflow(String input_directory, String output_directory, String authorization) {
        String workflow = generateACompRequestBody(input_directory, output_directory);
        System.out.println(workflow);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody request_body = RequestBody.create(mediaType, workflow);

        //Properties gbdx = GBDxCredentialManager.getGBDxCredentials();

        Request search_request = new Request.Builder()
                .url("https://geobigdata.io/workflows/v1/workflows")
                .post(request_body)
                .addHeader("content-type", "application/json")
                .addHeader("authorization", authorization)
                .build();

        OkHttpClient client = new OkHttpClient();
        String id = "";
        try {
            Response response = client.newCall(search_request).execute();
            String body = response.body().string();

            JSONObject obj = new JSONObject(body);
            id = obj.getString("id");
        } catch (IOException e) {
            e.printStackTrace(System.out);
            System.exit(0);
        }
        return id;
    }

    private static String generateACompRequestBody(String input_directory, String output_directory) {
        JSONObject json = new JSONObject();
        json.put("name", "acomp_stagetos3");

        /*
         Generate AComp task
         */
        LinkedHashMap acomp = new LinkedHashMap();
        acomp.put("name", "AComp");
        acomp.put("taskType", "AComp");

        LinkedList acomp_outputs = new LinkedList();
        LinkedHashMap acomp_output = new LinkedHashMap();
        acomp_output.put("name", "data");

        acomp_outputs.add(acomp_output);
        acomp.put("outputs", acomp_outputs);

        LinkedList acomp_inputs = new LinkedList();
        LinkedHashMap acomp_input = new LinkedHashMap();
        acomp_input.put("name", "data");
        acomp_input.put("value", input_directory);

        acomp_inputs.add(acomp_input);
        acomp.put("inputs", acomp_inputs);

        /*
         Generate Stage to S3 Task
         */
        LinkedHashMap stagetos3 = new LinkedHashMap();
        stagetos3.put("name", "StageToS3");
        stagetos3.put("taskType", "StageDataToS3");

        LinkedList stagetos3_inputs = new LinkedList();

        LinkedHashMap s3_input_one = new LinkedHashMap();
        s3_input_one.put("name", "data");
        s3_input_one.put("source", "AComp:data");

        LinkedHashMap s3_input_two = new LinkedHashMap();
        s3_input_two.put("name", "destination");
        s3_input_two.put("value", output_directory);

        stagetos3_inputs.add(s3_input_one);
        stagetos3_inputs.add(s3_input_two);
        stagetos3.put("inputs", stagetos3_inputs);

        /*
         add the tasks to the task list
         */
        LinkedList tasks = new LinkedList();
        tasks.add(acomp);
        tasks.add(stagetos3);

        json.put("tasks", tasks);
        //json.put("tasks", l1);

        //System.out.println("jsonString:");
        //System.out.println(json.toString());
        return json.toString();
    }

}
//    {
//        "name": "acomp_stagetos3",
//        "tasks": [
//            {
//                "name": "AComp",
//                "outputs": [
//                    {
//                        "name": "data"
//                    }
//                ],
//                "inputs": [
//                    {
//                        "name": "data",
//                        "value": "http://receiving-dgcs-tdgplatform-com.s3.amazonaws.com/054349800010_01_003/054349800010_01/054349800010_01_P002_PAN"
//                    }
//                ],
//                "taskType": "AComp"
//            },
//            {
//                "name": "StageToS3",
//                "inputs": [
//                    {
//                        "name": "data",
//                        "source": "AComp:data"
//                    },
//                    {
//                        "name": "destination",
//                        "value": "http://gbd-customer-data.s3.amazonaws.com/b265b97f-30f2-48bd-9bc5-84c7c7eb0e06/jthiel"
//                    }
//                ],
//                "taskType": "StageDataToS3"
//            }
//        ]
//    }
