package com.sjsu.cs249.happypatients.Cassandra;

import com.datastax.driver.core.utils.UUIDs;

import java.util.UUID;

public class Diagnosis {

    private UUID id;

    private String height;

    private String weight;

    private String diagnosis;

    private String treatment;


    Diagnosis() {
    }

    public Diagnosis(UUID id, String height, String weight, String diagnosis, String treatment) {
        this.id = id;
        this.height = height;
        this.weight = weight;
        this.diagnosis = diagnosis;
        this.treatment = treatment;

    }

    public Diagnosis(String id, String height, String weight, String diagnosis, String treatment) {
        this.id = UUID.fromString(id);
        this.height = height;
        this.weight = weight;
        this.diagnosis = diagnosis;
        this.treatment = treatment;

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }


}
