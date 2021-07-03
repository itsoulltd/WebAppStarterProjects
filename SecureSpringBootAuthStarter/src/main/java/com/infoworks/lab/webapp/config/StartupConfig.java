package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jsql.JsqlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig implements CommandLineRunner {

    @Autowired
    private JsqlConfig jsqlConfig;

    @Autowired @Qualifier("AppDBNameKey")
    private String dbKey;

    @Override
    public void run(String... args) throws Exception {
        //How to use executor:
        //
    }
}
