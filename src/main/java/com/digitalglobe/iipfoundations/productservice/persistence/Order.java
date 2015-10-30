/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.persistence;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Id;
import javax.persistence.Persistence;
import javax.persistence.Table;
import javax.persistence.Temporal;

/**
 *
 * @author jthiel
 */
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "OrderId")
    private String order_id;
    @Column(name = "ProcessId")
    private String process_id; // for the workflow process engine - currently activiti
    @Column(name = "Status")
    private String status;
    @Column(name = "WorkflowId")
    private String workflow_id; // for tracking the workflow building the process - for AComp this would be the GBDx workflow id
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "StartTime")
    private Date start_time;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "EndTime")
    private Date end_time;
    @Column(name = "DeliverLocation")
    private String delivery_location;
    
    @Column(name = "Product")
    private String product;
    @Column(name = "CatId")
    private String cat_id;
    
    //user info
    private String username;
    @Column(name = "AccountId")
    private String account_id;
    @Column(name = "AuthToken")
    private String auth_token;

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }        
    
    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }   
    
    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public Date getStart_time() {
        return start_time;
    }

    public void setStart_time(Date start_time) {
        this.start_time = start_time;
    }

    public Date getEnd_time() {
        return end_time;
    }

    public void setEnd_time(Date end_time) {
        this.end_time = end_time;
    }

    public String getDelivery_location() {
        return delivery_location;
    }

    public void setDelivery_location(String delivery_location) {
        this.delivery_location = delivery_location;
    }

    public String getWorkflow_id() {
        return workflow_id;
    }

    public void setWorkflow_id(String workflow_id) {
        this.workflow_id = workflow_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccount_id() {
        return account_id;
    }

    public void setAccount_id(String account_id) {
        this.account_id = account_id;
    }

    public String getProcess_id() {
        return process_id;
    }

    public void setProcess_id(String process_id) {
        this.process_id = process_id;
    }

    public Order() {
        this.order_id = UUID.randomUUID().toString();
    }

    public String getId() {
        return order_id;
    }

    public void setId(String id) {
        this.order_id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (order_id != null ? order_id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the order_id fields are not set
        if (!(object instanceof Order)) {
            return false;
        }
        Order other = (Order) object;
        if ((this.order_id == null && other.order_id != null) || (this.order_id != null && !this.order_id.equals(other.order_id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.digitalglobe.iipfoundations.productservice.persistence.Orders[ id=" + order_id + " ]";
    }

    public static void main(String[] args) {
        Order order = new Order();
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.digitalglobe.iipfoundations_IIPProductService_jar_1.0-SNAPSHOTPU");
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(order);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

}
