package com.infoworks.lab.app.config;

import com.infoworks.lab.jsql.ExecutorType;
import com.infoworks.lab.jsql.JsqlConfig;
import com.it.soul.lab.sql.SQLExecutor;

import java.sql.SQLException;

public class DatabaseBootstrap {

    public static void createTables() {
        JsqlConfig config = new JsqlConfig();
        try (SQLExecutor executor = (SQLExecutor) config.create(ExecutorType.SQL, System.getenv("app.db.name"))){
            String createPassenger = "CREATE TABLE IF NOT EXISTS `Passenger` (\n" +
                    "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                    "  `active` bit(1) NOT NULL,\n" +
                    "  `age` int(11) NOT NULL,\n" +
                    "  `dob` datetime DEFAULT NULL,\n" +
                    "  `name` varchar(255) DEFAULT NULL,\n" +
                    "  `sex` varchar(255) DEFAULT NULL,\n" +
                    "  PRIMARY KEY (`id`)\n" +
                    ") ENGINE=MyISAM AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci";
            executor.executeDDLQuery(createPassenger);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
