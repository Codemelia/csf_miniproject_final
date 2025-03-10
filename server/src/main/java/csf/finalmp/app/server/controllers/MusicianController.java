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
import org.springframework.web.bind.annotation.RestController;

import csf.finalmp.app.server.models.MusicianProfile;
import csf.finalmp.app.server.services.MusicianProfileService;

// PURPOSE OF THIS CONTROLLER
// PROVIDE REST ENDPOINTS FOR MUSICIAN REQUESTS FROM CLIENT

@CrossOrigin(originPatterns = "http://localhost:4200", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true")
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class MusicianController {

    @Autowired
    private MusicianProfileService musicSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(MusicianController.class.getName());

    // insert musician to mysql db
    // return musician obj
    @PostMapping(path = "/musician/create", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<MusicianProfile> insertMusician(@RequestBody MusicianProfile inMusician) {

        logger.info(">>> Creating musician: %s".formatted(inMusician.toString()));
        MusicianProfile outMusician = musicSvc.saveMusician(inMusician);
        return ResponseEntity.status(HttpStatus.CREATED).body(outMusician);

    }

    // get musician by id
    // return musician obj
    @GetMapping(path = "/musician/{id}")
    public ResponseEntity<MusicianProfile> getMusicianById(@PathVariable Long id) {

        logger.info(">>> Fetching musician with ID: %d".formatted(id));
        MusicianProfile musician = musicSvc.getMusicianById(id);
        return ResponseEntity.ok().body(musician);

    }

    // get musicians by location
    @GetMapping(path = "/musicians")
    public ResponseEntity<List<MusicianProfile>> getMusicians() {

        logger.info(">>> Fetching all musicians");
        List<MusicianProfile> musicians = musicSvc.getAllMusicians();
        return ResponseEntity.ok().body(musicians);

    }

    // update musician
    // return id if update op successful
    @PutMapping(path = "/musician/{id}/update")
    public ResponseEntity<Long> updateMusician(
        @PathVariable Long id,
        @RequestBody MusicianProfile musician) {

        logger.info(">>> Updating musician with ID: %d".formatted(id));
        Long updateId = musicSvc.updateMusician(id, musician);
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
