package com.infoworks.lab.rest.client;

import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.rest.client.datasource.*;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DatasourceClientTest {

    @Test
    public void doLoadTest() throws IOException {
        //
        RestTemplate template = new RestTemplateBuilder()
                .rootUri("http://localhost:8080/api/data/passengers")
                .setConnectTimeout(Duration.ofMillis(5000))
                .setReadTimeout(Duration.ofMillis(7000))
                .build();
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        RestDataSource<Long, Passenger> dataSource = new RestDataSource(Passenger.class
                , url
                , template);

        PaginatedResponse response = dataSource.load();
        Assert.assertTrue(response != null);

        Page page = response.getPage();
        Assert.assertTrue(page != null);

        Links links = response.getLinks();
        Assert.assertTrue(links != null);
    }

    @Test
    public void doAsyncLoadTest() throws IOException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        //
        URL url = new URL("http://localhost:8080/api/data/passengers");
        RestDataSource<Long, Passenger> dataSource = new RestDataSource(Passenger.class, url);
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
    }

    @Test
    public void readTest() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        RestDataSource<Long, Passenger> dataSource = new RestDataSource(Passenger.class, url);
        dataSource.load();
        //
        Passenger passenger = dataSource.read(1l);
        Assert.assertTrue(passenger != null);
        System.out.println(passenger.getName());
    }

    @Test
    public void sizeTest() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        RestDataSource<Long, Passenger> dataSource = new RestDataSource(Passenger.class, url);
        dataSource.load();
        //
        int size = dataSource.size();
        Assert.assertTrue(size >= 0);
        System.out.println("Size is: " + size);
    }

    @Test
    public void readNextTest() throws MalformedURLException {
        URL url = new URL("http://localhost:8080/api/data/passengers");
        RestDataSource<Long, Passenger> dataSource = new RestDataSource(Passenger.class, url);
        dataSource.load();
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
        //
        List<Passenger> passengers = dataSource.next();
        Assert.assertTrue(passengers != null);
        System.out.println(passengers.size());
        //
        System.out.println("Is last page: " + dataSource.isLastPage());
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
    }

    /////////////////////////////////////////////////////////////////////////////
}
