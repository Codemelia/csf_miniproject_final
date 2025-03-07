package csf.finalmp.app.server.controllers;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import csf.finalmp.app.server.models.Musician;
import csf.finalmp.app.server.services.MusicianService;

// PURPOSE OF THIS CONTROLLER
// PROVIDE REST ENDPOINTS FOR MUSICIAN REQUESTS FROM CLIENT

@CrossOrigin(origins = "*", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MusicianController {

    @Autowired
    private MusicianService musicSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(MusicianController.class.getName());

    // insert musician to mysql db
    // return musician obj
    @PostMapping(path = "/musician/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Musician> insertMusician(@RequestBody Musician inMusician) {

        logger.info(">>> Creating musician: %s".formatted(inMusician.toString()));
        Musician outMusician = musicSvc.insertMusician(inMusician);
        return ResponseEntity.status(HttpStatus.CREATED).body(outMusician);

    }

    // get musician by id
    // return musician obj
    @GetMapping(path = "/musician/{id}")
    public ResponseEntity<Musician> getMusicianById(@PathVariable Long id) {

        logger.info(">>> Fetching musician with ID: %d".formatted(id));
        Musician musician = musicSvc.getMusicianById(id);
        return ResponseEntity.ok().body(musician);

    }

    // get musicians by location
    // if no location given, get all musicians
    // return musician obj
    @GetMapping(path = "/musicians")
    public ResponseEntity<List<Musician>> getMusicians(
        @RequestParam(required = false) String location) {

        logger.info(">>> Fetching all musicians at LOCATION: %s".formatted(location));
        List<Musician> musicians = musicSvc.getMusicians(location);
        return ResponseEntity.ok().body(musicians);

    }

    // update musician
    // return id if update op successful
    @PutMapping(path = "/musician/{id}/update")
    public ResponseEntity<Long> updateMusician(
        @PathVariable Long id,
        @RequestBody Musician inMusician) {

        logger.info(">>> Updating musician with ID: %d".formatted(id));
        Long updateId = musicSvc.updateMusician(id, inMusician);
        return ResponseEntity.ok().body(updateId);

    }

    // delete musician
    // returns id if delete op successful
    @DeleteMapping(path = "/musician/{id}/delete")
    public ResponseEntity<Long> deleteMusician(@PathVariable Long id) {

        logger.info(">>> Deleting musician with ID: %d".formatted(id));
        Long deleteId = musicSvc.deleteMusician(id);
        return ResponseEntity.ok().body(deleteId);

    }
    
}
