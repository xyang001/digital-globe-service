/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digitalglobe.iipfoundations.productservice.activiti.delegates;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;

/**
 *
 * @author jthiel
 */
public class UnumConverter implements JavaDelegate {

    @Override
    public void execute(DelegateExecution de) throws Exception {
        System.out.println("Starting UNUM");
        
        System.out.println("UnumConverter.execute: " + de.getVariable("datadir"));
        ProcessBuilder builder = new ProcessBuilder("python", "universal_converter.py",  de.getVariable("datadir").toString());
        builder.directory(new File("C:\\Utilities\\UNUM\\").getAbsoluteFile()); // this is where you set the root folder for the executable to run with
        builder.redirectErrorStream(true);
        
        executeService(builder);
        System.out.println("UNUM Complete");
    }

    protected void executeService(ProcessBuilder builder) throws Exception {
        try {

            //Process p = Runtime.getRuntime().exec(cmd);
            Process p = builder.start();

            InputStream s = p.getInputStream();
            InputStream errorStream = p.getErrorStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(s));
            BufferedReader errorbr = new BufferedReader(new InputStreamReader(errorStream));
            ReadStream readStd = new ReadStream();
            ReadStream readErr = new ReadStream();
            readStd.br = br;
            readErr.br = errorbr;

            new Thread(readStd).start();
            new Thread(readErr).start();

            exitVal = p.waitFor();
            if (exitVal != 0) {
                throw new Exception("Workflow failed to continue.  Please refer to log.");
            }

            //System.out.println("command: " + cmd);
            //System.out.println("exited with value: " + cmd);

            lastLineOutput = readErr.lastLine.toString();

        } catch (IOException | InterruptedException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    class ReadStream implements Runnable {

        @Override
        public void run() {
            String line = null;
            try {
                while ((line = br.readLine()) != null) {
                    lastLine.append(" ").append(line);
                    if (!line.equalsIgnoreCase("")) {
                        System.out.println(line);
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace(System.out);
            }
        }
        protected StringBuilder lastLine = new StringBuilder();
        public BufferedReader br;
    }

    protected int exitVal;
    protected String lastLineOutput = null;

}
