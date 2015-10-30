/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.persistence;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;

/**
 *
 * @author jthiel
 */
@Entity
@Table(name = "producttracker")
public class ProductTracker implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "OrderId")
    private String order_id;

    @Column(name = "Product")
    private String product;

    @Column(name = "WorkflowPlatform")
    private String workflow_platform;

    @Column(name = "WorkflowId")
    private String workflow_id;
    
    @Column(name = "Status")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    

    public String getId() {
        return order_id;
    }

    public String getProduct() {
        return product;
    }

    public String getWorkflow_platform() {
        return workflow_platform;
    }

    public String getWorkflow_id() {
        return workflow_id;
    }

    public void setId(String id) {
        this.order_id = id;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public void setWorkflow_platform(String workflow_platform) {
        this.workflow_platform = workflow_platform;
    }

    public void setWorkflow_id(String workflow_id) {
        this.workflow_id = workflow_id;
    }
    
        public static void main(String[] args) {
        ProductTracker pt = new ProductTracker();
        pt.setId("sadfasdfas");
        pt.setProduct("acomp");
        pt.setWorkflow_platform("GBDx");
        pt.setWorkflow_id("123456");
        
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.digitalglobe.iipfoundations_IIPProductService_jar_1.0-SNAPSHOTPU");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(pt);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

}
