package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.repository.RestRepository;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController implements RestRepository<User, String> {

    /**
     * Example of inject @Scope beans.
     * e.g. @RequestScope bean SQLExecutor to do JDBC-Calls to database.
     */
    @Resource(name = "executor")
    private QueryExecutor executor;

    private DataSource<String, User> dataSource;

    @Autowired
    public UserController(@Qualifier("userService") DataSource<String, User> dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/rowCount")
    public ItemCount rowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
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

    @PostMapping
    public User insert(@Valid @RequestBody User user){
        //
        dataSource.put(user.getName(), user);
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user
            , @ApiIgnore @RequestParam(value = "name", required = false) String name){
        //
        dataSource.replace(user.getName(), user);
        return user;
    }

    @DeleteMapping
    public boolean delete(@RequestParam("name") String name){
        //
        User deleted = dataSource.remove(name);
        return deleted != null;
    }

    @Override
    public String getPrimaryKeyName() {
        return "id";
    }

    @Override
    public Class<User> getEntityType() {
        return User.class;
    }

    @PostMapping("/search")
    public List<User> search(@RequestBody SearchQuery query) {
        //
        int limit = query.getSize();
        if (limit <= 0) limit = 10;
        List<User> users = null;
        try {
            users = User.read(User.class
                    , executor
                    , query.getPredicate());
        } catch (Exception e) {}
        //
        limit = users.size() > limit ? limit : users.size();
        users = (users != null && users.size() > 0)
                ? users.subList(0, limit)
                : new ArrayList<>();
        return users;
    }

}
