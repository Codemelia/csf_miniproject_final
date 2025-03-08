package csf.finalmp.app.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import csf.finalmp.app.server.exceptions.custom.MusicianNotFoundException;
import csf.finalmp.app.server.models.Musician;
import csf.finalmp.app.server.repositories.MusicianRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

// FOR MUSICIAN CRUD OPERATIONS

@Service
public class MusicianService {

    @Autowired
    private MusicianRepository musicRepo;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(MusicianService.class.getName());

    // check if table exists by checking count
    public boolean tableExists() {

        try {
            Integer count = musicRepo.checkCount();
            logger.info(">>> MySQL: Musicians table SIZE: %d".formatted(count));
            return count != null && count >= 0;
        } catch (DataAccessException e) {
            logger.info(">>> MySQL: Musicians table does not exist");
            return false; // return false if data access exception occurs
        }

    }

    // create table
    public void createTable() {

        musicRepo.createTable();
        logger.info(">>> MySQL: Musicians table created");

    }

    // insert musician into table and retrieve musician obj with id
    public Musician insertMusician(Musician musician) {

        Long id = musicRepo.insertMusician(musician);
        musician.setId(id); // set mysql id to musician obj
        logger.info(
            ">>> MySQL: New musician inserted with ID: %d".formatted(musician.getId())
        );
        return musician;

    }

    // get musician by id
    public Musician getMusicianById(Long id) {

        Musician retrMusician = musicRepo.getMusicianById(id);
        logger.info(
            ">>> MySQL: Retrieved musician with ID: %d".formatted(retrMusician.getId())
        );
        return retrMusician;

    }

    // get musicians based on location
    // if no location given, get all musicians
    public List<Musician> getMusicians(String location) {

        List<Musician> retrMusicians = new ArrayList<>();

        if (location == null || location.isBlank()) {
            retrMusicians = musicRepo.getAllMusicians();
            logger.info(
                ">>> MySQL: Retrieved all musicians of SIZE %d".formatted(retrMusicians.size())
            );
        } else {
            retrMusicians = musicRepo.getMusiciansByLocation(location);
            logger.info(
                ">>> MySQL: Retrieved musicians of SIZE %d from LOCATION %s"
                    .formatted(retrMusicians.size(), location)
            );
        }
                
        return retrMusicians;

    }
    
    // check if row for id exists in db
    // returns true if rows > 0, false if rows <= 0
    public boolean checkMusicianId(Long id) {

        return musicRepo.checkMusicianId(id) > 0;

    }

    // update musician data
    // returns id if update successful; throws error otherwise
    public Long updateMusician(Long id, Musician musician) {

        int rows = musicRepo.updateMusician(id, musician);
        if (rows > 0) {
            logger.info(
                ">>> MySQL: Successfully updated musician with ID: %d".formatted(id)
            );
            return id;
        } else {
            logger.severe(
                ">>> MySQL: Failed to update musician with ID: %d".formatted(id)
            );
            throw new MusicianNotFoundException(
                "Musician could not be found for update"
            );
        }

    }

    // delete musician from db
    // returns id if update successful; throws error otherwise
    public Long deleteMusician(Long id) {

        int rows = musicRepo.deleteMusician(id);
        if (rows > 0) {
            logger.info(
                ">>> MySQL: Successfully deleted musician with ID: %d".formatted(id)
            );
            return id;
        } else {
            logger.severe(
                ">>> MySQL: Failed to delete musician with ID: %d".formatted(id)
            );
            throw new MusicianNotFoundException(
                "Musician could not be found for deletion"
            );
        }

    }

    // shared method to conv musician obj to json
    // unused
    /*
        public JsonObject convMusicianToJson(Musician musician) {

        return Json.createObjectBuilder()
            .add("id", musician.getId())
            .add("name", musician.getName())
            .add("location", musician.getLocation())
            .build();

        }
    */

}
