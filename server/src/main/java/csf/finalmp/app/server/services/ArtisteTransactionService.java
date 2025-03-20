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


    // update artiste wallet
    // returns true if > 0 rows updated
    public boolean updateArtisteWallet(String artisteId, Double amount) {

        // get curr balance, add amount, save in artiste wallet
        Double balance = artisteTransRepo.getArtisteBalance(artisteId);
        balance += amount;
        return artisteTransRepo.updateArtisteWallet(artisteId, balance) > 0;
        
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
    public Double getBalance(String artisteId) {
        return artisteTransRepo.getArtisteBalance(artisteId);
    }

    // get artist stripe account id
    public String getStripeAccountId(String artisteId) {
        return artisteTransRepo.getStripeAccountId(artisteId); 
    }

    // delete artiste from db
    // returns id if update successful; throws error otherwise
    public String deleteArtiste(String artisteId) {

        int rows = artisteTransRepo.deleteArtiste(artisteId);
        if (rows > 0) {
            logger.info(
                ">>> MySQL: Successfully deleted artiste with ID: %s".formatted(artisteId));
            return artisteId;
        } else {
            logger.severe(
                ">>> MySQL: Failed to delete artiste with ID: %s".formatted(artisteId));
            throw new UserNotFoundException(
                "Vibee does not exist. Please ensure you have the correct Vibee ID.");
        }

    }

    // check if artiste has valid access token
    public boolean checkArtisteStripeAccess(String artisteId) {
        return artisteTransRepo.checkArtisteStripeAccess(artisteId) > 0;
    }

}
