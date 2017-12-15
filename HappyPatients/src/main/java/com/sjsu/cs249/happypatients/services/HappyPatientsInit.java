package com.sjsu.cs249.happypatients.services;

import com.sjsu.cs249.happypatients.Cassandra.*;
import com.sjsu.cs249.happypatients.HazelCast.HazelCastInitializer;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.*;

public class HappyPatientsInit implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(HappyPatientsInit.class);


    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.debug("Run code when server starts up");

        CassandraInitializer ci = new CassandraInitializer();
        ci.init();
        HazelCastInitializer hi = new HazelCastInitializer();
        hi.init();

    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.debug("Run code when server shuts down");

        CassandraInitializer ci = new CassandraInitializer();
        ci.destroy();

        HazelCastInitializer hi = new HazelCastInitializer();
        hi.destroy();
    }
}