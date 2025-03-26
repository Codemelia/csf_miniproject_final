package csf.finalmp.app.server.controllers;

import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.models.ArtisteSpotifyDetails;
import csf.finalmp.app.server.models.helpers.SpotifyTrack;
import csf.finalmp.app.server.services.ArtisteProfileService;
import csf.finalmp.app.server.services.SpotifyService;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/spotify", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpotifyController {

    @Value("${frontend.app.url}")
    private String frontendBaseUrl; // for redirect after oauth callback

    @Autowired
    private SpotifyService spotifySvc;

    @Autowired
    private ArtisteProfileService artisteProfSvc;

    private Logger logger = Logger.getLogger(SpotifyController.class.getName());

    // gen oauth url
    @GetMapping("/gen-oauth/{artisteId}")
    public ResponseEntity<String> genOAuthUrl(@PathVariable String artisteId) {

        logger.info(">>> Generating Spotify OAuth URL for artiste ID: %s".formatted(artisteId));
        if (!artisteProfSvc.checkArtisteId(artisteId)) {
            throw new UserNotFoundException("Vibee could not be found.");
        }
        String oAuthUrl = spotifySvc.genOAuthUrl(artisteId);
        System.out.printf(">>> O Auth URL generated: %s", oAuthUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(oAuthUrl);

    }

    // handle oauth callback
    @GetMapping("/oauth/callback")
    public void handleOAuthCallback(
        @RequestParam(required = false) String code,
        @RequestParam(required = false) String state,
        @RequestParam(required = false) String error,
        HttpServletResponse response) throws IOException {

        // handle error
        if (error != null || code == null || state == null) {
            logger.warning(">>> Spotify error occurred");
            response.sendRedirect("%s/dashboard/profile-edit".formatted(frontendBaseUrl));
            return;
        }

        logger.info(">>> Callback from Spotify OAuth received: %s | %s".formatted(code, state));
        spotifySvc.saveOAuthResponse(code, state);
        response.sendRedirect("%s/dashboard/profile-edit".formatted(frontendBaseUrl));
    }

    // check if artiste spotify link is saved in mongo
    @GetMapping("/linked")
    public ResponseEntity<Boolean> isSpotifyLinked(
        @RequestParam("artisteId") String artisteId) {

        logger.info(">>> Checking Spotify link for artiste ID: %s".formatted(artisteId));    
        boolean linked = spotifySvc.isSpotifyLinked(artisteId);
        return ResponseEntity.ok(linked);

    }

    // search spotify for top 5 tracks
    @GetMapping("/search/{artisteId}")
    public ResponseEntity<List<SpotifyTrack>> searchTracks(
        @PathVariable String artisteId,
        @RequestParam("query") String query) {

        logger.info(">>> Searching Spotify for artiste ID: %s".formatted(artisteId));    
        List<SpotifyTrack> tracks = spotifySvc.searchTracks(artisteId, query);
        return ResponseEntity.ok(tracks);

    }

    // save playlist and return playlist name and url
    @PutMapping("/save-playlist/{artisteId}")
    public ResponseEntity<ArtisteSpotifyDetails> savePlaylist(
        @PathVariable String artisteId,
        @RequestBody List<SpotifyTrack> selectedTracks) {

        logger.info(">>> Creating Spotify playlist for artiste ID: %s".formatted(artisteId));    
        ArtisteSpotifyDetails playlistDetails = spotifySvc.savePlaylist(artisteId, selectedTracks);
        return ResponseEntity.ok(playlistDetails);
    }

    // get playlist url from mongo
    @GetMapping("/get-playlist/{artisteId}")
    public ResponseEntity<String> getPlaylist(
        @PathVariable String artisteId) {

        logger.info(">>> Getting Spotify playlist for artiste ID: %s".formatted(artisteId));    
        String playlistUrl = spotifySvc.getPlaylistUrl(artisteId);
        return ResponseEntity.ok(playlistUrl);

    }

}