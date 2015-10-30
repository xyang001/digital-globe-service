/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti.delegates;

import com.digitalglobe.iipfoundations.productservice.orderservice.OrderService;
import com.digitalglobe.iipfoundations.productservice.orderservice.OrderServiceException;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import com.digitalglobe.iipfoundations.productservice.persistence.PersistenceManager;
import com.digitalglobe.iipfoundations.productservice.persistence.ProductTracker;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class Order1BDelegate extends ProductDelegate implements JavaDelegate {

    private static final Logger logger = LoggerFactory.getLogger(Order1BDelegate.class);

    @Override
    public void execute(DelegateExecution de) throws Exception {
        logger.trace("Entering execute");
        Order order = loadOrder(de);
        
        try {
            String wf_id = OrderService.order1b((String)de.getVariable("catid"), (String)de.getVariable("token"));
            logger.trace("Workflow started id: {}", wf_id);
            order.setStatus(wf_id);
            order.setStatus("In Progress");
        } catch (OrderServiceException ex) {
            order.setStatus(ex.getMessage());
        }
        
        PersistenceManager om = new PersistenceManager();
        om.updateObject(order);
        
        ProductTracker pt = new ProductTracker();
        pt.setId(order.getId());
        pt.setProduct("oneb");
        pt.setWorkflow_platform("OrderService");
        pt.setWorkflow_id(order.getWorkflow_id());
        pt.setStatus("In Progress");
        om.saveObject(pt);

        logger.trace("Leaving execute");
    }

}
