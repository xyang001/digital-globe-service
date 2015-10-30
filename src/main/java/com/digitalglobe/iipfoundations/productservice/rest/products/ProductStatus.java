/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.rest.products;

import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCredentialManager;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import com.digitalglobe.iipfoundations.productservice.persistence.PersistenceManager;
import java.io.StringWriter;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Provides status updates for all products.
 *
 * @author jthiel
 */
@Path("/status")
public class ProductStatus {

    private static final Logger logger = LoggerFactory.getLogger(ProductStatus.class);

    @GET
    @Produces(MediaType.TEXT_XML)
    //@Authenticated
    @Path("{id}")
    public Response get(@HeaderParam("Authorization") String authorization, @PathParam("id") String order_id) {
        logger.trace("Entering get - order_id {}", order_id);
        if (authorization == null || !GBDxCredentialManager.isAuthHeaderValid(authorization)) {
            return Response.status(403).type("text/plain").entity("Authorization Header Invalid").build();
        }

        // load order from the database
        PersistenceManager om = new PersistenceManager();
        Order order = (Order)om.getObject(order_id, Order.class);

        String response = generateResponse(order);

        logger.trace("Leaving get - status {}", order.getStatus());
        return Response.status(200).type("text/plain").entity(response).build();
    }

    private static String generateResponse(Order order) {
        String response = "";

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("iipproductservice");
            doc.appendChild(rootElement);

            // order elements
            Element order_element = doc.createElement("order");
            rootElement.appendChild(order_element);

            Element orderid = doc.createElement("id");
            orderid.appendChild(doc.createTextNode(order.getId()));
            order_element.appendChild(orderid);

            Element status = doc.createElement("status");
            status.appendChild(doc.createTextNode(order.getStatus()));
            order_element.appendChild(status);

            Element location = doc.createElement("location");
            if (null != order.getDelivery_location()) {
                location.appendChild(doc.createTextNode(order.getDelivery_location()));
            }else{
                location.appendChild(doc.createTextNode(""));
            }

            order_element.appendChild(location);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");

            return output;

        } catch (ParserConfigurationException | TransformerException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }
}
