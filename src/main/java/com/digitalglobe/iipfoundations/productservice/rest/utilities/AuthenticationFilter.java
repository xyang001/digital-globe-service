/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.rest.utilities;


import com.digitalglobe.iipfoundations.productservice.rest.products.ACompProduct;
import java.lang.annotation.Annotation;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */


//@Provider
//public @interface AuthenticationFilter implements Authenticated {
//
//    private static final Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);
//
////    @Override
////    public void filter(ContainerRequestContext crc) throws WebApplicationException {
////        logger.trace("Entering filter");
////        System.out.println("filter");
////        logger.trace("Leaving filter");
////    }
//
//    @Override
//    public Class<? extends Annotation> annotationType() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//}
