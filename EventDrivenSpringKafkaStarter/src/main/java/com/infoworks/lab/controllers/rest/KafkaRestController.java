package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.beans.tasks.definition.Task;
import com.infoworks.lab.beans.tasks.definition.TaskQueue;
import com.infoworks.lab.domain.beans.tasks.mocks.AbortTask;
import com.infoworks.lab.domain.beans.tasks.mocks.ConsolePrintTask;
import com.infoworks.lab.rest.models.Message;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import scala.collection.JavaConversions;

import java.util.Properties;

@RestController
@RequestMapping("/v1/kafka")
public class KafkaRestController {

    private static Logger LOG = LoggerFactory.getLogger(KafkaRestController.class.getSimpleName());
    private ZkClient zooKeeper;
    private KafkaTemplate<String, String> kafkaTemplate;
    private TaskQueue taskQueue;

    @Value("${app.zookeeper.servers}")
    private String zookeeperServers;

    public KafkaRestController(@Qualifier("myZooKeeper") ZkClient zooKeeper
            , @Qualifier("kafkaTextTemplate") KafkaTemplate kafkaTemplate
            , @Qualifier("taskDispatchQueue") TaskQueue taskQueue) {
        this.zooKeeper = zooKeeper;
        this.kafkaTemplate = kafkaTemplate;
        this.taskQueue = taskQueue;
    }

    private boolean isTopicExist(String topic){
        ZkClient zkClient = zooKeeper;
        ZkUtils utils = new ZkUtils(zkClient, new ZkConnection(zookeeperServers), false);
        Iterable<String> topics = JavaConversions.asJavaIterable(utils.getAllTopics());
        boolean isMatched = false;
        for (String match : topics) {
            if (match.contains(topic)) {isMatched = true; break;}
        }
        return isMatched;
    }

    private void createTopic(String topicName, int partitions, int replications) throws RuntimeException{
        Properties topicConfig = new Properties();
        ZkUtils utils = new ZkUtils(zooKeeper, new ZkConnection(zookeeperServers), false);
        AdminUtils.createTopic(utils
                , topicName
                , partitions
                , replications
                , topicConfig
                , RackAwareMode.Enforced$.MODULE$);
    }

    @PostMapping("/create/topic/{topicName}")
    public ResponseEntity<String> createTopicRequest(@PathVariable String topicName
            , @RequestParam(name = "partitions", required = false, defaultValue = "1") int partitions
            , @RequestParam(name = "replications", required = false, defaultValue = "1") int replications){
        //
        try {
            if(isTopicExist(topicName)) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(String.format("Topic: %s already exist!", topicName));
            }
            createTopic(topicName, partitions, replications);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.ok(topicName + " is Created!");
    }

    @PostMapping("/post/message/{topicName}")
    public ResponseEntity<String> postMessage(@PathVariable String topicName
            , @RequestBody Message event){
        //
        if(!isTopicExist(topicName)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(String.format("Topic: %s does not exist.", topicName));
        }
        kafkaTemplate.send(topicName, event.toString());
        return ResponseEntity.ok("Message posted: " + topicName);
    }

    @GetMapping("/queue/task/{message}")
    public ResponseEntity<String> addToQueue(@PathVariable("message") final String message){
        //
        Task task;
        if (message.trim().toLowerCase().startsWith("abort")){
            Message mac = new Message().setPayload(String.format("{\"message\":\"%s\"}", message));
            AbortTask abortTask = new AbortTask();
            abortTask.setMessage(mac);
            task = abortTask;
        }else{
            Message mac = new Message().setPayload(String.format("{\"message\":\"%s\"}", message));
            ConsolePrintTask consolePrintTask = new ConsolePrintTask();
            consolePrintTask.setMessage(mac);
            task = consolePrintTask;
        }
        taskQueue.add(task);
        //Test:
        taskQueue.onTaskComplete((message1, state) -> {
            System.out.println("RUNNING ON " + Thread.currentThread().getName());
            System.out.println(state.name());
            System.out.println(message1.toString());
        });
        System.out.println("/queue/task/ " + "RETURNING");
        System.out.println("RUNNING ON " + Thread.currentThread().getName());
        //
        return new ResponseEntity(message, HttpStatus.OK);
    }
}
