/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.activiti.engine.HistoryService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.runtime.Execution;

/**
 *
 * @author jthiel
 */
public class ActivitiProcessInstanceManager {

    public String getProcessInstanceStatus(String processId) {
        
        ProcessEngine processEngine = ActivitiProcessEngineFactory.getProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        HistoryService historyService = processEngine.getHistoryService();
        Execution ex = runtimeService.createExecutionQuery().processInstanceId(processId).singleResult();
        
        
        
//        List<HistoricTaskInstance> taskList = historyService.createHistoricTaskInstanceQuery()
//                .processInstanceId(processId)
//                .list();
//        
//        for(HistoricTaskInstance hti : taskList){
//            System.out.println(hti.getEndTime());
//        }
        
        HistoricActivityInstance hai = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).singleResult();
        if(null == hai){
            return "Unknown Error.";
        }
        
        //Date start = hai.getStartTime();
        Date end = hai.getEndTime();
        
        if(null == end){
            return "In Progress";
        }else{
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            return "Completed: " + df.format(end);
        }
        
    }
    
    public static void main(String[] args) {
        ActivitiProcessEngineFactory.init();
        ActivitiProcessInstanceManager instanceManager = new ActivitiProcessInstanceManager();
        String status = instanceManager.getProcessInstanceStatus("322509");
        System.out.println(status);
    }
}
