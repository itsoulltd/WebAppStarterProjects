package com.infoworks.lab.rest.client;

import com.infoworks.lab.client.data.rest.Any;
import com.infoworks.lab.client.data.rest.Links;
import com.infoworks.lab.client.data.rest.Page;
import com.infoworks.lab.client.data.rest.PaginatedResponse;
import com.infoworks.lab.client.spring.DataRestClient;
import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.rest.models.QueryParam;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

public class DatasourceClientTest {

    @Test
    public void doLoadTest() throws Exception {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data/passengers")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class
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
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
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
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Passenger newPassenger = new Passenger();
        newPassenger.setName("Sohana Islam Khan");
        newPassenger.setAge(28);
        newPassenger.setSex("FEMALE");
        newPassenger.setActive(true);
        newPassenger.setDob(new Date(Instant.now().minus(28 * 365, ChronoUnit.DAYS).toEpochMilli()));
        //Create:
        Object id = dataSource.add(newPassenger);
        Assert.assertTrue(id != null);
        //Close:
        dataSource.close();
    }

    @Test
    public void updateSingleItem() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Object[] passengers = dataSource.readSync(0, dataSource.size());
        Assert.assertTrue(passengers.length > 0);
        //
        Passenger passenger = (Passenger) passengers[0];
        passenger.setName("Dr. Sohana Khan");
        //Update:
        Object id = passenger.parseId().orElse(null);
        if(id != null) dataSource.put(id, passenger);
        Assert.assertTrue(id != null);
        //Close:
        dataSource.close();
    }

    @Test
    public void readTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        Passenger passenger = dataSource.read(1l);
        Assert.assertTrue(passenger != null);
        System.out.println(passenger.getName());
        //Close:
        dataSource.close();
    }

    @Test
    public void sizeTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
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
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Optional<List<Passenger>> passengers = dataSource.next();
        Assert.assertTrue(passengers.isPresent());
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //Close:
        dataSource.close();
    }

    @Test
    public void readAsyncNextTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        dataSource.next((passengers) -> {
            Assert.assertTrue(passengers.isPresent());
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
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        Passenger newPassenger = new Passenger();
        newPassenger.setName("Sohana Islam Khan");
        newPassenger.setAge(28);
        newPassenger.setSex("FEMALE");
        newPassenger.setActive(true);
        newPassenger.setDob(new Date(Instant.now().minus(28 * 365, ChronoUnit.DAYS).toEpochMilli()));
        //Create:
        Object id = dataSource.add(newPassenger);
        Assert.assertTrue(id != null);
        //Read One:
        Passenger read = dataSource.read(id);
        Assert.assertTrue(read != null);
        //Read from local:
        Object[] items = dataSource.readSync(0, dataSource.size());
        Stream.of(items).forEach(item -> {
            if (item instanceof Passenger)
                System.out.println(((Passenger) item).getName());
        });
        //Update:
        newPassenger.setName("Dr. Sohana Islam Khan");
        dataSource.put(id, newPassenger);
        //Read again: (will read from local)
        Passenger readAgain = dataSource.read(id);
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
        URL url = new URL("http://localhost:8080/api/data/passengers");
        DataRestClient<Passenger> dataSource = new DataRestClient(Passenger.class, url);
        //Read All Pages Until last page:
        dataSource.load();
        Optional<List<Passenger>> opt;
        do {
            opt = dataSource.next();
            System.out.println("Current Page: " + dataSource.currentPage());
            System.out.println("Local Size: " + dataSource.size());
        } while (opt.isPresent());
        //
        Object[] all = dataSource.readSync(0, dataSource.size());
        Stream.of(all).forEach(item -> {
            if (item instanceof Passenger)
                System.out.println(((Passenger) item).getName());
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
        URL url = new URL("http://localhost:8080/api/data/passengers");
        SearchClient<Passenger> dataSource = new SearchClient<>(Passenger.class, url);
        dataSource.load();
        //
        Optional<List<Passenger>> passengers = dataSource.search("findByAgeLimit"
                , new QueryParam("min", "18"), new QueryParam("max", "29"));
        Assert.assertTrue(passengers.isPresent());
        //Close:
        dataSource.close();
    }

    @Test
    public void searchFindByNameTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        SearchClient<Passenger> dataSource = new SearchClient<>(Passenger.class, url);
        dataSource.load();
        //
        Optional<List<Passenger>> passengers = dataSource.search("/findByName", new QueryParam("name", "Soha"));
        Assert.assertTrue(passengers.isPresent());
        //Close:
        dataSource.close();
    }

    @Test
    public void searchFunctionIsExistTest() throws Exception {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        SearchClient<Passenger> dataSource = new SearchClient<>(Passenger.class, url);
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

    public static class Passenger extends Any<Long> {
        private String name;
        private String sex = Gender.NONE.name();
        private int age = 18;
        private Date dob = new java.sql.Date(new Date().getTime());
        private boolean active;

        public Passenger() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public Date getDob() {
            return dob;
        }

        public void setDob(Date dob) {
            this.dob = dob;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }

        @Override
        public void unmarshallingFromMap(Map<String, Object> data, boolean inherit) {
            Object dob = data.get("dob");
            if (dob != null) {
                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    Date parsed = formatter.parse(dob.toString());
                    data.put("dob", parsed);
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
            }
            super.unmarshallingFromMap(data, inherit);
        }
    }

    /////////////////////////////////////////////////////////////////////////////
}
