package com.infoworks.lab.repositories;

import com.infoworks.lab.domain.entities.Gender;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.domain.repositories.PassengerRepository;
import com.infoworks.lab.webapp.config.TestJPAConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestJPAConfig.class})
@Transactional
@TestPropertySource(locations = {"classpath:h2-db.properties"})
public class PassengerRepositoryTest {

    @Autowired
    PassengerRepository repository;

    @Test
    public void insertPassenger(){
        Passenger passenger = new Passenger("Sayed The Coder", Gender.MALE, 24);
        repository.save(passenger);

        List<Passenger> list = repository.findByName("Sayed The Coder");
        Assert.assertTrue(Objects.nonNull(list));

        if (list != null && list.size() > 0){
            Passenger passenger2 = list.get(0);
            Assert.assertTrue(Objects.equals(passenger.getName(), passenger2.getName()));
        }
    }

    @Test
    public void updatePassenger(){
        //TODO
    }

    @Test
    public void deletePassenger(){
        //TODO
    }

    @Test
    public void countPassenger(){
        //
        Passenger passenger = new Passenger("Sayed The Coder", Gender.MALE, 24);
        repository.save(passenger);

        long count = repository.count();
        Assert.assertTrue(count == 1);
    }

    @Test
    public void fetchPassenger(){
        //
        repository.save(new Passenger("Sayed The Coder", Gender.MALE, 24));
        repository.save(new Passenger("Evan The Pankha Coder", Gender.MALE, 24));
        repository.save(new Passenger("Razib The Pagla", Gender.MALE, 26));
        //
        Page<Passenger> paged = repository.findAll(PageRequest.of(0
                , 10
                , Sort.by(Sort.Order.asc("name"))));
        paged.get().forEach(passenger -> System.out.println(passenger.getName()));
    }
}