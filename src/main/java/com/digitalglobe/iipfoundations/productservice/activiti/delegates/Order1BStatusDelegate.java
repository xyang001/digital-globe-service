/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti.delegates;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class Order1BStatusDelegate implements JavaDelegate{

    private static final Logger logger = LoggerFactory.getLogger(Order1BStatusDelegate.class);
    
    @Override
    public void execute(DelegateExecution de) throws Exception {
        logger.info("Entering execute");
    }
    
}
