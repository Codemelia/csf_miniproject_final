package csf.finalmp.app.server.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import csf.finalmp.app.server.models.ArtisteProfile;
import csf.finalmp.app.server.services.ArtisteProfileService;
// import csf.finalmp.app.server.services.ArtisteTransactionService;

// PURPOSE OF THIS CONTROLLER
// PROVIDE REST ENDPOINTS FOR ARTISTE REQUESTS FROM CLIENT

@CrossOrigin(originPatterns = "http://localhost:4200", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true")
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtisteController {

    @Autowired
    private ArtisteProfileService artisteProfSvc;

    // @Autowired
    // private ArtisteTransactionService artisteTransSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(ArtisteController.class.getName());

    // insert artiste profile to mongodb
    // return string feedback
    @PostMapping(path = "/artiste/create-profile/{artisteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createArtiste(
        @PathVariable("artisteId") String artisteId,
        @RequestPart("stageName") String stageName,
        @RequestPart(value = "categories", required = false) String categoriesString,
        @RequestPart(value = "bio", required = false) String bio,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @RequestPart(value = "thankYouMessage", required = false) String thankYouMessage) throws Exception {
        
        logger.info(">>> Creating artiste with ID: %s".formatted(artisteId));
        List<String> categories = categoriesString != null ? Arrays.asList(categoriesString.split(",")) : new ArrayList<>();
        artisteProfSvc.saveArtisteProfile(artisteId, stageName, categories, bio, photo, thankYouMessage);
        return ResponseEntity.status(HttpStatus.CREATED).body("Basic artiste profile saved successfully");

    }

    // check if artiste id exists in both mongo and mysql
    @GetMapping(path = "/artiste/check")
    public ResponseEntity<Boolean> checkArtisteId(
        @RequestParam("artisteId") String artisteId) {

        logger.info(">>> Checking artiste ID: %s".formatted(artisteId));
        boolean artisteExistsMongo = artisteProfSvc.checkArtisteId(artisteId);
        return ResponseEntity.ok(artisteExistsMongo);

    }

    // retrieve artiste profile
    @GetMapping(path = "/artiste/profile/{artisteId}")
    public ResponseEntity<ArtisteProfile> getArtisteProfile(
        @PathVariable String artisteId) {
        
        logger.info(">>> Getting profile of artiste ID: %s".formatted(artisteId));
        ArtisteProfile profile = artisteProfSvc.getArtisteProfileById(artisteId);
        return ResponseEntity.ok(profile);

    }

    // update artiste profile
    @PutMapping(path = "/artiste/update-profile/{artisteId}")
    public ResponseEntity<Boolean> updateArtisteProfile(
        @PathVariable("artisteId") String artisteId,
        @RequestPart("stageName") String stageName,
        @RequestPart(value = "categories", required = false) String categoriesString,
        @RequestPart(value = "bio", required = false) String bio,
        @RequestPart(value = "photo", required = false) MultipartFile photo,
        @RequestPart(value = "thankYouMessage", required = false) String thankYouMessage) throws IOException {

        logger.info(">>> Updating profile of artiste ID: %s".formatted(artisteId));
        List<String> categories = categoriesString != null ? Arrays.asList(categoriesString.split(",")) : new ArrayList<>();
        boolean updated = artisteProfSvc.updateArtisteProfile(artisteId, stageName, categories, bio, photo, thankYouMessage);
        return ResponseEntity.ok(updated);

    }

    // get all artiste profiles
    @GetMapping(path = "/artistes")
    public ResponseEntity<List<ArtisteProfile>> getAllArtisteProfiles() {

        logger.info(">>> Retrieving all artiste profiles");
        List<ArtisteProfile> artistes = artisteProfSvc.getAllArtisteProfiles();
        return ResponseEntity.ok(artistes);

    }

}
