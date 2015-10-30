/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.orderservice;

/**
 *
 * @author jthiel
 */
public class OrderServiceException extends Exception {

    public OrderServiceException() {
        super();
    }

    public OrderServiceException(String message) {
        super(message);
    }

    public OrderServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrderServiceException(Throwable cause) {
        super(cause);
    }

}
