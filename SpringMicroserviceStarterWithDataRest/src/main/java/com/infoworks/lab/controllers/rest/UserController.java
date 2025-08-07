package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.rest.models.SearchQuery;
import com.it.soul.lab.data.base.DataSource;
import com.it.soul.lab.sql.QueryExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

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
