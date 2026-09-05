package com.infoworks.domain.repositories;

import com.infoworks.domain.entities.User;
import com.infoworks.sql.query.pagination.Pagination;
import com.infoworks.sql.query.pagination.SearchQuery;
import com.infoworks.sql.query.pagination.SortOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.List;

@DataJpaTest
class SearchableRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void search() {
        System.out.println("Test For " + SearchableRepositoryTest.class.getSimpleName());
        Assertions.assertNotNull(userRepository);
        //
        SearchQuery query = Pagination.of(SearchQuery.class, 0, 10, SortOrder.ASC);
        query.add("name").isEqualTo("James");
        List<User> results = userRepository.search(query, User.class);
        Assertions.assertNotNull(results);
        Assertions.assertEquals(results.size(), 0);
    }
}