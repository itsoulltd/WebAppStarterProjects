package com.infoworks.lab.controllers.rest;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/v1/socket/session")
public class SocketSessionStatusController {

    private StatefulRedisConnection connection;

    public SocketSessionStatusController(@Autowired StatefulRedisConnection connection) {
        this.connection = connection;
    }

    @GetMapping("/clear")
    public void clearCache(){
        //
    }

    @GetMapping("/print/keys/{pattern}")
    public List<String> printKeys(@PathVariable("pattern") String pattern){
        if (connection != null){
            if (pattern == null || pattern.isEmpty())
                pattern = "spring:session:*";
            if (pattern.length() == 1 && pattern.trim().startsWith("*"))
                pattern = "spring:session:*";
            //
            RedisCommands<String , String> cmd = connection.sync();
            List<String> keys = cmd.keys(pattern);
            keys.forEach(key-> System.out.println(key));
            return keys;
        }
        return Arrays.asList("No Keys Found!");
    }

}
