package com.sjsu.cs249.services;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.*;

import javax.servlet.*;

public class PolicyServerInit implements ServletContextListener{

    private static final Logger logger = Logger.getLogger(PolicyServerInit.class);


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent){
        logger.debug("PolicyInit");

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
