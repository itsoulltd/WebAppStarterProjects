package com.infoworks.lab.webapp.config;

import com.it.soul.lab.cql.CQLExecutor;
import com.it.soul.lab.cql.query.ReplicationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.sql.SQLException;

@Configuration
@PropertySource("classpath:application-cassandra.properties")
public class CassandraConfig {

    private static Logger LOG = LoggerFactory.getLogger(CassandraConfig.class);

    @Value("${cassandra.db.username}") String username;
    @Value("${cassandra.db.password}") String password;
    @Value("${cassandra.db.keyspace}") String keyspace;
    @Value("${cassandra.db.host}") String host;
    @Value("${cassandra.db.port}") String port;

    @Bean
    public CQLExecutor executor(){
        CQLExecutor cqlExecutor = null;
        try {
            cqlExecutor = new CQLExecutor.Builder()
                    .connectTo(Integer.valueOf(port), host).build();
            Boolean newKeyspace = cqlExecutor.createKeyspace(keyspace, ReplicationStrategy.SimpleStrategy, 3);
            if (newKeyspace){
                cqlExecutor.switchKeyspace(keyspace);
            }
        } catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        return cqlExecutor;
    }
}
