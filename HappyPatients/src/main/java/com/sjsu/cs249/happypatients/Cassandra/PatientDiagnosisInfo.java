package com.sjsu.cs249.happypatients.Cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

public class PatientDiagnosisInfo {

    private static final String TABLE_NAME = "PatientDiagnosisInfo";
    private static final Logger logger = Logger.getLogger(PatientDiagnosisInfo.class);

    private Session session;

    public PatientDiagnosisInfo(Session session) {
        this.session = session;
    }

    public void createTableDiagnosis() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append("(").append("id uuid PRIMARY KEY, ").append("height text,").
                append("weight text,").append("diagnosis text,").append("treatment text);");

        final String query = sb.toString();
        session.execute(query);
    }

    public void insertDiagnosis(Diagnosis diagnosis) {
        StringBuilder sb = new StringBuilder("INSERT INTO ").append(TABLE_NAME).append("(id, height, weight, diagnosis, treatment) ").
                append("VALUES (").append(diagnosis.getId()).append(", '").append(diagnosis.getHeight()).append("', '").append(diagnosis.getWeight()).append("', '")
                .append(diagnosis.getDiagnosis()).append("', '").append(diagnosis.getTreatment()).append("');");

        final String query = sb.toString();
        session.execute(query);
    }

    public Diagnosis selectById(UUID id) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE id = ").append(id).append(";");

        final String query = sb.toString();

        ResultSet rs = session.execute(query);

        List<Diagnosis> diagnosis = new ArrayList<Diagnosis>();

        for (Row r : rs) {
            Diagnosis d = new Diagnosis(r.getUUID("id"), r.getString("height"), r.getString("weight"),
                    r.getString("diagnosis") , r.getString("treatment"));
            diagnosis.add(d);
        }

        return diagnosis.get(0);
    }

    public synchronized List<Diagnosis> selectAllTreatement(String treatment) {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE treatment = '").append(treatment).append("' ALLOW FILTERING;");

        final String query = sb.toString();

        logger.debug(query);

        ResultSet rs = session.execute(query);

        List<Diagnosis> diagnosis = new ArrayList<Diagnosis>();

        for (Row r : rs) {
            Diagnosis d = new Diagnosis(r.getUUID("id"), r.getString("height"), r.getString("weight"),
                    r.getString("diagnosis") , r.getString("treatment"));
            diagnosis.add(d);
        }

        return diagnosis;
    }

    public List<Diagnosis> selectAll() {
        StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME);

        final String query = sb.toString();

        ResultSet rs = session.execute(query);

        List<Diagnosis> diagnosis = new ArrayList<Diagnosis>();

        for (Row r : rs) {
            Diagnosis d = new Diagnosis(r.getUUID("id"), r.getString("height"), r.getString("weight"),
                    r.getString("diagnosis") , r.getString("treatment"));
            diagnosis.add(d);
        }
        return diagnosis;
    }

    public void updateHeight(UUID id, String height) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET height='").
                append(height).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

    public void updateWeight(UUID id, String weight) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET weight='").
                append(weight).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

    public void updateDiagnosis(UUID id, String diagnosis) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET diagnosis='").
                append(diagnosis).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

    public void updateTreatment(UUID id, String treatment) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET treatment='").
                append(treatment).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

    public void deleteDiagnosisById(UUID id) {
        StringBuilder sb = new StringBuilder("DELETE FROM ").append(TABLE_NAME).append(" WHERE id = ").append(id).append(";");

        final String query = sb.toString();
        session.execute(query);
    }

    public void deleteTable(String tableName) {
        StringBuilder sb = new StringBuilder("DROP TABLE IF EXISTS ").append(tableName);

        final String query = sb.toString();
        session.execute(query);
    }

}
