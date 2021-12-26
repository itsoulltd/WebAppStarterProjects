package com.infoworks.lab.controllers.rest;

import com.infoworks.lab.domain.entities.Passenger;
import com.infoworks.lab.rest.models.ItemCount;
import com.infoworks.lab.rest.models.SearchQuery;
import com.infoworks.lab.services.PassengerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/passenger")
public class PassengerController {

    private PassengerService service;

    @Autowired
    public PassengerController(PassengerService service) {
        this.service = service;
    }

    @GetMapping("/rowCount")
    public ItemCount getRowCount(){
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(service.size()).longValue());
        return count;
    }

    @GetMapping
    public List<Passenger> query(@RequestParam("limit") Integer limit
            , @RequestParam("offset") Integer offset){
        //TODO: Test with RestExecutor
        List<Passenger> passengers = Arrays.asList(service.readSync(offset, limit));
        return passengers;
    }

    @PostMapping("/search")
    public List<Passenger> query(@RequestBody SearchQuery query){
        //TODO: Test with RestExecutor
        List<Passenger> passengers = service.search(query);
        return passengers;
    }

    @PostMapping @SuppressWarnings("Duplicates")
    public ItemCount insert(@Valid @RequestBody Passenger passenger){
        //TODO: Test with RestExecutor
        service.put(passenger.getUuid(), passenger);
        ItemCount count = new ItemCount();
        count.setCount(Integer.valueOf(service.size()).longValue());
        return count;
    }

    @PutMapping @SuppressWarnings("Duplicates")
    public ItemCount update(@Valid @RequestBody Passenger passenger){
        //TODO: Test with RestExecutor
        Passenger old = service.replace(passenger.getUuid(), passenger);
        ItemCount count = new ItemCount();
        if (old != null)
            count.setCount(Integer.valueOf(service.size()).longValue());
        return count;
    }

    @DeleteMapping
    public Boolean delete(@RequestParam("uuid") String uuid){
        //TODO: Test with RestExecutor
        Passenger deleted = service.remove(uuid);
        return deleted != null;
    }

}
