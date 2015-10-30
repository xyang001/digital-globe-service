/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.persistence;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author jthiel
 */
public class PersistenceManager {

    EntityManagerFactory entityManagerFactory;
    EntityManager entityManager;

    public PersistenceManager() {
        entityManagerFactory = Persistence.createEntityManagerFactory("com.digitalglobe.iipfoundations_IIPProductService_jar_1.0-SNAPSHOTPU");
    }

    public void saveObject(Object object) {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.persist(object);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public synchronized void updateObject(Object object) {
        entityManager = entityManagerFactory.createEntityManager();
        entityManager.getTransaction().begin();
        entityManager.merge(object);
        entityManager.getTransaction().commit();
        entityManager.close();
    }

    public Object getObject(String id, Class<?> cls) {
        entityManager = entityManagerFactory.createEntityManager();
        Object object = entityManager.find(cls, id);
        entityManager.close();
        return object;
    }

    public void close() {
        entityManager.close();
    }

    @Override
    public void finalize() {
        close();
    }

}
