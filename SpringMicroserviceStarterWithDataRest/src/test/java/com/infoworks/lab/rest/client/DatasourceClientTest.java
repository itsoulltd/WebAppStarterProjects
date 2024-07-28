package com.infoworks.lab.rest.client;

import com.infoworks.lab.client.data.rest.Links;
import com.infoworks.lab.client.data.rest.Page;
import com.infoworks.lab.client.data.rest.PaginatedResponse;
import com.infoworks.lab.client.spring.DataRestClient;
import com.infoworks.lab.domain.entities.User;
import com.infoworks.lab.rest.models.QueryParam;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class DatasourceClientTest {

    @Test
    public void doLoadTest() throws Exception {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data/users")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        //
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class
                , url
                , template);

        dataSource.setEnableLogging(true);
        PaginatedResponse response = dataSource.load();
        Assert.assertTrue(response != null);

        Page page = response.getPage();
        Assert.assertTrue(page != null);

        Links links = response.getLinks();
        Assert.assertTrue(links != null);
        //Close:
        dataSource.close();
    }

    @Test
    public void doAsyncLoadTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        //
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load((response) -> {
            //In-case of exception:
            if (response.getStatus() >= 400) {
                System.out.println(response.getError());
                latch.countDown();
            }
            //When success:
            Assert.assertTrue(response != null);

            Page page = response.getPage();
            Assert.assertTrue(page != null);

            Links links = response.getLinks();
            Assert.assertTrue(links != null);
            //
            latch.countDown();
        });

        latch.await();
        //Close:
        dataSource.close();
    }

    @Test
    public void addSingleItem() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        User newUser = new User();
        newUser.setName("Sohana Islam Khan");
        newUser.setAge(28);
        newUser.setSex("FEMALE");
        newUser.setActive(true);
        newUser.setDob(new Date(Instant.now().minus(28 * 365, ChronoUnit.DAYS).toEpochMilli()));
        //Create:
        Object id = dataSource.add(newUser);
        Assert.assertTrue(id != null);
        //Close:
        dataSource.close();
    }

    @Test
    public void updateSingleItem() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Object[] users = dataSource.readSync(0, dataSource.size());
        Assert.assertTrue(users.length > 0);
        //
        User user = (User) users[0];
        user.setName("Dr. Sohana Khan");
        //Update:
        Object id = user.parseId().orElse(null);
        if(id != null) dataSource.put(id, user);
        Assert.assertTrue(id != null);
        //Close:
        dataSource.close();
    }

    @Test
    public void readTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load();
        //
        User user = dataSource.read(1l);
        Assert.assertTrue(user != null);
        System.out.println(user.getName());
        //Close:
        dataSource.close();
    }

    @Test
    public void sizeTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load();
        //
        int size = dataSource.size();
        Assert.assertTrue(size >= 0);
        System.out.println("Size is: " + size);
        //Close:
        dataSource.close();
    }

    @Test
    public void readNextTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Optional<List<User>> users = dataSource.next();
        Assert.assertTrue(users.isPresent());
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //Close:
        dataSource.close();
    }

    @Test
    public void readAsyncNextTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        //
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        dataSource.next((users) -> {
            Assert.assertTrue(users.isPresent());
            latch.countDown();
        });
        latch.await();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //Close:
        dataSource.close();
    }

    @Test
    public void CRUDTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        User newUser = new User();
        newUser.setName("Sohana Islam Khan");
        newUser.setAge(28);
        newUser.setSex("FEMALE");
        newUser.setActive(true);
        newUser.setDob(new Date(Instant.now().minus(28 * 365, ChronoUnit.DAYS).toEpochMilli()));
        //Create:
        Object id = dataSource.add(newUser);
        Assert.assertTrue(id != null);
        //Read One:
        User read = dataSource.read(id);
        Assert.assertTrue(read != null);
        //Read from local:
        Object[] items = dataSource.readSync(0, dataSource.size());
        Stream.of(items).forEach(item -> {
            if (item instanceof User)
                System.out.println(((User) item).getName());
        });
        //Update:
        newUser.setName("Dr. Sohana Islam Khan");
        dataSource.put(id, newUser);
        //Read again: (will read from local)
        User readAgain = dataSource.read(id);
        System.out.println(readAgain.getName());
        //Delete:
        System.out.println("Count before delete: " + dataSource.size());
        dataSource.remove(id);
        System.out.println("Count after delete: " + dataSource.size());
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        dataSource.close();
    }

    @Test
    public void readAllPages() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        DataRestClient<User> dataSource = new DataRestClient(User.class, url);
        //Read All Pages Until last page:
        dataSource.load();
        Optional<List<User>> opt;
        do {
            opt = dataSource.next();
            System.out.println("Current Page: " + dataSource.currentPage());
            System.out.println("Local Size: " + dataSource.size());
        } while (opt.isPresent());
        //
        Object[] all = dataSource.readSync(0, dataSource.size());
        Stream.of(all).forEach(item -> {
            if (item instanceof User)
                System.out.println(((User) item).getName());
        });
        //Close:
        dataSource.close();
    }

    @Test
    public void whenCreatesEmptyOptional_thenCorrect() {
        Optional<String> empty = Optional.empty();
        Assert.assertFalse(empty.isPresent());
        //Available on Java-11:
        //Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void givenOptional_whenIsPresentWorks_thenCorrect() {
        Optional<String> opt = Optional.of("Baeldung");
        Assert.assertTrue(opt.isPresent());

        opt = Optional.ofNullable(null);
        Assert.assertFalse(opt.isPresent());
    }

    @Test
    public void searchFindByAgeLimitTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        SearchClient<User> dataSource = new SearchClient<>(User.class, url);
        dataSource.load();
        //
        Optional<List<User>> users = dataSource.search("findByAgeLimit"
                , new QueryParam("min", "18"), new QueryParam("max", "29"));
        Assert.assertTrue(users.isPresent());
        //Close:
        dataSource.close();
    }

    @Test
    public void searchFindByNameTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        SearchClient<User> dataSource = new SearchClient<>(User.class, url);
        dataSource.load();
        //
        Optional<List<User>> users = dataSource.search("/findByName", new QueryParam("name", "Soha"));
        Assert.assertTrue(users.isPresent());
        //Close:
        dataSource.close();
    }

    @Test
    public void searchFunctionIsExistTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/users");
        SearchClient<User> dataSource = new SearchClient<>(User.class, url);
        dataSource.load();
        //
        boolean isExist = dataSource.isSearchActionExist("findByName");
        Assert.assertTrue(isExist);
        //
        isExist = dataSource.isSearchActionExist("findByNameAndOthers");
        Assert.assertFalse(isExist);
        //Close:
        dataSource.close();
    }

    /////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////////////////
}
