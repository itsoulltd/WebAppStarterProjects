package com.infoworks.services.tasks;

import com.infoworks.objects.Message;
import com.infoworks.objects.Response;
import com.infoworks.orm.Property;
import com.infoworks.tasks.ExecutableTask;

import java.time.Duration;
import java.util.Optional;

public class WaitTask extends ExecutableTask<Message, Response> {

    public WaitTask() {
        this(Duration.ofSeconds(5));
    }

    public WaitTask(Duration duration) {
        super(new Property("wait", String.valueOf(duration.toMillis())));
    }

    @Override
    public Response execute(Message message) throws RuntimeException {
        long wait = Long.valueOf(Optional.ofNullable(getPropertyValue("wait"))
                .orElse(String.valueOf(Duration.ofSeconds(5)))
                .toString());
        //Going to sleep for a while :D
        try {
            Thread.sleep(wait);
        } catch (InterruptedException e) {}
        //
        return new Response().setMessage("Slept for " + wait + " millisecond.").setStatus(200);
    }
}
