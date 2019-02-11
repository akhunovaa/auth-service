package com.botmasterzzz.auth;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.ServletException;

public class Server {

    private static final int DEFAULT_PORT = 8060;
    private final Tomcat tomcat;

    public static void main(String[] args) throws Exception {
        new Server().run();
    }

    public Server() throws ServletException {
        this(getPort());
    }

    public Server(int port) throws ServletException {
        tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.setBaseDir(System.getProperty("java.io.tmpdir"));
        tomcat.getHost().setAppBase(System.getProperty("java.io.tmpdir"));
        tomcat.addWebapp("/auth", System.getProperty("java.io.tmpdir"));
    }


    public void run() throws LifecycleException {
        tomcat.start();
        tomcat.getServer().await();
    }


    public void start() throws LifecycleException {
        tomcat.start();

    }

    public void stop() throws LifecycleException {
        try {
            tomcat.stop();
        } finally {
            tomcat.destroy();
        }
    }

    public static int getPort() {
        return System.getenv("AUTH.TOMCAT.PORT") == null ? DEFAULT_PORT : Integer.parseInt(System.getenv("AUTH.TOMCAT.PORT"));
    }
}