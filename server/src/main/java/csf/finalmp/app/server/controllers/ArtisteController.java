package csf.finalmp.app.server.controllers;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.WriterException;
import com.stripe.exception.StripeException;

import csf.finalmp.app.server.models.Artiste;
import csf.finalmp.app.server.services.ArtisteService;

// PURPOSE OF THIS CONTROLLER
// PROVIDE REST ENDPOINTS FOR ARTISTE REQUESTS FROM CLIENT

@CrossOrigin(originPatterns = "http://localhost:4200", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE},
    allowCredentials = "true")
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class ArtisteController {

    @Autowired
    private ArtisteService artisteSvc;

    // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(ArtisteController.class.getName());

    // insert artiste to mysql db
    // return string feedback
    @PostMapping(path = "/artiste/create/{artisteId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createArtiste(
        @PathVariable("artisteId") String artisteId,
        @RequestPart("stageName") String stageName,
        @RequestPart(value = "bio", required = false) String bio,
        @RequestPart(value = "photo", required = false) MultipartFile photo) throws StripeException, WriterException, IOException {
        
        logger.info(">>> Creating artiste with ID: %s".formatted(artisteId));
        artisteSvc.insertArtiste(artisteId, stageName, bio, photo);
        return ResponseEntity.status(HttpStatus.CREATED).body("Basic artiste profile saved successfully");

    }

    // get artiste by id
    // return artiste obj
    @GetMapping(path = "/artiste/{id}")
    public ResponseEntity<Artiste> getArtisteById(@PathVariable String artisteId) {

        logger.info(">>> Fetching artiste with ID: %s".formatted(artisteId));
        Artiste artiste = artisteSvc.getArtisteById(artisteId);
        return ResponseEntity.ok().body(artiste);

    }

    // get all artistes
    @GetMapping(path = "/artistes")
    public ResponseEntity<List<Artiste>> getArtistes() {

        logger.info(">>> Fetching all artistes");
        List<Artiste> artistes = artisteSvc.getAllArtistes();
        return ResponseEntity.ok().body(artistes);

    }

    // check if artiste id exists in db
    @GetMapping(path = "/artiste-check")
    public ResponseEntity<Boolean> checkArtisteId(
        @RequestParam("artisteId") String artisteId
    ) {
        logger.info(">>> Checking artiste ID: %s".formatted(artisteId));
        boolean artisteExists = artisteSvc.checkArtisteId(artisteId);
        return ResponseEntity.ok().body(artisteExists);
    }

    // update artiste profile
    // return id if update op successful
    @PutMapping(path = "/artiste/{id}/update")
    public ResponseEntity<String> updateArtiste(
        @PathVariable String artisteId,
        @RequestPart("stageName") String name,
        @RequestPart("bio") String bio,
        @RequestPart("photo") MultipartFile photo,
        @RequestPart("thankYouMessage") String thankYouMessage) throws IOException {

        logger.info(">>> Updating artiste with ID: %s".formatted(artisteId));
        artisteSvc.updateArtisteProfile(artisteId, name, bio, photo, thankYouMessage); // throws exception if fail
        return ResponseEntity.ok().body("Vibee profile successfully updated!");

    }

    // delete artiste
    // returns id if delete op successful
    @DeleteMapping(path = "/artiste/{id}/delete")
    public ResponseEntity<String> deleteArtiste(@PathVariable String artisteId) {

        logger.info(">>> Deleting artiste with ID: %s".formatted(artisteId));
        String deleteId = artisteSvc.deleteArtiste(artisteId);
        return ResponseEntity.ok().body(deleteId);

    }

}
