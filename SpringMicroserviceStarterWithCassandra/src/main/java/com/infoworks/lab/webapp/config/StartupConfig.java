package com.infoworks.lab.webapp.config;

import com.infoworks.lab.domain.entities.User;
import com.it.soul.lab.cql.CQLExecutor;
import com.it.soul.lab.cql.query.AlterAction;
import com.it.soul.lab.sql.query.models.Property;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Arrays;

@Component
public class StartupConfig implements CommandLineRunner {

    private static Logger LOG = LoggerFactory.getLogger(StartupConfig.class);
    private CQLExecutor executor;

    @Value("${cassandra.db.drop.table.onstart}")
    private boolean dropOnStart;

    public StartupConfig(CQLExecutor executor) {
        this.executor = executor;
    }

    @EventListener
    public void handleContextStartedListener(ContextRefreshedEvent event){
        System.out.println("ContextStarted....");
    }

    @EventListener
    public void handleContextStoppedListener(ContextClosedEvent event){
        System.out.println("ContextStopped....");
    }

    @Override
    public void run(String... args) throws Exception {
        //Initialize Cassandra Tables from their entities:
        if (dropOnStart) {
            dropsTables(executor, User.class);
        }
        createTables(executor, User.class);
        //e.g. How to alter an existing Entity Property:
        //executeAlter(executor, Passenger.class, AlterAction.ADD, new Property("<new-property-name>", "<property-datatype-as-value>"));
        //executeAlter(executor, Passenger.class, AlterAction.DROP, new Property("<property-to-drop>"));
        //executeAlter(executor, Passenger.class, AlterAction.RENAME, new Property("<property-to-rename>", "<property-new-name>"));
        //executeAlter(executor, Passenger.class, AlterAction.ALTER, new Property("<name-of-non-key-property>", "<property-datatype-as-value-to-alter>"));
    }

    private void executeAlter(CQLExecutor cqlExecutor, Class aClass, AlterAction action, Property property) {
        try {
            boolean res = cqlExecutor.alterTable(aClass, action, property);
            LOG.info("{} is Altered By {} {}", aClass.getSimpleName(), action.name(), (res ? "YES" : "NO"));
        } catch (SQLException e) {
            LOG.info("{} is Altered By {} Failed with {}", aClass.getSimpleName(), action.name(), e.getMessage());
        }
    }

    private void createTables(CQLExecutor cqlExecutor, Class...classes) {
        Arrays.stream(classes).forEach(aClass -> {
            try {
                boolean created = cqlExecutor.createTable(aClass);
                LOG.info("{} is created {}", aClass.getSimpleName(), (created ? "YES" : "NO"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void dropsTables(CQLExecutor cqlExecutor, Class...classes){
        Arrays.stream(classes).forEach(aClass -> {
            try {
                boolean dropped = cqlExecutor.dropTable(aClass);
                LOG.info("{} is dropped {}", aClass.getSimpleName(), (dropped ? "YES" : "NO"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
