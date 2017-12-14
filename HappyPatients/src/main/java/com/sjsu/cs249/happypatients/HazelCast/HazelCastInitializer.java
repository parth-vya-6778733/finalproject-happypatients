package com.sjsu.cs249.happypatients.HazelCast;

import com.datastax.driver.core.Session;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.sjsu.cs249.happypatients.Cassandra.*;
import org.apache.log4j.Logger;

import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class HazelCastInitializer {
    private static final Logger logger = Logger.getLogger(HazelCastInitializer.class);
//    public HazelcastInstance hci;

    public void init() {
        Config config = new Config().setInstanceName("hospitalsys")
                .addMapConfig(
                        new MapConfig()
                                .setName("patients")
                                .setMaxSizeConfig(new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE))
                                .setEvictionPolicy(EvictionPolicy.LRU));
        HazelcastInstance hazelcast = Hazelcast.newHazelcastInstance( config );


    }

    public void destroy() {

        Set<HazelcastInstance> hci = Hazelcast.getAllHazelcastInstances();
        for (HazelcastInstance h : hci) {
            h.getList("patients").destroy();
        }
    }


}
