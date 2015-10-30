/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti.delegates;

import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCatalogQuery;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 *
 * @author jthiel
 */
public class QueryGBDxCatalogDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution de) throws Exception {
        String catid = "" + de.getVariable("catid");
        boolean materialsAvailable = GBDxCatalogQuery.isCatIdAvailable(catid, (String)de.getVariable("authorization"));
        de.setVariable("materialsAvailable", materialsAvailable);
    }

}
