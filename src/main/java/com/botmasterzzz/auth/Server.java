package com.botmasterzzz.auth;

import com.botmasterzzz.auth.config.AppProperties;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import javax.servlet.ServletException;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class Server {

    private static final int DEFAULT_PORT = 8060;
    private final Tomcat tomcat;

    public static void main(String[] args) {
        SpringApplication.run(Server.class, args);
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