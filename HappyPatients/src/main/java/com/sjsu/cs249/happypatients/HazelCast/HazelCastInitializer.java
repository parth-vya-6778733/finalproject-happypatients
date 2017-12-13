package com.sjsu.cs249.happypatients.HazelCast;

import com.datastax.driver.core.Session;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.sjsu.cs249.happypatients.Cassandra.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class HazelCastInitializer {
    private static final Logger logger = Logger.getLogger(HazelCastInitializer.class);

    public void init(String initialTreatment) {
        Config config = new Config();
        config.setInstanceName("my-instance");
        Hazelcast.newHazelcastInstance(config);

        CassandraConnector connector = new CassandraConnector();
        connector.connect("127.0.0.1", 9042);
        Session session = connector.getSession();
        KeyspaceRepository sr = new KeyspaceRepository(session);
        sr.useKeyspace("hospitalOps");
        PatientPersonalInfo ppi = new PatientPersonalInfo(session);
        PatientDiagnosisInfo pdi = new PatientDiagnosisInfo(session);
        List<Diagnosis> d = pdi.selectAllTreatement(initialTreatment);
        List<Patient> p = new ArrayList<Patient>();

        HazelcastInstance hci = Hazelcast.getHazelcastInstanceByName("my-instance");
        List<Patient> cacheMap = hci.getList("patients");
        for(Diagnosis di : d) {
            p.add(ppi.selectById(di.getId()));
        }
        cacheMap = p;

        for(Patient pi : cacheMap)
        {
            logger.debug("Cached Patient: " + pi.getFirstName());
        }

        connector.close();


    }

    public void destroy()
    {
        Config config = new Config();
        config.setInstanceName("my-instance");
        Hazelcast.newHazelcastInstance(config);

        HazelcastInstance hci = Hazelcast.getHazelcastInstanceByName("my-instance");
        hci.getList("patients").destroy();
    }


}
