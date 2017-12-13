package com.sjsu.cs249.happypatients.Cassandra;

import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import org.apache.log4j.Logger;

public class CassandraInitializer {
    private static final Logger logger = Logger.getLogger(CassandraInitializer.class);
    CassandraConnector connector = new CassandraConnector();

    public void init()
    {
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();

        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.createKeyspace("hospitalOps", "SimpleStrategy", 1);
        sr.useKeyspace("hospitalOps");

        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        ppi.createTablePatients();

        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);
        pdi.createTableDiagnosis();

        Patient p1 = new Patient(UUIDs.timeBased(), "John", "Doe", "1992-09-22", "555 SJSU drive San Jose, CA", "408-555-5555");
        ppi.insertPatient(p1);

        Diagnosis d1 = new Diagnosis(p1.getId(),"6-2","200lbs","Alzheimer","ONGOING");
        pdi.insertDiagnosis(d1);

        Patient p2 = new Patient(UUIDs.timeBased(), "Jack", "Smith", "1992-09-22", "555 SJSU drive San Jose, CA", "408-555-5555");
        ppi.insertPatient(p2);

        Diagnosis d2 = new Diagnosis(p2.getId(),"6-2","200lbs","Cancer","ICU");
        pdi.insertDiagnosis(d2);

        Patient p3 = new Patient(UUIDs.timeBased(), "Tina", "Turner", "1992-09-22", "555 SJSU drive San Jose, CA", "408-555-5555");
        ppi.insertPatient(p3);

        Diagnosis d3 = new Diagnosis(p3.getId(),"6-2","200lbs","Lung Disease","ONGOING");
        pdi.insertDiagnosis(d3);

        Patient p4 = new Patient(UUIDs.timeBased(), "Tom", "Sawyer", "1992-09-22", "555 SJSU drive San Jose, CA", "408-555-5555");
        ppi.insertPatient(p4);

        Diagnosis d4 = new Diagnosis(p4.getId(),"6-2","200lbs","Fractured Tibia","URGENTCARE");
        pdi.insertDiagnosis(d4);

        Patient p5 = new Patient(UUIDs.timeBased(), "Lola", "Jones", "1992-09-22", "555 SJSU drive San Jose, CA", "408-555-5555");
        ppi.insertPatient(p5);

        Diagnosis d5 = new Diagnosis(p5.getId(),"6-2","200lbs","Stroke","ICU");
        pdi.insertDiagnosis(d5);


        connector.close();
    }

    public void destroy()
    {
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();

        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.createKeyspace("hospitalOps", "SimpleStrategy", 1);
        sr.useKeyspace("hospitalOps");

        sr.deleteKeyspace("hospitalOps");
        connector.close();
    }
}
