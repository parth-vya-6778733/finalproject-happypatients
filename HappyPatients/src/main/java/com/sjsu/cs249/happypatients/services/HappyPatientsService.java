package com.sjsu.cs249.happypatients.services;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.datastax.driver.core.Session;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.sjsu.cs249.happypatients.Cassandra.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
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

    @POST
    @Path("/addDiagnosis")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addDiagnosis(Diagnosis diagnosis) {
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);
        pdi.insertDiagnosis(diagnosis);

        connector.close();

        String output = "Diagnosis added : " + diagnosis.getDiagnosis() + ". Here is your ID: " + diagnosis.getId();

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
        m.sendMessage("Updating Patient Personal Info");
        m.EmailConsumer();
        m.AnalyticsConsumer();

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);

        if(!(patient.getFirstName().equals("") || patient.getFirstName().equals(null)))
        {
            logger.debug("Patient old first name: " + ppi.selectById(patient.getId()).getFirstName());
            ppi.updateFirstName(patient.getId(),patient.getFirstName());
            logger.debug("Updated first name to: " + patient.getFirstName());
        }
        if(!(patient.getLastName().equals("") || patient.getLastName().equals(null)))
        {
            logger.debug("Patient old last name: " + ppi.selectById(patient.getId()).getLastName());
            ppi.updateLastName(patient.getId(),patient.getLastName());
            logger.debug("Updated last name to: " + patient.getLastName());
        }
        if(!(patient.getBirthDate().equals("") || patient.getBirthDate().equals(null)))
        {
            logger.debug("Patient old bday: " + ppi.selectById(patient.getId()).getBirthDate());
            ppi.updateBirthdate(patient.getId(),patient.getBirthDate());
            logger.debug("Updated bday to: " + patient.getBirthDate());
        }
        if(!(patient.getAddress().equals("") || patient.getAddress().equals(null)))
        {
            logger.debug("Patient old address: " + ppi.selectById(patient.getId()).getAddress());
            ppi.updateAddress(patient.getId(),patient.getAddress());
            logger.debug("Updated address to: " + patient.getAddress());
        }
        if(!(patient.getPhoneNumber().equals("") || patient.getPhoneNumber().equals(null)))
        {
            logger.debug("Patient old number: " + ppi.selectById(patient.getId()).getPhoneNumber());
            ppi.updatePhoneNumber(patient.getId(),patient.getPhoneNumber());
            logger.debug("Updated number to: " + patient.getPhoneNumber());
        }

        patientName = ppi.selectById(patient.getId()).getFirstName() + " " + ppi.selectById(patient.getId()).getLastName();

        connector.close();



        String output = "Patient info updated: " + patientName;

        return Response.status(200).entity(output).build();

    }

    @PUT
    @Path("/updateDiagnosisInfo")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateDiagnosisInfo(Diagnosis diagnosis) {
        logger.debug("Updating Diagnosis info");
        String patientName = "";

        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        MessageBroker m = new MessageBroker();

        m.sendMessage("Updating Patient Diagnosis Info");
        m.EmailConsumer();
        m.AnalyticsConsumer();

        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);

        if(!(diagnosis.getHeight().equals("") || diagnosis.getHeight().equals(null)))
        {
            logger.debug("Patient old height: " + pdi.selectById(diagnosis.getId()).getHeight());
            pdi.updateHeight(diagnosis.getId(),diagnosis.getHeight());
            logger.debug("Updated height to: " + diagnosis.getHeight());
        }
        if(!(diagnosis.getWeight().equals("") || diagnosis.getWeight().equals(null)))
        {
            logger.debug("Patient old weight: " + pdi.selectById(diagnosis.getId()).getWeight());
            pdi.updateWeight(diagnosis.getId(),diagnosis.getWeight());
            logger.debug("Updated weight to: " + diagnosis.getWeight());
        }
        if(!(diagnosis.getDiagnosis().equals("") || diagnosis.getDiagnosis().equals(null)))
        {
            logger.debug("Patient old diagnosis: " + pdi.selectById(diagnosis.getId()).getDiagnosis());
            pdi.updateDiagnosis(diagnosis.getId(),diagnosis.getDiagnosis());
            logger.debug("Updated diagnosis to: " + diagnosis.getDiagnosis());
            if(diagnosis.getDiagnosis().equals("done"))
            {
                m.sendMessage("Diagnosis Done");
                m.EmailConsumer();
                m.AnalyticsConsumer();
            }
        }
        if(!(diagnosis.getTreatment().equals("") || diagnosis.getTreatment().equals(null)))
        {
            logger.debug("Patient old treatment: " + pdi.selectById(diagnosis.getId()).getTreatment());
            pdi.updateTreatment(diagnosis.getId(),diagnosis.getTreatment());
            logger.debug("Updated treatment to: " + diagnosis.getTreatment());
            if(diagnosis.getTreatment().equals("completed")) {
                m.sendMessage("Treatment Completed");
                m.EmailConsumer();
                m.AnalyticsConsumer();
            }
        }

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patientName = ppi.selectById(diagnosis.getId()).getFirstName() + " " + ppi.selectById(diagnosis.getId()).getLastName();

        connector.close();



        String output = "Patient diagnosis updated: " + patientName;

        return Response.status(200).entity(output).build();

    }

    @PUT
    @Path("/updatePatientCache")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatientCache(String treatment) {
        logger.debug("Updating cache store: " + treatment);

        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);
        List<Diagnosis> d = pdi.selectAllTreatement(treatment);
        List<Patient> p = new ArrayList<Patient>();

        HazelcastInstance hci = Hazelcast.getHazelcastInstanceByName("my-instance");
        List<Patient> cacheMap = hci.getList("patients");
        for(Diagnosis di : d) {
            p.add(ppi.selectById(di.getId()));
        }
        cacheMap = p;

        for(Patient pi : cacheMap)
        {
            logger.debug("Updated Cached Patient List: " + pi.getFirstName());
        }

        return Response.status(200).entity(treatment).build();

    }

    @DELETE
    @Path("/deletePersonalInfo/{param}")
    public Response deletePersonalInfo(@PathParam("param") UUID patientId) {
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        patientName = ppi.selectById(patientId).getFirstName() + " " + ppi.selectById(patientId).getLastName();

        ppi.deleteAddress(patientId);

        MessageBroker m = new MessageBroker();
        m.sendMessage("Deleting patient personal info");
        m.EmailConsumer();
        m.AnalyticsConsumer();

        connector.close();

        String output = "Removed Patient Address for: " + patientName;

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