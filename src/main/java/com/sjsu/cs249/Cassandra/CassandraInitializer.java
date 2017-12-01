package com.sjsu.cs249.Cassandra;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.sjsu.cs249.services.MessageBroker;
import org.apache.log4j.Logger;

public class CassandraInitializer {
    private static final Logger logger = Logger.getLogger(CassandraInitializer.class);

    public static void main(String args[])
    {
//        CassandraConnector connector = new CassandraConnector();
//        connector.connect("127.0.0.1", 9042);
//        Session session = connector.getSession();
//
//        KeyspaceRepository sr = new KeyspaceRepository(session);
//        sr.createKeyspace("hospitalOps", "SimpleStrategy", 1);
//        sr.useKeyspace("hospitalOps");
//
//        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
//        ppi.createTablePatients();
//
//        Patient patient = new Patient(UUIDs.timeBased(), "John", "Doe", "1992-09-22", "555 SJSU drive San Jose, CA", "408-555-5555");
//        ppi.insertPatient(patient);
//
//        patient.setFirstName("Arnold");
//        ppi.updateFirstName(patient.getId(),patient.getFirstName());
//
//        connector.close();

        //Test Message Broker

        MessageBroker m = new MessageBroker();

        m.sendMessage("Hello World");

        for(String s : m.receiveMessages())
        {
            System.out.println("Messages: " + s);
        }
    }
}
