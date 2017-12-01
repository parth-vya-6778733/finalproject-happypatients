package com.sjsu.cs249.services;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.sjsu.cs249.Cassandra.*;
import com.datastax.driver.core.Session;
import org.apache.log4j.Logger;

import javax.jms.*;
import javax.naming.InitialContext;

import java.util.UUID;

@Path("/patient_system")
public class HappyPatientsService {
    CassandraConnector connector = new CassandraConnector();
    private static final Logger logger = Logger.getLogger(HappyPatientsService.class);

    @GET
    @Path("/retrievePatient/{param}")
    public Response retrievePatient(@PathParam("param") UUID patientId) {
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);

        patientName = ppi.selectById(patientId).getFirstName() + " " + ppi.selectById(patientId).getLastName();

        connector.close();

        String output = "Hello Patient : " + patientName;

        return Response.status(200).entity(output).build();

    }

    @POST
    @Path("/addPatient")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addPatient(Patient patient) {
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patient.setId();
        ppi.insertPatient(patient);

        connector.close();



        String output = "Welcome Patient : " + patient.getFirstName() + ". Here is your ID: " + patient.getId();

        return Response.status(200).entity(output).build();

    }

    @PUT
    @Path("/updatePersonalInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePersonalInfo(Patient patient) {
        logger.debug("Updating Patient info");
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        MessageBroker m = new MessageBroker();

        m.sendMessage("Hello World");

        for(String s : m.receiveMessages())
        {
            logger.debug("Messages: " + s);
        }

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);

        if(!(patient.getFirstName().equals("") || patient.getFirstName().equals(null)))
        {
            logger.debug("Patient old name: " + ppi.selectById(patient.getId()).getFirstName());
            ppi.updateFirstName(patient.getId(),patient.getFirstName());
            logger.debug("Updated name to: " + patient.getFirstName());
        }
        if(patient.getLastName().equals("") || patient.getLastName().equals(null))
        {

        }
        if(patient.getBirthDate().equals("") || patient.getBirthDate().equals(null))
        {

        }
        if(patient.getAddress().equals("") || patient.getAddress().equals(null))
        {

        }
        if(patient.getPhoneNumber().equals("") || patient.getPhoneNumber().equals(null))
        {

        }

        patientName = ppi.selectById(patient.getId()).getFirstName() + " " + ppi.selectById(patient.getId()).getLastName();

        connector.close();



        String output = "Patient info updated: " + patientName;

        return Response.status(200).entity(output).build();

    }

    @DELETE
    @Path("/deletePatient/{param}")
    public Response deletePatient(@PathParam("param") UUID patientId) {
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patientName = ppi.selectById(patientId).getFirstName() + " " + ppi.selectById(patientId).getLastName();

        ppi.deletePatientById(patientId);

        connector.close();

        String output = "Removed Patient : " + patientName;

        return Response.status(200).entity(output).build();

    }

    @DELETE
    @Path("/deleteSystem/{param}")
    public Response deleteSystem(@PathParam("param") String keyspaceName) {
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace(keyspaceName);

        sr.deleteKeyspace(keyspaceName);

        connector.close();

        String output = "System Deleted : " + keyspaceName;

        return Response.status(200).entity(output).build();

    }


}