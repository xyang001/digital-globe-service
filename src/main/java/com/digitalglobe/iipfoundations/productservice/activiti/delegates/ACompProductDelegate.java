/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti.delegates;

import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxAComp;
import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCatalogQuery;
import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxS3Credentials;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import com.digitalglobe.iipfoundations.productservice.persistence.PersistenceManager;
import com.digitalglobe.iipfoundations.productservice.persistence.ProductTracker;
import java.util.ArrayList;
import java.util.Properties;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class ACompProductDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(ACompProductDelegate.class);

    @Override
    public void execute(DelegateExecution de) throws Exception {
        logger.trace("Entering ACompProductDelegate");
        logger.trace("Authorization: {}", de.getVariable("authorization"));

        Properties s3creds = GBDxS3Credentials.getS3Credentials((String) de.getVariable("authorization"));
        ArrayList<String> locations = GBDxCatalogQuery.getCatIdLocation((String) de.getVariable("catid"), (String)de.getVariable("authorization"));
        // for now run on only one POO for speed, use the first one returned in the list
        String location = locations.get(0);
        location = location.substring(0, location.lastIndexOf("/"));

        String output_directory = "http://" + s3creds.getProperty("bucket") + ".s3.amazonaws.com/" + s3creds.getProperty("prefix") + "/IIPProductService/" + de.getVariable("order_id");
        logger.trace(output_directory);

        //String wf_id = "dummy_test";
        String wf_id = GBDxAComp.requestACompWorkflow(location, output_directory, (String)de.getVariable("authorization"));
        logger.trace("Workflow started id: {}", wf_id);

        PersistenceManager om = new PersistenceManager();
        Order order =(Order)om.getObject((String) de.getVariable("order_id"),Order.class);
        order.setWorkflow_id(wf_id);
        order.setStatus("In Progress");
        order.setDelivery_location(output_directory);
        om.updateObject(order);
        
        ProductTracker pt = new ProductTracker();
        pt.setId(order.getId());
        pt.setProduct("acomp");
        pt.setWorkflow_platform("GBDx");
        pt.setWorkflow_id(order.getWorkflow_id());
        pt.setStatus("In Progress");
        om.saveObject(pt);
        
        logger.trace("Leaving ACompProductDelegate");
    }

}
 