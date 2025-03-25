package csf.finalmp.app.server.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import csf.finalmp.app.server.exceptions.custom.UserAlreadyExistsException;
import csf.finalmp.app.server.exceptions.custom.UserAuthenticationException;
import csf.finalmp.app.server.models.ArtisteProfile;
import csf.finalmp.app.server.repositories.ArtisteProfileRepository;

@Service
public class ArtisteProfileService {

    @Autowired
    private ArtisteProfileRepository artisteProfRepo;

    @Autowired
    private UserService userSvc;

    @Value("${frontend.app.url}")
    private String frontendBaseUrl; // for qr code generation

    private Logger logger = Logger.getLogger(ArtisteProfileService.class.getName());

    // save artiste profile
    public void saveArtisteProfile(String artisteId, String artisteStageName, List<String> categories, 
        String bio, MultipartFile photo, String thankYouMessage) throws Exception {

        // validation
        String role = userSvc.getUserRoleById(artisteId);        
        if (!role.equals("ARTISTE")) {
            throw new UserAuthenticationException("Only Vibees can create Vibee profiles.");
        }

        boolean nameExists = checkArtisteStageName(artisteStageName);
        if (nameExists) {
            throw new UserAlreadyExistsException("Stage name already exists. Please choose another.");
        }

        ArtisteProfile artisteProfile = parseToProfile(artisteId, artisteStageName, categories, 
            bio, photo, thankYouMessage);

        // gen qr code url for tipping
        // http://localhost:4200/tip-form/{stagename}
        String qrCodeUrl = String.format("%s/tip-form/%s", frontendBaseUrl, 
            URLEncoder.encode(artisteStageName, StandardCharsets.UTF_8));
        QRCodeWriter qrWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrWriter.encode(qrCodeUrl, BarcodeFormat.QR_CODE, 200, 200);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        artisteProfile.setQrCodeUrl(qrCodeUrl); // set qr code to artiste obj
        artisteProfile.setQrCode(pngOutputStream.toByteArray()); // set qr code image to artiste obj 

        // insert updated artiste obj to db
        boolean inserted = artisteProfRepo.saveArtisteProfile(artisteProfile);
        logger.info(
            ">>> MongoDB: Artiste inserted?: %b".formatted(inserted));
        if (!inserted) { // if insertion outcome incorrect
            throw new Exception("An error occurred while creating your profile. Please try again.");
        } 

    }

    // helper method to parse variables to profile
    public ArtisteProfile parseToProfile(String artisteId, String artisteStageName, List<String> categories, 
        String bio, MultipartFile photo, String thankYouMessage) throws IOException {
    
        // set variables
        ArtisteProfile artisteProfile = new ArtisteProfile();
        artisteProfile.setArtisteId(artisteId);
        artisteProfile.setStageName(artisteStageName);

        if (categories != null && categories.size() > 0) artisteProfile.setCategories(categories);
        if (bio != null && !bio.isEmpty()) artisteProfile.setBio(bio);
        if (photo != null) artisteProfile.setPhoto(photo.getBytes());
        if (thankYouMessage != null) artisteProfile.setThankYouMessage(thankYouMessage);

        return artisteProfile;
        
    }

    // update artiste profile
    public boolean updateArtisteProfile(String artisteId, String stageName, List<String> categories, 
        String bio, MultipartFile photo, String thankYouMessage) throws IOException {

        // parse variables to profile
        ArtisteProfile profile = parseToProfile(artisteId, stageName, categories, 
            bio, photo, thankYouMessage);

        return artisteProfRepo.updateArtisteProfile(profile);
    }

    // check artiste id
    public boolean checkArtisteId(String artisteId) {
        return artisteProfRepo.checkArtisteId(artisteId);
    }

    // check artiste stage name
    public boolean checkArtisteStageName(String artisteStageName) {
        return artisteProfRepo.checkArtisteStageName(artisteStageName);
    }

    // get artiste id by stage name
    public String getArtisteIdByStageName(String artisteStageName) {
        return artisteProfRepo.getArtisteIdByStageName(artisteStageName);
    }

    // get thank you message by artiste id
    public String getArtisteThankYouMsgById(String artisteId) {
        return artisteProfRepo.getArtisteThankYouMsgById(artisteId);
    }

    // get artiste profile by id
    public ArtisteProfile getArtisteProfileById(String artisteId) {
        return artisteProfRepo.getArtisteProfileById(artisteId);
    }

    // get artiste stage name by id
    public String getArtisteStageNameById(String artisteId) {
        return artisteProfRepo.getArtisteStageNameById(artisteId);
    }

    public List<ArtisteProfile> getAllArtisteProfiles() {
        return artisteProfRepo.getAllArtisteProfiles();
    }
    
}
