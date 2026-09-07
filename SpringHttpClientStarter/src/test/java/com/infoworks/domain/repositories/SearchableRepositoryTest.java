package com.infoworks.domain.repositories;

import com.infoworks.domain.entities.User;
import com.infoworks.sql.query.pagination.Pagination;
import com.infoworks.sql.query.pagination.SearchQuery;
import com.infoworks.sql.query.pagination.SortOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.function.Consumer;

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

    public void search(SearchQuery query, SearchableRepository repository, int pageCount, Consumer<List<User>> consumer) {
        int pageSize = (query.getSize() <= 0) ? 10 : query.getSize();
        query.setSize(pageSize); //update with validated pageSize.
        pageCount = (pageCount <= 0) ? 1 : pageCount;
        int start = 1; //SearchableRepository page-number is 1-based.
        while (start <= pageCount) {
            query.setPage(start);
            List<User> result = repository.search(query, User.class);
            if (consumer != null) {
                if (!result.isEmpty()) consumer.accept(result);
                else { consumer.accept(result); break; }
            }
            //Next page:
            start++;
        }
    }

    public long maxPageCount(JpaRepository repository, int pageSize) {
        long maxCount = repository.count();
        pageSize = (pageSize <= 0) ? 10 : pageSize;
        return (pageSize == maxCount) ? 1 : (maxCount / pageSize) + 1;
    }
}