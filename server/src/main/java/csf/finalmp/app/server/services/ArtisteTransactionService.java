package csf.finalmp.app.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.models.ArtisteTransactionDetails;
import csf.finalmp.app.server.repositories.ArtisteTransactionRepository;

import java.util.List;
import java.util.logging.Logger;

// FOR ARTISTE CRUD OPERATIONS

@Service
public class ArtisteTransactionService {

    @Autowired
    private ArtisteTransactionRepository artisteTransRepo;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(ArtisteTransactionService.class.getName());

    // check if table exists by checking count
    public boolean tableExists() {

        try {
            Integer count = artisteTransRepo.checkCount();
            logger.info(">>> MySQL: Artistes table SIZE: %d".formatted(count));
            return count != null && count >= 0;
        } catch (DataAccessException e) {
            logger.info(">>> MySQL: Artistes table does not exist");
            return false; // return false if data access exception occurs
        }

    }

    // create table
    public void createTable() {
        artisteTransRepo.createTable();
        logger.info(">>> MySQL: Artistes table created");
    }

    // update artiste earnings
    // returns true if > 0 rows updated
    public boolean updateArtisteEarnings(String artisteId, Double amount) {

        // get curr balance, add amount, save in artiste earnings
        Double balance = artisteTransRepo.getArtisteEarnings(artisteId);
        balance += amount;
        return artisteTransRepo.updateArtisteEarnings(artisteId, balance) > 0;
        
    }

    // get artiste by id
    public ArtisteTransactionDetails getArtisteById(String artisteId) {

        ArtisteTransactionDetails artiste = artisteTransRepo.getArtisteById(artisteId);
        logger.info(
            ">>> MySQL: Retrieved artiste with ID: %s".formatted(artiste.getArtisteId()));
        return artiste;

    }

    // get all artistes
    public List<ArtisteTransactionDetails> getAllArtistes() {

        List<ArtisteTransactionDetails> artistes = artisteTransRepo.getAllArtistes();
            logger.info(
                ">>> MySQL: Retrieved all artistes of SIZE %d".formatted(artistes.size()));
        return artistes;

    }
    
    // check if row for id exists in db
    // returns true if rows > 0, false if rows <= 0
    public boolean checkArtisteId(String artisteId) {
        return artisteTransRepo.checkArtisteId(artisteId) > 0;
    }

    // retrieve artiste curr balance
    public Double getArtisteEarnings(String artisteId) {
        return artisteTransRepo.getArtisteEarnings(artisteId);
    }

    // get artist stripe account id
    public String getStripeAccountId(String artisteId) {
        return artisteTransRepo.getStripeAccountId(artisteId); 
    }

    // check if artiste has valid access token
    public boolean checkArtisteStripeAccess(String artisteId) {
        return artisteTransRepo.checkArtisteStripeAccess(artisteId) > 0;
    }

}
