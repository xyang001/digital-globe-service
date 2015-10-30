/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.rest.products;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.activiti.engine.ProcessEngine;
import com.digitalglobe.iipfoundations.productservice.activiti.ActivitiProcessEngineFactory;
import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCredentialManager;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import com.digitalglobe.iipfoundations.productservice.persistence.PersistenceManager;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author jthiel
 */
@Path("/acomp")
public class ACompProduct extends Product {

    private static final Logger logger = LoggerFactory.getLogger(ACompProduct.class);

    /**
     * Method handling HTTP GET requests. The returned object will be sent to
     * the client as "text/plain" media type.
     *
     * @param authorization
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_XML)
    //@Authenticated
    public Response get(@HeaderParam("Authorization") String authorization) {

        if (authorization == null || !GBDxCredentialManager.isAuthHeaderValid(authorization)) {
            return Response.status(403).type("text/plain").entity("Authorization Header Invalid").build();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<iipfoundations>");
        sb.append("<product>acomp</product>");
        sb.append("<catid>catid</catid>");
        sb.append("</iipfoundations>");

        return Response.status(200).type("text/plain").entity(sb.toString()).build();

    }

    @POST
    @Produces(MediaType.TEXT_XML)
    //@Authenticated
    public Response post(String xml, @HeaderParam("Authorization") String authorization) {
        logger.trace("Entering post - Authorization {}", authorization);
        if (authorization == null || !GBDxCredentialManager.isAuthHeaderValid(authorization)) {
            logger.warn("Unauthorized Access: {}", authorization);
            return Response.status(403).type("text/plain").entity("Authorization Header Invalid").build();
        }

        try {
            String response = "";
            String token = authTokenFromAuthHeader(authorization);
            Order order = generateOrder(token, xml);
            processXMLInput(xml, order);

            ProcessEngine processEngine = ActivitiProcessEngineFactory.getProcessEngine();
            final RuntimeService runtimeService = processEngine.getRuntimeService();

            final Map<String, Object> variables = new HashMap<>();
            variables.put("catid", order.getCat_id());
            variables.put("order_id", order.getId());
            variables.put("authorization", authorization);
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("ACompProduct", variables);

            order.setProcess_id(processInstance.getId());
            order.setStatus("In Progress");

            PersistenceManager pm = new PersistenceManager();
            pm.updateObject(order);

            response = generateResponse(order);
            logger.trace("Leaving post");
            return Response.status(200).type("text/plain").entity(response).build();

        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace(System.out);
            logger.trace("Leaving post: With Error!");
            return Response.status(500).type("text/plain").entity("Internal Server Error").build();
        }

    }
}
