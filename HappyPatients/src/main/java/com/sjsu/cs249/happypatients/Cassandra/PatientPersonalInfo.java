package com.sjsu.cs249.happypatients.Cassandra;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.log4j.Logger;

public class PatientPersonalInfo {

        private static final String TABLE_NAME = "PatientPersonalInfo";
        private static final Logger logger = Logger.getLogger(PatientPersonalInfo.class);

        private Session session;

        public PatientPersonalInfo(Session session) {
            this.session = session;
        }

        public void createTablePatients() {
            StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(TABLE_NAME).append("(").append("id uuid PRIMARY KEY, ").append("firstName text,").
                    append("lastName text,").append("birthDate text,").append("address text,").append("phoneNumber text);");

            final String query = sb.toString();
            session.execute(query);
        }

        public void alterTablePatients(String columnName, String columnType) {
            StringBuilder sb = new StringBuilder("ALTER TABLE ").append(TABLE_NAME).append(" ADD ").append(columnName).append(" ").append(columnType).append(";");

            final String query = sb.toString();
            session.execute(query);
        }

        public void insertPatient(Patient patient) {
            StringBuilder sb = new StringBuilder("INSERT INTO ").append(TABLE_NAME).append("(id, firstName, lastName, birthDate, address, phoneNumber) ").
                    append("VALUES (").append(patient.getId()).append(", '").append(patient.getFirstName()).append("', '").append(patient.getLastName()).append("', '")
                    .append(patient.getBirthDate()).append("', '").append(patient.getAddress()).append("', '").append(patient.getPhoneNumber()).append("');");

            final String query = sb.toString();
            session.execute(query);
        }

        public void updateFirstName(UUID id, String name) {
            StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET firstName='").
                    append(name).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

            final String query = sb.toString();
            logger.debug("Query: ");
            logger.debug(query);
            session.execute(query);
        }

    public void updateLastName(UUID id, String name) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET lastName='").
                append(name).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

    public void updateBirthdate(UUID id, String bday) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET birthdate='").
                append(bday).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

    public void updateAddress(UUID id, String addy) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET address='").
                append(addy).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

    public void updatePhoneNumber(UUID id, String pn) {
        StringBuilder sb = new StringBuilder("UPDATE ").append(TABLE_NAME).append(" SET phonenumber='").
                append(pn).append("'").append(" WHERE id=").append(id).append(" IF EXISTS").append(";");

        final String query = sb.toString();
        logger.debug("Query: ");
        logger.debug(query);
        session.execute(query);
    }

        public synchronized Patient selectById(UUID id) {
            StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME).append(" WHERE id = ").append(id).append(";");

            final String query = sb.toString();

            ResultSet rs = session.execute(query);

            List<Patient> patients = new ArrayList<Patient>();

            for (Row r : rs) {
                Patient p = new Patient(r.getUUID("id"), r.getString("firstName"), r.getString("lastName"),
                        r.getString("birthDate") , r.getString("address"), r.getString("phoneNumber"));
                patients.add(p);
            }

            return patients.get(0);
        }

        public List<Patient> selectAll() {
            StringBuilder sb = new StringBuilder("SELECT * FROM ").append(TABLE_NAME);

            final String query = sb.toString();
            ResultSet rs = session.execute(query);

            List<Patient> patients = new ArrayList<Patient>();

            for (Row r : rs) {
                Patient p = new Patient(r.getUUID("id"), r.getString("firstName"), r.getString("lastName"),
                        r.getString("birthDate") , r.getString("address"), r.getString("phoneNumber"));
                patients.add(p);
            }
            return patients;
        }

    public void deleteAddress(UUID id) {
        StringBuilder sb = new StringBuilder("DELETE address FROM ").append(TABLE_NAME).append(" WHERE id = ").append(id).append(";");

        final String query = sb.toString();

        ResultSet rs = session.execute(query);
    }


    public void deletePatientById(UUID id) {
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
