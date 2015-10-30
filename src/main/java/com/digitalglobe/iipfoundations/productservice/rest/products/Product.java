/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.rest.products;

import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxCredentialManager;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import com.digitalglobe.iipfoundations.productservice.persistence.PersistenceManager;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Date;
import java.util.Properties;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author jthiel
 */
public class Product {

    /**
     *
     * @param xml - The body of the request
     * @param order - the order to update.
     * @throws SAXException
     * @throws IOException
     * @throws javax.xml.parsers.ParserConfigurationException
     */
    protected void processXMLInput(String xml, Order order) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(xml.getBytes("utf-8")));

        Node productNode = doc.getElementsByTagName("product").item(0);
        String product = productNode.getTextContent();
        order.setProduct(product);
        Node catidNode = doc.getElementsByTagName("catid").item(0);
        String cat_id = catidNode.getTextContent();
        order.setCat_id(cat_id);
    }

    /**
     *
     * @param header - The authorization header it is in the form of Bearer
     * token and matches the style used by GBDx.
     * @return The token part of the authorization header.
     */
    protected String authTokenFromAuthHeader(String header) {
        String[] auth_parts = header.split(" ");
        String token = auth_parts[1];
        return token;
    }

    /**
     *
     * @param token - the users authorization token
     * @param request
     * @return - A new order object that has been saved to the database
     */
    protected Order generateOrder(String token, String request) throws SAXException, IOException, ParserConfigurationException {
        Order order = new Order();

        Properties user_info = GBDxCredentialManager.validateUserToken(token);
        order.setAccount_id(user_info.getProperty("account_id"));
        order.setUsername(user_info.getProperty("username"));
        order.setAuth_token(token);

        Date date = new Date();
        order.setStart_time(date);
        order.setStatus("Received");

        processXMLInput(request, order);
        
        om.saveObject(order);

        return order;
    }

    protected void updateOrder(Order order) {
        om.updateObject(order);
    }

    protected static String generateResponse(Order order) {
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

    private final PersistenceManager om = new PersistenceManager();
}
