package com.sjsu.cs249.happypatients.services;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;

import com.datastax.driver.core.Session;
import com.hazelcast.config.Config;
import com.hazelcast.config.CollectionConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.config.GroupConfig;

import com.hazelcast.core.IList;
import com.hazelcast.core.IMap;
import com.sjsu.cs249.happypatients.Cassandra.*;
import com.sjsu.cs249.happypatients.HazelCast.HazelCastInitializer;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import io.swagger.annotations.*;

@Path("/patient_system")
@Api(value = "PatientSystem")
@Produces({"application/json", "application/xml"})
public class HappyPatientsService {
    CassandraConnector connector = new CassandraConnector();
    private static final Logger logger = Logger.getLogger(HappyPatientsService.class);
    String currentPolicy = "";

    @GET
    @Path("/retrievePatient/{param}")
    public Response retrievePatient(@PathParam("param") UUID patientId) {
        String patientName = "";
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);

        patientName = ppi.selectById(patientId).getFirstName() + " " + ppi.selectById(patientId).getLastName();
        String patientAddy = ppi.selectById(patientId).getAddress();
        String diagnosis = pdi.selectById(patientId).getDiagnosis();
        String treatment = pdi.selectById(patientId).getTreatment();

        connector.close();

        String output = "Hello Patient : " + patientName + "\n"
                + "Here are your details: " + "\n"
                + "Address: " + patientAddy + "\n"
                + "Diagnosis: " + diagnosis + "\n"
                + "Treatment" + treatment + "\n";

        return Response.status(200).entity(output).build();

    }

    @GET
    @ApiOperation(value = "Gets Cached Patients",
            notes = "This gets updated when policy gets updated",
            response = Patient.class,
            responseContainer = "List")
    @Path("/retrieveCachedPatient")
    public Response retrievePatient() {
        String output = "";

        IMap<String,String> cacheMap = Hazelcast.getHazelcastInstanceByName("hospitalsys").getMap("patients");

        for(String pb : cacheMap.keySet())
        {
            output = "\n" +output + "Patient: " + pb + "\n"
            + cacheMap.get(pb) + "\n";
            logger.debug("Old Cached Patient List: " + pb);
        }

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
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        Patient p = ppi.selectById(diagnosis.getId());
        connector.close();

        getPolicy();
        logger.debug("Treatment: " + diagnosis.getTreatment());
        logger.debug("Policy: " + this.currentPolicy);


        if(diagnosis.getTreatment().equals(this.currentPolicy))
        {
            logger.debug("adding to cache with new patient diagnosis.");
            addToCachedMap(p,diagnosis);
        }

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
        m.sendMessage("Updating Patient Personal Info");
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
        m.sendMessage("Updating Patient Diagnosis Info");
        m.AnalyticsConsumer();

        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);

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
                m.sendMessage("Diagnosis Done");
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
                m.sendMessage("Treatment Completed");
                m.AnalyticsConsumer();
            }
            getPolicy();
            if(diagnosis.getTreatment().equals(this.currentPolicy)){
                addToCachedMap(ppi.selectById(diagnosis.getId()),diagnosis);
            }
            else
            {
                checkCacheAndRemove(ppi.selectById(diagnosis.getId()));
            }

        }


        patientName = ppi.selectById(diagnosis.getId()).getFirstName() + " " + ppi.selectById(diagnosis.getId()).getLastName();

        connector.close();



        String output = "Patient diagnosis updated: " + patientName;

        return Response.status(200).entity(output).build();

    }

    @PUT
    @Path("/updatePatientCache")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updatePatientCache(String treatment) throws InterruptedException{
        logger.debug("Updating cache store: " + treatment);


        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");


        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);
        List<Diagnosis> d = pdi.selectAllTreatement(treatment);
        Map<String,Patient> p = new HashMap<String,Patient>();
        Map<String,Diagnosis> diag = new HashMap<String, Diagnosis>();
        String firstName = "";
        Patient pp = null;

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        for(Diagnosis di : d) {
            firstName = ppi.selectById(di.getId()).getFirstName();
            pp = ppi.selectById(di.getId());
            p.put(firstName,pp);
            diag.put(firstName,di);
        }
        connector.close();

        updateMap(p,diag);

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
        m.sendMessage("Deleting patient personal info(Address only)");
        m.EmailConsumer();
        m.sendMessage("Deleting patient personal info(Address only)");
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
        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);

        checkCacheAndRemove(ppi.selectById(patientId));
        ppi.deletePatientById(patientId);
        pdi.deleteDiagnosisById(patientId);


        String output = "Removed Patient : " + patientName;

        connector.close();

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
        IMap<String, String> cacheMap = Hazelcast.getHazelcastInstanceByName("hospitalsys").getMap("patients");
        cacheMap.clear();
        String output = "System Deleted : " + keyspaceName;

        return Response.status(200).entity(output).build();

    }

    public synchronized void updateMap(Map<String,Patient> patientMap, Map<String,Diagnosis> diagnoses) {
        IMap<String, String> cacheMap = Hazelcast.getHazelcastInstanceByName("hospitalsys").getMap("patients");

        for (String pb : cacheMap.keySet()) {
            logger.debug("Old Cached Patient List: " + pb);
        }
        cacheMap.clear();


        for (String pat : patientMap.keySet()) {
            String info = "Here are your details: " + "\n"
                    + "Address: " + patientMap.get(pat).getAddress() + "\n"
                    + "Diagnosis: " + diagnoses.get(pat).getDiagnosis() + "\n"
                    + "Treatment: " + diagnoses.get(pat).getTreatment() + "\n";
            cacheMap.put(pat, info);
        }

        for(String pi : cacheMap.keySet())
        {
            logger.debug("Updated Cached Patient List: " + pi);
        }
    }

    public synchronized void addToCachedMap(Patient p, Diagnosis d) {
        IMap<String, String> cacheMap = Hazelcast.getHazelcastInstanceByName("hospitalsys").getMap("patients");

        for (String pb : cacheMap.keySet()) {
            logger.debug("Old Cached Patient List: " + pb);
        }


        String info = "Here are your details: " + "\n"
                + "Address: " + p.getAddress() + "\n"
                + "Diagnosis: " + d.getDiagnosis() + "\n"
                + "Treatment: " + d.getTreatment() + "\n";
        cacheMap.put(p.getFirstName(), info);


        for(String pi : cacheMap.keySet())
        {
            logger.debug("Updated Cached Patient List: " + pi);
        }
    }

    public synchronized void checkCacheAndRemove(Patient p) {
        IMap<String, String> cacheMap = Hazelcast.getHazelcastInstanceByName("hospitalsys").getMap("patients");

        for (String pb : cacheMap.keySet()) {
            logger.debug("Old Cached Patient List: " + pb);
        }

        Map<String, String> tempmap = new HashMap<String,String>();
        for (String pat : cacheMap.keySet()) {
            if(!(p.getFirstName().equals(pat))) {
                tempmap.put(pat,cacheMap.get(pat));
                logger.debug("Adding to temp Cache");
            }
        }
        cacheMap.clear();
        cacheMap.putAll(tempmap);


        for(String pi : cacheMap.keySet())
        {
            logger.debug("Updated Cached Patient List: " + pi);
        }
    }

    public void getPolicy()
    {
        try{
        URL url = new URL("http://localhost:8080/rest/policy_system/retrievePolicy");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");


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
            this.currentPolicy=treatment;
        }


        conn.disconnect();

    }
        catch(Exception e)
    {
        logger.debug(e);
    }

}





}