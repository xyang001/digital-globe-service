/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.rest.products;

import com.digitalglobe.iipfoundations.productservice.rest.utilities.Authenticated;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.activiti.engine.ProcessEngine;
import com.digitalglobe.iipfoundations.productservice.activiti.ActivitiProcessEngineFactory;
import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCredentialManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import static java.lang.Thread.sleep;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author jthiel
 */
@Path("/unum")
public class UnumProduct {

    /**
     * Method handling HTTP GET requests. The returned object will be sent to the client as "text/plain" media type.
     *
     * @param authorization
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    //@Authenticated
    public Response get(@HeaderParam("Authorization") String authorization) {

         if(authorization == null || !GBDxCredentialManager.isAuthHeaderValid(authorization)){
            return Response.status(403).type("text/plain").entity("Authorization Header Invalid").build();            
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<iipfoundations>");
        sb.append("<product>unum</product>");
        sb.append("<data>data directory</data>");
        sb.append("</iipfoundations>");

        return Response.status(200).type("text/plain").entity(sb.toString()).build();
    }

    @POST
    @Produces(MediaType.TEXT_XML)
    //@Authenticated
    public Response post(@HeaderParam("Authorization") String authorization, String xml) {
        
        if (authorization == null || !GBDxCredentialManager.isAuthHeaderValid(authorization)) {
            return Response.status(403).type("text/plain").entity("Authorization Header Invalid").build();            
        }
        
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("utf-8")));

            Node productNode = doc.getElementsByTagName("product").item(0);
            String product = productNode.getTextContent();
            System.out.println(product);

            Node dataNode = doc.getElementsByTagName("data").item(0);
            String data = dataNode.getTextContent();
            System.out.println(data);

            ProcessEngine processEngine = ActivitiProcessEngineFactory.getProcessEngine();
            final RuntimeService runtimeService = processEngine.getRuntimeService();

            final Map<String, Object> variables = new HashMap<String, Object>();
            variables.put("datadir", data);

            Thread thread = new Thread() {
                public void run() {
                    System.out.println("Thread Running");
                    try {
                        sleep(10000L);
                    } catch (InterruptedException ex) {
                    }
                    ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("unum_converter", variables);

                }
            };

            thread.start();

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace(System.out);
            return Response.status(500).type("text/plain").entity("Internal Server Error").build(); 
        }

        return Response.status(200).type("text/xml").entity(xml).build(); 
    }
}
