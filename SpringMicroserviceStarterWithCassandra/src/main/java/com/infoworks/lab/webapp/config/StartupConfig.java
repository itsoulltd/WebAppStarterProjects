package com.infoworks.lab.webapp.config;

import com.infoworks.lab.domain.entities.Passenger;
import com.it.soul.lab.cql.CQLExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Arrays;

@Component
public class StartupConfig implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(StartupConfig.class);
    private CQLExecutor executor;

    public StartupConfig(CQLExecutor executor) {
        this.executor = executor;
    }

    @Override
    public void run(String... args) throws Exception {
        //Initialize Cassandra Tables from their entities:
        createTables(executor, Passenger.class);
    }

    private void createTables(CQLExecutor cqlExecutor, Class...classes) {
        Arrays.stream(classes).forEach(aClass -> {
            try {
                boolean created = cqlExecutor.createTable(aClass);
                LOG.info("{} is {}", aClass.getSimpleName(), (created ? "YES" : "NO"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void dropsTables(CQLExecutor cqlExecutor, Class...classes){
        Arrays.stream(classes).forEach(aClass -> {
            try {
                boolean dropped = cqlExecutor.dropTable(aClass);
                LOG.info("{} is {}", aClass.getSimpleName(), (dropped ? "YES" : "NO"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
