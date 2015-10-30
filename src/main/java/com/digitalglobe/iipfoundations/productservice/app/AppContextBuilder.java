/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.app;

/**
 *
 * @author jthiel
 */

import org.eclipse.jetty.webapp.WebAppContext;

public class AppContextBuilder {
    
    private WebAppContext webAppContext;
    
    public WebAppContext buildWebAppContext(){
        webAppContext = new WebAppContext();
        webAppContext.setDescriptor(webAppContext + "/WEB-INF/web.xml");
        webAppContext.setResourceBase("src/main/webapp");
        webAppContext.setContextPath("/iip/info");
        return webAppContext;
    }
    
}
