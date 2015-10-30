/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti.delegates;

import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import com.digitalglobe.iipfoundations.productservice.persistence.PersistenceManager;
import org.activiti.engine.delegate.DelegateExecution;

/**
 *
 * @author jthiel
 */
public class ProductDelegate {
    
    Order loadOrder(DelegateExecution de){
        PersistenceManager om = new PersistenceManager();
        Order order = (Order)om.getObject((String) de.getVariable("order_id"),Order.class);
        return order;
    }
}
