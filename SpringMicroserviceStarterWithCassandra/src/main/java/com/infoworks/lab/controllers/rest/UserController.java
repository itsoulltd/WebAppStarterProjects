package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.repository.RestRepository;
import com.infoworks.lab.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController implements RestRepository<User, String> {

    private UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/rowCount")
    public ItemCount rowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(service.size()).longValue());
        return count;
    }

    @GetMapping
    public List<User> fetch(
            @RequestParam(value = "limit", defaultValue = "10", required = false) Integer limit
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page){
        //
        if (limit < 0) limit = 10;
        if (page < 0) page = 0;
        List<User> users = Arrays.asList(service.readSync(page, limit));
        return users;
    }

    @PostMapping("/search")
    public List<User> query(@RequestBody SearchQuery query){
        //
        List<User> users = service.search(query);
        return users;
    }

    @PostMapping
    public User insert(@Valid @RequestBody User user){
        //
        service.put(user.getUuid(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user
            , @ApiIgnore @RequestParam(value = "name", required = false) String name){
        //
        service.replace(user.getUuid(), user);
        return user;
    }

    @DeleteMapping
    public boolean delete(@RequestParam("uuid") String uuid){
        //
        User deleted = service.remove(uuid);
        return deleted != null;
    }

    @Override
    public String getPrimaryKeyName() {
        return "uuid";
    }

    @Override
    public Class<User> getEntityType() {
        return User.class;
    }

}
