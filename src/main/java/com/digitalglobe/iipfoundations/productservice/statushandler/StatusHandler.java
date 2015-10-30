/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.statushandler;

import com.digitalglobe.iipfoundations.productservice.gbdx.GBDxWorkflow;
import com.digitalglobe.iipfoundations.productservice.persistence.Order;
import com.digitalglobe.iipfoundations.productservice.persistence.PersistenceManager;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author jthiel
 */
public class StatusHandler {

    private static final Logger logger = LoggerFactory.getLogger(StatusHandler.class);

    public StatusHandler() {
        status_checker = new StatusChecker();
    }

    public void start() {
        status_checker.reset();
        status_runner = new Thread(status_checker);
        status_runner.start();
        running = true;
    }

    public void stop() throws InterruptedException {
        status_runner.interrupt();
        status_checker.doStop();
        status_runner.join();
        running = false;
    }

    public boolean isStarted() {
        return running;
    }

    private boolean running = false;
    private final StatusChecker status_checker;
    private Thread status_runner;

    private class StatusChecker implements Runnable {

        @Override
        @SuppressWarnings("SleepWhileInLoop")
        public void run() {

            while (running) {
                logger.trace("Status Checker!");

                /*
                 * Load the orders that are in progress
                 */
                EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("com.digitalglobe.iipfoundations_IIPProductService_jar_1.0-SNAPSHOTPU");
                EntityManager entityManager = entityManagerFactory.createEntityManager();
                CriteriaBuilder cb = entityManager.getCriteriaBuilder();
                CriteriaQuery<Order> cq = cb.createQuery(Order.class);
                Root<Order> order = cq.from(Order.class);
                //cq.where(cb.equal(order.get(Order.status), "In Progress"));
                TypedQuery<Order> q = entityManager.createQuery(cq);
                List<Order> results = q.getResultList();

                /*
                 * process the in progress orders
                 * right now the only place to check is GBDx against the workflowid
                 * if the GBDx workflow is finished the order is finished
                 */
                for (Order o : results) {
                    String wf_id = o.getWorkflow_id();
                    if (!wf_id.equalsIgnoreCase("dummy_test")) {
                        String wf_status = null;
                        try {
                            wf_status = GBDxWorkflow.status(wf_id, "Bearer " + o.getAuth_token());
                        } catch (Throwable ex) {
                            logger.debug("Get workflow status interrupted: {}", ex.getMessage());
                            this.doStop();
                        }
                        logger.trace("Order_id: " + o.getId() + "\tProcess Status: " + o.getStatus() + "\tworkflow_id: " + o.getWorkflow_id() + "\tworkflow status: " + wf_status);
                        if (wf_status.equalsIgnoreCase("complete")) {
                            o.setStatus(wf_status);

                            Date date = new Date();
                            o.setEnd_time(date);

                            PersistenceManager om = new PersistenceManager();
                            om.updateObject(o);
                        }
                    }
                }

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {

                }
            }

        }

        public void doStop() {
            running = false;
        }

        public void reset() {
            running = true;
        }

        boolean running = true;

    }

}
