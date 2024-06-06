package com.infoworks.lab.services.impl;

import com.infoworks.lab.domain.entities.SequenceGenerator;
import com.infoworks.lab.services.GeneratorService;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service("seqGenService")
public class SequenceGeneratorService implements GeneratorService {

    private final MongoOperations mongoOperations;

    public SequenceGeneratorService(MongoOperations mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    @Override
    public long getNext(String id) {
        SequenceGenerator generator = mongoOperations.findAndModify(
                Query.query(where("_id").is(id))
                , new Update().inc("seq", 1)
                , options().returnNew(true)
                , SequenceGenerator.class
        );
        if (generator == null) {
            generator = new SequenceGenerator();
            generator.setId(id);
            generator.setSeq(1);
            mongoOperations.insert(generator);
        }
        return generator.getSeq();
    }
}
