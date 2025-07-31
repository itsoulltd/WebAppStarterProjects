package com.infoworks.lab.template;

import com.infoworks.lab.util.services.iProperties;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.nio.file.Path;
import java.nio.file.Paths;

//@DataMongoTest
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {MongoConfig.class})
//@TestPropertySource(locations = {"classpath:application.properties", "classpath:application-mongo.properties"})
public class MongoTestTemplate {

    private String applicationName;
    private String schema;
    private String host;
    private String port;
    private String url;
    private String username;
    private String password;
    private String persistenceUnitName;

    private MongodExecutable mongodExecutable;
    private MongoTemplate mongoTemplate;

    @Before
    public void setUp() throws Exception {
        Path path = Paths.get("src","test","resources","application.properties");
        String absolutePath = path.toAbsolutePath().toString();
        iProperties application = iProperties.create(absolutePath, null);
        applicationName = application.read("spring.application.name");
        //
        path = Paths.get("src","test","resources","application-mongo.properties");
        absolutePath = path.toAbsolutePath().toString();
        iProperties mongo = iProperties.create(absolutePath, null);
        schema = mongo.read("mongo.db.url.schema");
        host = mongo.read("mongo.db.host");
        port = mongo.read("mongo.db.port");
        url = String.format("%s%s:%s", schema, host, port);
        username = mongo.read("mongo.db.username");
        password = mongo.read("mongo.db.password");
        persistenceUnitName = mongo.read("mongo.db.name");
        //
        IMongodConfig mongodbConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(host, Integer.parseInt(port), Network.localhostIsIPv6()))
                .build();
        try {
            MongodStarter starter = MongodStarter.getDefaultInstance();
            mongodExecutable = starter.prepare(mongodbConfig);
            mongodExecutable.start();
            MongoClient client = MongoClients.create(url);
            mongoTemplate = new MongoTemplate(client, persistenceUnitName);
        } finally {
            if (mongodExecutable != null) {
                mongodExecutable.stop();
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
        }
    }

    @Test
    public void mongoTemplateConfigured() {
        System.out.println(String.format("%s loading....", applicationName));
        Assert.assertTrue(mongoTemplate != null);
        System.out.println(String.format("Embedded Url: %s", url));
    }
}