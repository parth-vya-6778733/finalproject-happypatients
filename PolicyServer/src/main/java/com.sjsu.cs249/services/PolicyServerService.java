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


}