package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.cache.MemCache;
import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.repository.RestRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController implements RestRepository<Passenger, String> {

    private MemCache<Passenger> dataSource;

    public PassengerController(@Qualifier("passengerService") MemCache dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping("/rowCount")
    public ItemCount rowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(dataSource.size()).longValue());
        return count;
    }

    @GetMapping
    public List<Passenger> fetch(
            @RequestParam(value = "limit", defaultValue = "10", required = false) Integer limit
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page){
        //
        if (limit < 0) limit = 10;
        if (page < 0) page = 0;
        List<Passenger> passengers = Arrays.asList(dataSource.readSync(page, limit));
        return passengers;
    }

    @GetMapping("/findByKey")
    public Passenger read(@RequestParam("key") String key){
        Passenger passenger = dataSource.read(key);
        return passenger;
    }

    @PostMapping
    public Passenger insert(@Valid @RequestBody Passenger passenger){
        //
        dataSource.put(passenger.getName(), passenger);
        return passenger;
    }

    @PutMapping
    public Passenger update(@Valid @RequestBody Passenger passenger
            , @ApiIgnore @RequestParam(value = "name", required = false) String name){
        //
        dataSource.replace(passenger.getName(), passenger);
        return passenger;
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
    public Class<Passenger> getEntityType() {
        return Passenger.class;
    }

}
