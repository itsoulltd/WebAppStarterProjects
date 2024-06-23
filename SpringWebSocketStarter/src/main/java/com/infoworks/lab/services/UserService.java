package com.infoworks.lab.services;

import com.infoworks.lab.cache.MemCache;
import com.infoworks.lab.datasources.LettuceDataSource;
import com.infoworks.lab.domain.entities.User;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisKeyCommands;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service("userService")
public class UserService extends MemCache<User> {

    private RedisClient client;
    private StatefulRedisConnection<String, String> connection;

    public UserService(RedisClient client) {
        super(new LettuceDataSource(client), User.class);
        this.client = client;
        this.connection = this.client.connect();
    }

    /**
     * To make a short key, we choose prefix: 'Class.getSimpleName() + "_"
     * @return
     */
    protected String maskPrefix() {
        String fullClsName = User.class.getSimpleName();
        return fullClsName + "_";
    }

    public String maskedKey(String key) {
        return maskPrefix() + key;
    }

    @Override
    public void put(String key, User user) {
        super.put(maskedKey(key), user);
    }

    @Override
    public String add(User e) {
        String key = String.valueOf(e.hashCode());
        this.put(maskedKey(key), e);
        return key;
    }

    @Override
    public User read(String key) {
        return super.read(maskedKey(key));
    }

    @Override
    public User remove(String key) {
        return super.remove(maskedKey(key));
    }

    @Override
    public User replace(String key, User user) {
        return super.replace(maskedKey(key), user);
    }

    @Override
    public User[] readSync(int offset, int pageSize) {
        if (offset < 0) offset = 0;
        //TODO (CAUTION): fetch cmd.keys(pattern) in Batch (in-future) to avoid memory-dumb.
        RedisKeyCommands<String, String> cmd = connection.sync();
        List<String> keys = cmd.keys(maskPrefix());
        List<String> targetMaskedKeys = keys.stream()
                .filter(key -> key.startsWith(maskPrefix()))
                .collect(Collectors.toList());
        targetMaskedKeys.sort(String::compareTo);
        //Pagination logic:
        List<User> results = new ArrayList<>();
        List<String> subMaskedKeys;
        if (targetMaskedKeys.size() > (offset + pageSize)) {
             subMaskedKeys = targetMaskedKeys.subList(offset, (offset + pageSize));
        } else {
            if (offset > targetMaskedKeys.size()) offset = 0;
            subMaskedKeys = targetMaskedKeys.subList(offset, targetMaskedKeys.size());
        }
        //Where logic:
        for (String maskedKey : subMaskedKeys) {
            results.add(super.read(maskedKey));
        }
        return results.toArray(new User[0]);
    }

    @Override
    public void readAsync(int offset, int pageSize, Consumer<User[]> consumer) {
        if (consumer != null)
            consumer.accept(readSync(offset, pageSize));
    }

    @Override
    public void close() throws Exception {
        if (connection != null){
            connection.close();
            connection = null;
        }
        if (client != null) {
            client.shutdown(50l, 70l, TimeUnit.MILLISECONDS);
            client = null;
        }
        super.close();
    }
}
