package com.sjsu.cs249.services;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.io.DataOutputStream;

@Path("/policy_system")
public class PolicyServerService {
    private static final Logger logger = Logger.getLogger(PolicyServerService.class);
    public String policy = "ONGOING";

    @GET
    @Path("/retrievePolicy")
    public Response retrievePolicy() {
        return Response.status(200).entity(policy).build();
    }

    @GET
    @Path("/updateHospitalServerCache")
    public Response updateHospitalServerCache(){
        try {

            URL url = new URL("http://localhost:9090/rest/patient_system/updatePatientCache");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("PUT");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput( true );
            String urlParameters  = "ICU";
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

        } catch (MalformedURLException e) {

            e.printStackTrace();

        } catch (IOException e) {

            e.printStackTrace();

        }
        return Response.status(200).entity("ICU").build();
    }


}