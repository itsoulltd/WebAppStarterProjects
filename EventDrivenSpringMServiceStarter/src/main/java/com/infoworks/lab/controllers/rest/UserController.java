package com.infoworks.lab.controllers.rest;

import com.infoworks.data.cache.MemCache;
import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.domain.models.ItemCount;
import com.infoworks.lab.domain.repositories.RestRepository;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController implements RestRepository<User, String> {

    private MemCache<User> dataSource;

    public UserController(@Qualifier("userService") MemCache dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/rowCount")
    public ItemCount rowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        count.setStatus(200);
        return count;
    }

    @GetMapping
    public List<User> fetch(
            @RequestParam(value = "limit", defaultValue = "10", required = false) Integer limit
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page){
        //
        if (limit < 0) limit = 10;
        if (page < 0) page = 0;
        List<User> users = Arrays.asList(dataSource.readSync(page, limit));
        return users;
    }

    @GetMapping("/findByKey")
    public User read(@RequestParam("key") String key){
        User user = dataSource.read(key);
        return user;
    }

    @PostMapping
    public User insert(@Valid @RequestBody User user){
        //
        dataSource.put(user.getName(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user
            , @Parameter(hidden = true) @RequestParam(value = "name", required = false) String name){
        //
        dataSource.replace(user.getName(), user);
        return user;
    }

    @DeleteMapping
    public boolean delete(@RequestParam("name") String name){
        return dataSource.remove(name) != null;
    }

    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    @Override
    public Class<User> getEntityType() {
        return User.class;
    }

}
