package com.sjsu.cs249.services;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

@Path("/policy_system")
public class PolicyServerService {
    private static final Logger logger = Logger.getLogger(PolicyServerService.class);
    public String policy = "ONGOING";

    @GET
    @Path("/retrievePolicy")
    public Response retrievePolicy() {
        try {
            InputStream inputStream = this.getClass().getClassLoader()
                    .getResourceAsStream("Policy.properties");

            Properties properties = new Properties();

            properties.load(inputStream);
            //get the value of the property
            String propValue = properties.getProperty("policy");

            System.out.println("Property value is: " + propValue);
            this.policy=propValue;
        }
        catch(Exception e)
        {
            logger.debug(e);
        }
        return Response.status(200).entity(policy).build();
    }


}