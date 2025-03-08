package csf.finalmp.app.server.controllers;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;

import csf.finalmp.app.server.models.Tip;
import csf.finalmp.app.server.models.TipRequest;
import csf.finalmp.app.server.services.TipService;

// PURPOSE OF THIS CONTROLLER
// PROVIDE REST ENDPOINTS FOR TIP REQUESTS FROM CLIENT

@CrossOrigin(origins = "*", allowedHeaders = "*",
    methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RestController
@RequestMapping(path = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class TipController {

    @Autowired
    private TipService tipSvc;

        // logger to ensure proper tracking
    private Logger logger = Logger.getLogger(TipController.class.getName());

    // insert tip from client
    // returns tip object
    @PostMapping(path="/tips/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Tip> insertTip(
        @RequestBody TipRequest request) throws StripeException { // throw exception for global handler

        logger.info(">>> Processing tip request: %s".formatted(request.toString()));
        Tip tip = tipSvc.insertTip(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(tip);

    }

    // get tip by musician id
    // returns tip object
    @GetMapping(path = "tips/{musicianId}")
    public ResponseEntity<List<Tip>> getTipsByMusicianId(
        @PathVariable Long musicianId
    ) {
        
        logger.info(">>> Fetching tips for musician with ID: %d".formatted(musicianId));
        List<Tip> tips = tipSvc.getTipsByMusicianId(musicianId);
        return ResponseEntity.ok().body(tips);

    }


}
