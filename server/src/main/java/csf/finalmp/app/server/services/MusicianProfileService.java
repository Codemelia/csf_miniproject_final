package csf.finalmp.app.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import csf.finalmp.app.server.exceptions.custom.MusicianNotFoundException;
import csf.finalmp.app.server.models.MusicianProfile;
import csf.finalmp.app.server.repositories.MusicianProfileRepository;

import java.util.List;
import java.util.logging.Logger;

// FOR MUSICIAN CRUD OPERATIONS

@Service
public class MusicianProfileService {

    @Autowired
    private MusicianProfileRepository musicRepo;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(MusicianProfileService.class.getName());

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
    public MusicianProfile saveMusician(MusicianProfile musician) {

        Long id = musicRepo.saveMusician(musician);
        musician.setId(id); // set mysql id to musician obj
        logger.info(
            ">>> MySQL: New musician inserted with ID: %d".formatted(musician.getId()));
        return musician;

    }

    // get musician by id
    public MusicianProfile getMusicianById(Long id) {

        MusicianProfile retrMusician = musicRepo.getMusicianById(id);
        logger.info(
            ">>> MySQL: Retrieved musician with ID: %d".formatted(retrMusician.getId()));
        return retrMusician;

    }

    // get all musicians
    public List<MusicianProfile> getAllMusicians() {

        List<MusicianProfile> retrMusicians = musicRepo.getAllMusicians();
            logger.info(
                ">>> MySQL: Retrieved all musicians of SIZE %d".formatted(retrMusicians.size()));
                
        return retrMusicians;

    }
    
    // check if row for id exists in db
    // returns true if rows > 0, false if rows <= 0
    public boolean checkMusicianId(Long id) {

        return musicRepo.checkMusicianId(id) > 0;

    }

    // update musician data
    // returns id if update successful; throws error otherwise
    public Long updateMusician(Long id, MusicianProfile musician) {

        int rows = musicRepo.updateMusician(id, musician);
        if (rows > 0) {
            logger.info(
                ">>> MySQL: Successfully updated musician with ID: %d".formatted(id));
            return id;
        } else {
            logger.severe(
                ">>> MySQL: Failed to update musician with ID: %d".formatted(id));
            throw new MusicianNotFoundException(
                "Vibee does not exist. Please ensure you have the correct Vibee ID.");
        }

    }

    // delete musician from db
    // returns id if update successful; throws error otherwise
    public Long deleteMusician(Long id) {

        int rows = musicRepo.deleteMusician(id);
        if (rows > 0) {
            logger.info(
                ">>> MySQL: Successfully deleted musician with ID: %d".formatted(id));
            return id;
        } else {
            logger.severe(
                ">>> MySQL: Failed to delete musician with ID: %d".formatted(id));
            throw new MusicianNotFoundException(
                "Vibee does not exist. Please ensure you have the correct Vibee ID.");
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
