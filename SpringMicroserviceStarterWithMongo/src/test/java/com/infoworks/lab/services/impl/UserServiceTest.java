package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.domain.repositories.UserRepository;
import com.infoworks.lab.services.GeneratorService;
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
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.MappingMongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

//@DataMongoTest
//@RunWith(SpringRunner.class)
//@SpringBootTest(classes = {MongoConfig.class})
//@TestPropertySource(locations = {"classpath:application.properties", "classpath:application-mongo.properties"})
public class UserServiceTest {

    private String applicationName;
    private String url;
    private String persistenceUnitName;

    private MongodExecutable mongodExecutable;
    private UserService userService;

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
        String schema = mongo.read("mongo.db.url.schema");
        String host = mongo.read("mongo.db.host");
        String port = mongo.read("mongo.db.port");
        String dbName = mongo.read("mongo.db.name");
        String dbQuery = mongo.read("mongo.db.url.query");
        String username = mongo.read("mongo.db.username");
        String password = mongo.read("mongo.db.password");
        //url = String.format("%s%s:%s@%s:%s/%s%s", schema, username, password, host, port, dbName, dbQuery);
        url = String.format("%s%s:%s/%s",schema, host, port, dbName);
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
            MongoTemplate mongoTemplate = new MongoTemplate(client, persistenceUnitName);
            Assert.assertNotNull(mongoTemplate);
            //Config:
            GeneratorService genService = new SequenceGeneratorService(mongoTemplate);
            Assert.assertNotNull(genService);
            //
            BasicMongoPersistentEntity<?> userMappingContext = new MongoMappingContext().getPersistentEntity(User.class);
            MongoEntityInformation<User, Integer> userEntityMetadata = new MappingMongoEntityInformation(userMappingContext);
            UserRepository userRepository = new UserRepositoryProxy(userEntityMetadata, mongoTemplate);
            Assert.assertNotNull(userRepository);
            //
            userService = new UserService(userRepository, genService);
            Assert.assertNotNull(userService);
            System.out.println("setUp: mongo-startup well!");
        } catch (Exception e){
            System.out.println(e.getMessage());
            if (mongodExecutable != null) {
                mongodExecutable.stop();
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        if (mongodExecutable != null) {
            mongodExecutable.stop();
            System.out.println("tearDown: mongo-gracefully stopped!");
        }
    }

    @Test
    public void mongoTemplateConfigured() {
        System.out.println(String.format("%s loading....", applicationName));
        System.out.println(String.format("Embedded Url: %s", url));
    }

    @Test
    public void saveTest() {
        User user = new User();
        user.setName("Tic Toc");
        user.setEmail("tic@gmail.com");
        user.setSex("MALE");
        user.setAge(20);
        //Add
        String _id = userService.add(user);
        Assert.assertNotNull(_id);
        //Read
        User user1 = userService.read("Tic Toc");
        Assert.assertNotNull(user1);
        System.out.println(user1.getEmail());
    }

    @Test
    public void updateTest() {
        User user = new User();
        user.setName("Tic Toc");
        user.setEmail("tic@gmail.com");
        user.setSex("MALE");
        user.setAge(20);
        //Add
        String _id = userService.add(user);
        Assert.assertNotNull(_id);
        //Read
        String searchKey = "Tic Toc";
        User user1 = userService.read(searchKey);
        Assert.assertNotNull(user1);
        System.out.println("Before Gender: " + user1.getSex());
        //Update
        user1.setSex("FEMALE");
        User updated = userService.replace(searchKey, user1);
        Assert.assertEquals(user1.getSex(), updated.getSex());
        System.out.println("After Gender: " + updated.getSex());
    }
}

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class UserRepositoryProxy extends SimpleMongoRepository<User, Integer> implements UserRepository {

    private MongoOperations mongoOperations;

    public UserRepositoryProxy(MongoEntityInformation<User, Integer> metadata, MongoOperations mongoOperations) {
        super(metadata, mongoOperations);
        this.mongoOperations = mongoOperations;
    }

    @Override
    public List<User> findByName(String name) {
        List<User> result = mongoOperations.find(
                Query.query(Criteria.where("name").is(name))
                , User.class);
        return result;
    }
}

