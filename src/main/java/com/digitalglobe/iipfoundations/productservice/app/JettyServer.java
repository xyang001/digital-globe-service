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
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.glassfish.jersey.servlet.ServletContainer;

public class JettyServer {

    private Server server;

    public JettyServer() {
        this(443);
    }

    public JettyServer(Integer port) {
        server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(80);
        server.addConnector(connector);

        /*
         configure ssl
         */
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(port);

        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        SslContextFactory sslContextFactory = new SslContextFactory("src\\main\\resources\\keystore.jks");
        sslContextFactory.setKeyStorePassword("password");

        ServerConnector httpsConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
        httpsConnector.setPort(port);
        httpsConnector.setIdleTimeout(50000);
        httpsConnector.setName("secured");
        //server.setConnectors(new Connector[]{httpsConnector});
        
        
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        //contexts.setHandlers(new Handler[]{new AppContextBuilder().buildWebAppContext()});
        // Add the unsecured context
        contexts.addHandler(new AppContextBuilder().buildWebAppContext());

        // add the secured context
        ServletContextHandler servlet_context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        servlet_context.setContextPath("/iip/products");
        servlet_context.setVirtualHosts(new String[]{"@secured"});
        ServletHolder jerseyServlet = servlet_context.addServlet(ServletContainer.class, "/*");
        jerseyServlet.setInitOrder(0);
        
        // Tells the Jersey Servlet which REST service/class to load.
        jerseyServlet.setInitParameter("jersey.config.server.provider.packages", "com.digitalglobe.iipfoundations.productservice.rest.products");

        contexts.addHandler(servlet_context);
        setHandler(contexts);
        
        server.addConnector(httpsConnector);

        /**
         * ****************************************************************************
         */
    }

    public final void setHandler(ContextHandlerCollection contexts) {
        server.setHandler(contexts);
    }

    public void start() throws Exception {
        server.start();

    }

    public void stop() throws Exception {
        server.stop();
        server.join();
    }

    public boolean isStarted() {
        return server.isStarted();
    }

    public boolean isStopped() {
        return server.isStopped();
    }

}
