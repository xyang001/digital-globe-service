/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.rest.products;

import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCredentialManager;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import java.io.IOException;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 *
 * @author jthiel
 */
@Path("/acompjbpm")
public class ACompProductJBPM extends Product {

    private static final Logger logger = LoggerFactory.getLogger(ACompProductJBPM.class);

    @POST
    @Produces(MediaType.TEXT_XML)
    //@Authenticated
    public Response post(String xml, @HeaderParam("Authorization") String authorization) {
        logger.trace("Entering ACompProduct Post - Authorization {}", authorization);
        if (authorization == null || !GBDxCredentialManager.isAuthHeaderValid(authorization)) {
            logger.warn("Unauthorized Access: {}", authorization);
            return Response.status(403).type("text/plain").entity("Authorization Header Invalid").build();
        }

        try {
            String token = authTokenFromAuthHeader(authorization);
            Order order = generateOrder(token, xml);

            logger.trace("Leaving post");
            return Response.status(200).type("text/plain").entity(generateResponse(order)).build();
        } catch (ParserConfigurationException | IOException | SAXException ex) {
            ex.printStackTrace(System.out);
            logger.trace("Leaving post: With Error!");
            return Response.status(500).type("text/plain").entity("Internal Server Error").build();
        }
    }
}
