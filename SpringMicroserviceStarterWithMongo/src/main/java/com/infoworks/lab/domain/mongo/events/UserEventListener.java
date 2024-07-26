package com.infoworks.lab.domain.mongo.events;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.services.GeneratorService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.stereotype.Component;

@Component
public class UserEventListener extends AbstractMongoEventListener<User> {

    public final static String SEQUENCE_KEY = "user_seq";
    private GeneratorService genService;

    public UserEventListener(@Qualifier("seqGenService") GeneratorService genService) {
        this.genService = genService;
    }

    @Override
    public void onBeforeConvert(BeforeConvertEvent<User> event) {
        if (event.getSource().getId() == null
                || event.getSource().getId() <= 0) {
            long id = genService.getNext(SEQUENCE_KEY);
            event.getSource().setId(Long.valueOf(id).intValue());
        }
    }
}
