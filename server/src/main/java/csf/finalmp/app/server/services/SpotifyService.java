package csf.finalmp.app.server.services;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import csf.finalmp.app.server.exceptions.custom.SpotifyException;
import csf.finalmp.app.server.models.ArtisteSpotifyDetails;
import csf.finalmp.app.server.models.helpers.SpotifyTrack;
import csf.finalmp.app.server.repositories.SpotifyRepository;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class SpotifyService {

    @Value("${spotify.client-id}")
    private String spotifyClientId;

    @Value("${spotify.client-secret}")
    private String spotifyClientSecret;

    @Value("${backend.app.url}")
    private String backendBaseUrl;

    @Autowired
    private SpotifyRepository spotifyRepo;

    @Autowired
    private ArtisteProfileService artisteProfSvc;

    private final RestTemplate template = new RestTemplate();

    private static final String SPOTIFY_OAUTH_BASE_URL = "https://accounts.spotify.com/authorize";
    private static final String SPOTIFY_OAUTH_TOKEN_URL = "https://accounts.spotify.com/api/token";
    private static final String SPOTIFY_GET_USERID_URL = "https://api.spotify.com/v1/me";
    private static final String SPOTIFY_SEARCH_TRACKS_URL = "https://api.spotify.com/v1/search";
    private static final String SPOTIFY_EMBED_BASE_URL = "https://open.spotify.com/embed/track";
    private static final String SPOTIFY_USERS_URL = "https://api.spotify.com/v1/users";
    private static final String SPOTIFY_PLAYLISTS_URL = "https://api.spotify.com/v1/playlists";

    private final Logger logger = Logger.getLogger(SpotifyService.class.getName());

    // gen oauth url
    public String genOAuthUrl(String artisteId) {

        return UriComponentsBuilder.fromUriString(SPOTIFY_OAUTH_BASE_URL)
            .queryParam("client_id", spotifyClientId)
            .queryParam("response_type", "code")
            .queryParam("redirect_uri", String.format("%s/api/spotify/oauth/callback", backendBaseUrl))
            .queryParam("scope", "playlist-modify-private user-read-private")
            .queryParam("state", artisteId) // send id as state for verification on callback
            .toUriString();
            
    }

    public void saveOAuthResponse(String code, String state) {

        String artisteId = String.valueOf(state);

        // exchange code for access token
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("code", code);
        params.add("redirect_uri", String.format("%s/api/spotify/oauth/callback", backendBaseUrl));
        params.add("client_id", spotifyClientId);
        params.add("client_secret", spotifyClientSecret);

        // set content type
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // send request
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = template.exchange(
            SPOTIFY_OAUTH_TOKEN_URL, 
            HttpMethod.POST, 
            entity,
            String.class
        );

        // handle errors
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.severe(">>> Spotify OAuth Error: %s".formatted(response.getStatusCode().toString()));
            throw new SpotifyException("Failed to connect your Spotify account. Please try again.");
        }

        // read response
        String responseString = response.getBody();
        JsonObject responseJson = Json.createReader(new StringReader(responseString))
            .readObject();

        // extract token details
        String accessToken = responseJson.getString("access_token");
        String refreshToken = responseJson.getString("refresh_token");
        int expiresIn = responseJson.getInt("expires_in");

        // retrieve spotify id
        String spotifyUserId = getSpotifyUserId(accessToken);

        // set initial details
        ArtisteSpotifyDetails details = new ArtisteSpotifyDetails();
        details.setArtisteId(artisteId);
        details.setSpotifyUserId(spotifyUserId);
        details.setAccessToken(accessToken);
        details.setRefreshToken(refreshToken);
        details.setAccessExpiresAt(LocalDateTime.ofInstant(Instant.now().plusSeconds(expiresIn), ZoneId.systemDefault()));

        // save to mongo db
        spotifyRepo.saveAccountDetails(details);

    }

    // get spotify user id
    public String getSpotifyUserId(String accessToken) {
        
        // set auth header
        HttpHeaders headers = setAuthHeaders(accessToken);

        // send request
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = template.exchange(
            SPOTIFY_GET_USERID_URL, 
            HttpMethod.GET, 
            entity,
            String.class
        );

        // handle errors
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.severe(">>> Spotify UID Error: %s".formatted(response.getStatusCode().toString()));
            throw new SpotifyException("Failed to connect your Spotify account. Please try again.");
        }

        String responseString = response.getBody();
        JsonObject responseJson = Json.createReader(new StringReader(responseString))
            .readObject();
        return responseJson.getString("id");

    }

    // save access token
    public void updateAccessToken(String artisteId, String accessToken) {
        spotifyRepo.updateAccessToken(artisteId, accessToken);
    }

    // check if artiste has linked spotify
    public boolean isSpotifyLinked(String artisteId) {
        return spotifyRepo.artisteExists(artisteId);
    }

    // search tracks
    public List<SpotifyTrack> searchTracks(String artisteId, String query) {

        String accessToken = checkAndRetrieveAccessToken(artisteId);

        // create headers to send search request to spotify
        HttpHeaders headers = setAuthHeaders(accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // create query url
        String queryUrl = UriComponentsBuilder.fromUriString(SPOTIFY_SEARCH_TRACKS_URL)
            .queryParam("q", UriUtils.encode(query, StandardCharsets.UTF_8)) // encode query string
            .queryParam("type", "track")
            .queryParam("limit", 5) // set limit to 5 (UI restrictions)
            .toUriString();

        ResponseEntity<String> response = template.exchange(
            queryUrl,
            HttpMethod.GET,
            entity,
            String.class);

        // handle errors
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.severe(">>> Spotify Search Tracks Error: %s".formatted(response.getStatusCode().toString()));
            throw new SpotifyException("Failed to connect your Spotify account. Please try again.");
        }

        // get track items from spotify response
        String responseString = response.getBody();
        JsonObject responseJson = Json.createReader(new StringReader(responseString))
            .readObject();
        
        // get tracks as json object and items as json array nested
        JsonObject tracksJson = responseJson.getJsonObject("tracks");

        // handle errors
        if (tracksJson == null || !tracksJson.containsKey("items")) {
            throw new SpotifyException("No tracks found for your search.");
        }

        JsonArray itemsArray = tracksJson.getJsonArray("items");
        
        // get id from each track and construct embed url
        List<SpotifyTrack> tracks = new ArrayList<>();
        for (JsonValue v : itemsArray) {
            JsonObject trackJson = v.asJsonObject();
            String trackId = trackJson.getString("id");
            SpotifyTrack track = new SpotifyTrack(
                trackId, 
                String.format("%s/%s", SPOTIFY_EMBED_BASE_URL, trackId));
            tracks.add(track);
        }

        return tracks;
    }

    // create spotify playlist with selected tracks and save playlist link
    public ArtisteSpotifyDetails savePlaylist(String artisteId, List<SpotifyTrack> selectedTracks) {
        
        String accessToken = checkAndRetrieveAccessToken(artisteId);
        String artisteStageName = artisteProfSvc.getArtisteStageNameById(artisteId);
        String spotifyUserId = getSpotifyUserId(accessToken);

        // create playlist and get playlist id
        String playlistId = createPlaylist(spotifyUserId, artisteStageName, accessToken);

        // add tracks to playlist
        String tracksRespString = addTracksToPlaylist(accessToken, playlistId, selectedTracks);

        // read response
        JsonObject tracksRespJson = Json.createReader(new StringReader(tracksRespString))
            .readObject();

        // handle errors
        if (tracksRespJson == null || !tracksRespJson.containsKey("snapshot_id")) {
            logger.severe(">>> Spotify Tracks Error: %s".formatted(tracksRespString));
            throw new SpotifyException("Failed to create your playlist. Please try again.");
        } 

        // if snapshot id exists, creation was successful
        // get playlist link and name
        ArtisteSpotifyDetails playlistDetails = getPlaylistDetails(artisteId, playlistId);
        playlistDetails.setPlaylistId(playlistId);

        // save to mongo
        spotifyRepo.updatePlaylistDetails(artisteId, playlistDetails);

        // return playlist link and name
        return playlistDetails;

    }

    // get playlist link from spotify after creating
    public ArtisteSpotifyDetails getPlaylistDetails(String artisteId, String playlistId) {

        String accessToken = checkAndRetrieveAccessToken(artisteId);
        
        // construct url
        String getPlaylistUrl = UriComponentsBuilder.fromUriString(SPOTIFY_PLAYLISTS_URL)
            .pathSegment(playlistId).toUriString();
    
        // headers
        HttpHeaders headers = setAuthHeaders(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
    
        // request and exchange
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = template.exchange(
            getPlaylistUrl,
            HttpMethod.GET,
            request, 
            String.class);

        // handle errors
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.severe(">>> Spotify Tracks Error: %s".formatted(response.getStatusCode().toString()));
            throw new SpotifyException("Failed to load your playlist. Please check your Spotify account.");
        }

        // get playlist link
        String respString = response.getBody();
        JsonObject respJson = Json.createReader(new StringReader(respString))
            .readObject();

        // get playlist name
        String playlistName = respJson.getString("name");
        
        // get external urls nested in json object and get playlistUrl
        JsonObject externalUrls = respJson.getJsonObject("external_urls");
        String playlistUrl = externalUrls.getString("spotify");

        // set to playlist details and return
        ArtisteSpotifyDetails playlistDetails = new ArtisteSpotifyDetails();
        playlistDetails.setPlaylistName(playlistName);
        playlistDetails.setPlaylistUrl(playlistUrl);
        return playlistDetails;

    }

    // add tracks to playlist, return resp json containing snapshot
    public String addTracksToPlaylist(String accessToken, String playlistId, List<SpotifyTrack> tracks) {

        // convert tracks to track uri
        List<String> trackUris = tracks.stream()
            .map(track -> "spotify:track:" + track.getTrackId())
            .collect(Collectors.toList());

        // construct url
        String addTracksUrl = UriComponentsBuilder.fromUriString(SPOTIFY_PLAYLISTS_URL)
            .pathSegment(playlistId, "tracks")
            .toUriString();

        // headers
        HttpHeaders headers = setAuthHeaders(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // request body
        Map<String, Object> tracksBody = new HashMap<>();
        tracksBody.put("uris", trackUris);

        // request
        HttpEntity<Map<String, Object>> tracksRequest = new HttpEntity<>(tracksBody, headers);

        // exchange and return snapshotid
        ResponseEntity<String> tracksResponse = template.exchange(
            addTracksUrl, 
            HttpMethod.POST,
            tracksRequest,
            String.class);

        // handle errors
        if (!tracksResponse.getStatusCode().is2xxSuccessful() || tracksResponse.getBody() == null) {
            logger.severe(">>> Spotify Tracks Error: %s".formatted(tracksResponse.getStatusCode().toString()));
            throw new SpotifyException("Failed to create your playlist. Please try again.");
        }

        // get snapshot ID from response
        String tracksRespString = tracksResponse.getBody();
        JsonObject tracksRespJson = Json.createReader(new StringReader(tracksRespString))
            .readObject();
        return tracksRespJson.toString();

    }

    // create playlist and retrieve playlistId
    public String createPlaylist(String spotifyUserId, String artisteStageName, String accessToken) {

        // create headers
        HttpHeaders headers = setAuthHeaders(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // create playlist description details
        Map<String, Object> playlistDesc = new HashMap<>();
        playlistDesc.put("name", "VIBEY: Vibe with %s".formatted(artisteStageName));
        playlistDesc.put("public", false); // make exclusive to those who have link only
        playlistDesc.put("description", "Exclusive playlist curated for you by %s!".formatted(artisteStageName));

        HttpEntity<Map<String, Object>> playlistRequest = new HttpEntity<>(playlistDesc, headers);

        // build request uri
        String createPlaylistUrl = UriComponentsBuilder.fromUriString(SPOTIFY_USERS_URL)
            .pathSegment(spotifyUserId, "playlists")
            .toUriString();

        ResponseEntity<String> descResponse = template.exchange(
            createPlaylistUrl, 
            HttpMethod.POST,
            playlistRequest,
            String.class);

        // handle errors
        if (!descResponse.getStatusCode().is2xxSuccessful() || descResponse.getBody() == null) {
            logger.severe(">>> Spotify Playlist Error: %s".formatted(descResponse.getStatusCode().toString()));
            throw new SpotifyException("Failed to create your playlist. Please try again.");
        }

        // get playlist ID from response
        String descRespString = descResponse.getBody();
        JsonObject descRespJson = Json.createReader(new StringReader(descRespString))
            .readObject();
        return descRespJson.getString("id");

    }

    // refresh access token
    public String refreshAccessToken(String artisteId, String refreshToken) {

        // encode auth header body
        String credentials = String.format("%s:%s", spotifyClientId, spotifyClientSecret);
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());

        // create body
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "refresh_token");
        body.add("refresh_token", refreshToken);

        // headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic " + encodedCredentials);

        // create request
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        // send request
        ResponseEntity<String> response = template.exchange(
            SPOTIFY_OAUTH_TOKEN_URL, 
            HttpMethod.POST, 
            entity, 
            String.class);

        // handle errors
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.severe(">>> Spotify Refresh Token Error: %s".formatted(response.getStatusCode().toString()));
            throw new SpotifyException("Failed to connect your Spotify account. Please try again.");
        }

        String responseString = response.getBody();
        JsonObject responseJson = Json.createReader(new StringReader(responseString))
            .readObject();

        // save and return access token
        String accessToken = responseJson.getString("access_token");
        spotifyRepo.updateAccessToken(artisteId, accessToken);
        return accessToken;

    }

    // check and retrieve access token
    public String checkAndRetrieveAccessToken(String artisteId) {
        // get access, refresh, and expiresAt
        Document tokenAndExpiryDoc = spotifyRepo.getTokensAndExpiry(artisteId);

        // get expiry date and handle date - set an expiry threshold of 5 mins
        String expiresAtString = tokenAndExpiryDoc.get("accessExpiresAt").toString();
        
        // dtf
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        
        // parse
        ZonedDateTime expiresAtZoned = ZonedDateTime.parse(expiresAtString, formatter);
        LocalDateTime expiresAt = expiresAtZoned.toLocalDateTime();
        
        // get current time and threshold
        LocalDateTime currentDateTime = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime refreshThreshold = expiresAt.minusMinutes(5);

        // if access token expired, refresh
        // if not, get access token
        String accessToken;
        if (currentDateTime.isAfter(refreshThreshold)) {
            logger.info(">>> Refreshing access token");
            String refreshToken = tokenAndExpiryDoc.getString("refreshToken");
            accessToken = refreshAccessToken(artisteId, refreshToken);
        } else {
            logger.info(">>> Retrieving access token");
            accessToken = tokenAndExpiryDoc.getString("accessToken");
        }

        return accessToken;
    }

    // set header
    public HttpHeaders setAuthHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }

    // get playlisturl from repo
    public String getPlaylistUrl(String artisteId) {
        return spotifyRepo.getPlaylistUrl(artisteId);
    }
 
}