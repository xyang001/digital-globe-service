/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.app;

/**
 *
 * @author jthiel
 */

import com.digitalglobe.iipfoundations.productservice.statushandler.StatusHandler;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class ApplicationControlActionListener implements ActionListener {

	private final JettyServer jettyServer;
        private final StatusHandler statusHandler;
        
	public ApplicationControlActionListener(JettyServer jettyServer, StatusHandler statusHandler) {
		this.jettyServer = jettyServer;
                this.statusHandler = statusHandler;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		 JButton btnStartStop =  (JButton) actionEvent.getSource();
		 if(jettyServer.isStarted()){
			 btnStartStop.setText("Stopping...");
			 btnStartStop.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			 try {
				jettyServer.stop();
                                statusHandler.stop();
			} catch (Exception exception) {
				exception.printStackTrace(System.out);
			}
			 btnStartStop.setText("Start");
			 btnStartStop.setCursor
				(new Cursor(Cursor.DEFAULT_CURSOR));
		 }
		 else if(jettyServer.isStopped()){
			 btnStartStop.setText("Starting...");
			 btnStartStop.setCursor(new Cursor(Cursor.WAIT_CURSOR));
			 try {
				jettyServer.start();
                                statusHandler.start();
			} catch (Exception exception) {
				exception.printStackTrace(System.out);
			}
			 btnStartStop.setText("Stop");
			 btnStartStop.setCursor
				(new Cursor(Cursor.DEFAULT_CURSOR));
		 }
	}
}
