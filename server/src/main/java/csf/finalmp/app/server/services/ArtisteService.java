package csf.finalmp.app.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.stripe.exception.StripeException;
import csf.finalmp.app.server.exceptions.custom.UserAlreadyExistsException;
import csf.finalmp.app.server.exceptions.custom.UserAuthenticationException;
import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.models.Artiste;
import csf.finalmp.app.server.repositories.ArtisteRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

// FOR ARTISTE CRUD OPERATIONS

@Service
public class ArtisteService {

    @Autowired
    private ArtisteRepository artisteRepo;

    @Autowired
    private UserService userSvc;

    @Value("${frontend.app.url}")
    private String frontendBaseUrl; // for qr code generation

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(ArtisteService.class.getName());

    // check if table exists by checking count
    public boolean tableExists() {

        try {
            Integer count = artisteRepo.checkCount();
            logger.info(">>> MySQL: Artistes table SIZE: %d".formatted(count));
            return count != null && count >= 0;
        } catch (DataAccessException e) {
            logger.info(">>> MySQL: Artistes table does not exist");
            return false; // return false if data access exception occurs
        }

    }

    // create table
    public void createTable() {
        artisteRepo.createTable();
        logger.info(">>> MySQL: Artistes table created");
    }

    // insert artiste into table and return feedback string
    @Transactional(rollbackFor = Exception.class)
    public void insertArtiste(String artisteId, String stageName, String bio, MultipartFile photo) throws StripeException, WriterException, IOException {

        String role = userSvc.getUserRoleById(artisteId);        
        if (!role.equals("ARTISTE")) {
            throw new UserAuthenticationException("Only Vibees can create Vibee profiles.");
        }

        // check if stage name exists in db
        boolean nameExists = artisteRepo.checkArtisteStageName(stageName) > 0;
        if (nameExists) {
            throw new UserAlreadyExistsException("Stage name already exists. Please choose another.");
        }

        boolean idExists = artisteRepo.checkArtisteId(artisteId) > 0;
        if (idExists) {
            throw new UserAlreadyExistsException("Vibee already exists. Please login to your Vibee account.");
        }

        // set variables
        Artiste artiste = new Artiste();
        artiste.setStageName(stageName);

        if (bio != null && !bio.isEmpty()) { artiste.setBio(bio); } 
        else { artiste.setBio(null); }

        if (photo != null) { artiste.setPhoto(photo.getBytes()); } 
        else { artiste.setPhoto(null); }

        artiste.setArtisteId(artisteId);

        // gen qr code url for tipping
        // http://localhost:4200/tip/{stagename}
        String qrCodeUrl = String.format("%s/tip-form/%s", frontendBaseUrl, 
            URLEncoder.encode(stageName, StandardCharsets.UTF_8));
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        artiste.setQrCodeUrl(qrCodeUrl); // set qr code to artiste obj
        artiste.setQrCode(pngOutputStream.toByteArray()); // set qr code image to artiste obj 

        // insert updated artiste obj to db
        boolean inserted = artisteRepo.insertArtiste(artiste) > 0;
        logger.info(
            ">>> MySQL: Artiste inserted?: %b".formatted(inserted));
        if (!inserted) { // if insertion outcome incorrect
            throw new UserAuthenticationException("An error occurred while creating your profile. Please log in again.");
        } 

    }

    // update artiste profile
    // returns true if rows updated > 0, false if rows updated <= 0
    public boolean updateArtisteProfile(String artisteId, String stageName, String bio, MultipartFile photo, String thankYouMessage) throws IOException {
        Artiste artiste = new Artiste();
        artiste.setArtisteId(artisteId);
        artiste.setStageName(stageName);
        artiste.setBio(bio);
        artiste.setThankYouMessage(thankYouMessage);
        if (photo != null) { artiste.setPhoto(photo.getBytes()); } 
        else { artiste.setPhoto(null); }
        return artisteRepo.updateArtisteProfile(artiste) > 0;
    }

    // update artiste wallet
    // returns true if > 0 rows updated
    public boolean updateArtisteWallet(String artisteId, Double amount) {

        // get curr balance, add amount, save in artiste wallet
        Double balance = artisteRepo.getArtisteBalance(artisteId);
        balance += amount;
        return artisteRepo.updateArtisteWallet(artisteId, balance) > 0;
        
    }

    // get artiste by id
    public Artiste getArtisteById(String artisteId) {

        Artiste artiste = artisteRepo.getArtisteById(artisteId);
        logger.info(
            ">>> MySQL: Retrieved artiste with ID: %s".formatted(artiste.getArtisteId()));
        return artiste;

    }

    // get all artistes
    public List<Artiste> getAllArtistes() {

        List<Artiste> artistes = artisteRepo.getAllArtistes();
            logger.info(
                ">>> MySQL: Retrieved all artistes of SIZE %d".formatted(artistes.size()));
        return artistes;

    }
    
    // check if row for id exists in db
    // returns true if rows > 0, false if rows <= 0
    public boolean checkArtisteId(String artisteId) {
        return artisteRepo.checkArtisteId(artisteId) > 0;
    }

    // retrieve artiste curr balance
    public Double getBalance(String artisteId) {
        return artisteRepo.getArtisteBalance(artisteId);
    }

    // get artist stripe account id
    public String getStripeAccountId(String artisteId) {
        return artisteRepo.getStripeAccountId(artisteId); 
    }

    // get artiste id by artiste stage name
    public String getArtisteIdByStageName(String artisteStageName) {
        return artisteRepo.getArtisteIdByStageName(artisteStageName);
    }

    // get artiste ty message by stage name
    public String getArtisteThankYouMessage(String artisteStageName) {
        return artisteRepo.getArtisteThankYouMessage(artisteStageName);
    }

    // delete artiste from db
    // returns id if update successful; throws error otherwise
    public String deleteArtiste(String artisteId) {

        int rows = artisteRepo.deleteArtiste(artisteId);
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
        return artisteRepo.checkArtisteStripeAccess(artisteId) > 0;
    }

    // check artiste stagename
    public boolean checkArtisteStageName(String artisteStageName) {
        return artisteRepo.checkArtisteStageName(artisteStageName) > 0;
    }

}
