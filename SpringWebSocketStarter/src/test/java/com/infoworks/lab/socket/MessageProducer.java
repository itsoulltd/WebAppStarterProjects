package com.infoworks.lab.socket;

import com.infoworks.lab.client.spring.SocketTemplate;
import com.infoworks.lab.client.spring.SocketType;
import com.infoworks.lab.rest.models.Message;
import com.infoworks.lab.simulator.JsonLogWriter;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

public class MessageProducer extends com.infoworks.lab.simulator.Runtime {

    private JsonLogWriter writer = new JsonLogWriter(this.getClass(), "MessageProducer");

    @Override
    protected void run() {
        //writer.createIfNotExist(true);
        try {
            SocketTemplate socket = new SocketTemplate(SocketType.Standard);
            socket.setAuthorizationHeader("TOKEN");
            socket.setQueryParam("user_name", "user_name");
            socket.setQueryParam("secret", "app_secret");
            socket.connect("ws://localhost:8080/process");
            //
            String question = "Wanna Start? -> ";
            while (boolScanner(question)) {
                emitLocations(300, socket, (status) -> {
                    System.out.println(status);
                });
                question = "Wanna Start Again? -> ";
            }
            System.exit(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    private void emitLocations(long waitRatio, SocketTemplate socket, Consumer<String> onCompletion) {
        CountDownLatch latch = new CountDownLatch(1);
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5, 6);
        //
        long emitInterval = waitRatio * (new Random().nextInt(9) + 1);
        flux.delayElements(Duration.ofMillis(emitInterval))
                .doOnComplete(latch::countDown)
                .subscribe(intVal -> {
                    Message message = new Message();
                    message.setPayload("Hello " + intVal);
                    socket.send("/event", message);
                    System.out.println(message.toString());
                });

        try {
            latch.await();
        } catch (InterruptedException e) {}
    }

}
