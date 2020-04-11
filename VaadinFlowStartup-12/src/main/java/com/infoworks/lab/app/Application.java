package com.infoworks.lab.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

  
   /* public void contextRefreshedEvent(){
        Map<String, String> env = System.getenv();
        GeoTracker.shared().initialize(null, null);
        Map target = GeoTracker.shared().updateServiceURLs(env.get(WebResource.API_PUBLIC_DNS.key()));
        env.forEach((key, value) -> {
            if (key.startsWith("com.itsoul.lab")){
                target.put(key, value);
            }
        });
        if (target.size() > 0) GeoTracker.shared().loadProperties(target);
        System.out.println("API Gateway:" + WebResource.API_GATEWAY.value());
    }*/

}
