package com.sjsu.cs249.services;

import org.apache.log4j.Logger;

import java.io.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.server.ExportException;
import java.util.Properties;
import javax.servlet.*;

public class PolicyServerInit implements ServletContextListener{

    private static final Logger logger = Logger.getLogger(PolicyServerInit.class);


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent){
        logger.debug("PolicyInit");
        servletContextEvent.getServletContext();
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("Policy.properties");

        Properties properties = new Properties();

        System.out.println("InputStream is: " + inputStream);

        //load the inputStream using the Properties
        try {
            properties.load(inputStream);
            //get the value of the property
            String propValue = properties.getProperty("policy");

            System.out.println("Property value is: " + propValue);

            URL url = new URL("http://localhost:9090/rest/patient_system/updatePatientCache");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput( true );
            String urlParameters  = propValue;
            byte[] postData = urlParameters.getBytes("UTF-8");
            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
                wr.write( postData );
            }

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String treatment = "";
            logger.debug("Output from Server .... \n");
            while ((treatment = br.readLine()) != null) {
                logger.debug(treatment);
            }

            conn.disconnect();
        }
        catch(Exception e)
        {
            logger.debug(e);
        }


    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
