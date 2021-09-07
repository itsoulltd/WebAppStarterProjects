package com.infoworks.lab.webapp.config;

import com.infoworks.lab.jsql.JsqlConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupConfig implements CommandLineRunner {

    private JsqlConfig jsqlConfig;
    private String dbKey;

    public StartupConfig(@Autowired JsqlConfig jsqlConfig
            ,@Autowired @Qualifier("AppDBNameKey") String dbKey) {
        this.jsqlConfig = jsqlConfig;
        this.dbKey = dbKey;
    }

    @Override
    public void run(String... args) throws Exception {
        //
        System.out.println("Startup Done");
    }
}
