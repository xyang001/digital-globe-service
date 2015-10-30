/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class ActivitiProcessEngineFactory {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiProcessEngineFactory.class);
    private static final ProcessEngine processEngine = ProcessEngineConfiguration.createProcessEngineConfigurationFromResourceDefault().buildProcessEngine();

    public static ProcessEngine getProcessEngine() {

        if (!isInitialized()) {
            init();
        }
        return processEngine;
    }

    public static void init() {
        RepositoryService repositoryService = processEngine.getRepositoryService();
        repositoryService.createDeployment().addClasspathResource("QueryGBDx.bpmn").deploy();
        repositoryService.createDeployment().addClasspathResource("UNUM.bpmn").deploy();
        repositoryService.createDeployment().addClasspathResource("ACompProduct.bpmn").deploy();
        try {
            repositoryService.createDeployment().addClasspathResource("Order1B.bpmn").deploy();
        } catch (Throwable t) {
            logger.warn(t.getMessage());
        }
        isInitialized = true;
    }

    private static boolean isInitialized() {

        return isInitialized;
    }

    private static boolean isInitialized = false;
}
