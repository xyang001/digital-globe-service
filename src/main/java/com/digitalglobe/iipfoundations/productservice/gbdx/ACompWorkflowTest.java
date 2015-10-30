/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.gbdx;

import java.util.ArrayList;
import java.util.Properties;
import java.util.UUID;

/**
 *
 * @author jthiel
 */
public class ACompWorkflowTest {

//    private static String catid = "105041001281F200";
//
//    public static void main(String[] args) {
//        Properties s3 = GBDxCredentialManager.getS3Credentials();
//        //Check GBDx catalog for availablity of catid materials - not just metadata
//        //boolean is_catid_available = GBDxCatalogQuery.isCatIdAvailable(catid);
//        //System.out.println(is_catid_available);
//
//        /*
//         Get the location of the catid materials - for GBDx will be an s3 bucket.
//         */
//        ArrayList<String> locations = GBDxCatalogQuery.getCatIdLocation(catid, null);
//        // for now run on only one POO for speed, use the first one returned in the list
//        String location = locations.get(0);
//        System.out.println(location);
//        location = location.substring(0,location.lastIndexOf("/"));
//        System.out.println(location);
//        /*
//         generate an order id
//         */
//        UUID order_id = UUID.randomUUID();
//        //System.out.println(order_id.toString());
//
//        /*
//         generate the output directory
//         */
//        String output_directory = "http://" + s3.getProperty("bucket") + ".s3.amazonaws.com/" + s3.getProperty("prefix") + "/IIPProductService/" + order_id.toString();
//        
//        //String wf_id = GBDxAComp.requestACompWorkflow(location, output_directory);
//        System.out.println("Workflow started id: " + wf_id);
//         // wait for workflow completion
//        GBDxEventNotification gbdxEN = new GBDxEventNotification();
//        gbdxEN.notifyWorkflowComplete(wf_id);
//        System.out.println("Workflow complete id: " + wf_id);
//        
//
//    }
}
