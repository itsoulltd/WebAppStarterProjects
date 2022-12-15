package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.rest.repository.RestRepository;
import com.infoworks.lab.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController implements RestRepository<Passenger, String> {

    private PassengerService service;

    @Autowired
    public PassengerController(PassengerService service) {
        this.service = service;
    }

    @GetMapping("/rowCount")
    public ItemCount rowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(service.size()).longValue());
        return count;
    }

    @GetMapping
    public List<Passenger> fetch(
            @RequestParam(value = "limit", defaultValue = "10", required = false) Integer limit
            , @RequestParam(value = "page", defaultValue = "0", required = false) Integer page){
        //
        if (limit < 0) limit = 10;
        if (page < 0) page = 0;
        List<Passenger> passengers = Arrays.asList(service.readSync(page, limit));
        return passengers;
    }

    @PostMapping("/search")
    public List<Passenger> query(@RequestBody SearchQuery query){
        //
        List<Passenger> passengers = service.search(query);
        return passengers;
    }

    @PostMapping
    public Passenger insert(@Valid @RequestBody Passenger passenger){
        //
        service.put(passenger.getUuid(), passenger);
        return passenger;
    }

    @PutMapping
    public Passenger update(@Valid @RequestBody Passenger passenger
            , @ApiIgnore @RequestParam(value = "name", required = false) String name){
        //
        service.replace(passenger.getUuid(), passenger);
        return passenger;
    }

    @DeleteMapping
    public boolean delete(@RequestParam("uuid") String uuid){
        //
        Passenger deleted = service.remove(uuid);
        return deleted != null;
    }

    @Override
    public String getPrimaryKeyName() {
        return "uuid";
    }

    @Override
    public Class<Passenger> getEntityType() {
        return Passenger.class;
    }

}
