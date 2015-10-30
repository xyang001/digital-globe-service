/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.rest.products;

import com.digitalglobe.iipfoundations.productservice.activiti.ActivitiProcessEngineFactory;
import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCredentialManager;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author jthiel
 */
@Path("/oneb")
public class OneBProduct extends Product {

    private static final Logger logger = LoggerFactory.getLogger(OneBProduct.class);

    @POST
    @Produces(MediaType.TEXT_XML)
    public Response post(String xml, @HeaderParam("Authorization") String authorization) {
        logger.trace("Entering OneBProduct Post - Authorization {}", authorization);
        if (authorization == null || !GBDxCredentialManager.isAuthHeaderValid(authorization)) {
            logger.warn("Unauthorized Access: {}", authorization);
            return Response.status(403).type("text/plain").entity("Authorization Header Invalid").build();
        }
        try {
            String token = authTokenFromAuthHeader(authorization);
            Order order = generateOrder(token, xml);

            ProcessEngine processEngine = ActivitiProcessEngineFactory.getProcessEngine();
            final RuntimeService runtimeService = processEngine.getRuntimeService();

            try {
                processXMLInput(xml, order);
            } catch (SAXException | IOException | ParserConfigurationException ex) {
                logger.warn(ex.getMessage());
                return Response.status(500).type("text/plain").entity("Internal Server Error").build();
            }

            final Map<String, Object> variables = new HashMap<>();
            variables.put("catid", order.getCat_id());
            variables.put("order_id", order.getId());
            variables.put("token", token);

            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("OneBProduct", variables);
            order.setProcess_id(processInstance.getId());
            order.setStart_time(new Date());
            order.setStatus("Received");
            updateOrder(order);

            String response = generateResponse(order);

            logger.trace("Leaving OneBProduct Post - Authorization {}", authorization);
            return Response.status(200).type("text/plain").entity(response).build();
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace(System.out);
            logger.trace("Leaving post: With Error!");
            return Response.status(500).type("text/plain").entity("Internal Server Error").build();
        }

    }

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
        sb.append("<product>oneb</product>");
        sb.append("<catid>catid</catid>");
        sb.append("</iipfoundations>");

        return Response.status(200).type("text/xml").entity(sb.toString()).build();

    }
}
