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
        String treatment = "";

        CassandraInitializer ci = new CassandraInitializer();
        ci.init();
        HazelCastInitializer hi = new HazelCastInitializer();
        hi.init();


//        try {
//
//            URL url = new URL("http://localhost:8080/rest/policy_system/retrievePolicy");
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//
//            if (conn.getResponseCode() != 200) {
//                throw new RuntimeException("Failed : HTTP error code : "
//                        + conn.getResponseCode());
//            }
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//
//
//            logger.debug("Output from Server .... \n");
//            while ((treatment = br.readLine()) != null) {
//                logger.debug(treatment);
//
//            }
//
//            conn.disconnect();
//
//        } catch (MalformedURLException e) {
//
//            e.printStackTrace();
//
//        } catch (IOException e) {
//
//            e.printStackTrace();
//
//        }




    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.debug("Run code when server shuts down");

        CassandraInitializer ci = new CassandraInitializer();
        ci.destroy();

//        HazelCastInitializer hi = new HazelCastInitializer();
//        hi.destroy();
    }
}