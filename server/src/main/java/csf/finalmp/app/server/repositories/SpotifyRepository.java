package csf.finalmp.app.server.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoWriteException;

import csf.finalmp.app.server.models.ArtisteSpotifyDetails;

@Repository
public class SpotifyRepository {

    @Autowired
    private MongoTemplate template;

    private static final String SPOTIFY_DETAILS_CN = "spotifyDetails";

    // save acct details from oauth respnse
    /*
        db.spotifyDetails.insert({
            _id: <artisteId>,
            spotifyUserId: <spotifyUserId>,
            accessToken: <accessToken>,
            refreshToken: <refreshToken>,
            accessExpiresAt: <accessExpiresAt>
        })
    */
    public boolean saveAccountDetails(ArtisteSpotifyDetails details) {
        
        try {

            Document doc = new Document();
            doc.append("_id", details.getArtisteId());
            doc.append("spotifyUserId", details.getSpotifyUserId());
            doc.append("accessToken", details.getAccessToken());
            doc.append("refreshToken", details.getRefreshToken());
            doc.append("accessExpiresAt", details.getAccessExpiresAt());

            template.insert(doc, SPOTIFY_DETAILS_CN);
            return true;

        } catch (MongoWriteException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    // update new access token on refresh
    /*
        db.spotifyDetails.updateOne(
            { _id: <artisteId },
            { $set: { accessToken: <accessToken> } }
        )
    */
    public void updateAccessToken(String artisteId, String accessToken) {

        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        Update update = new Update();
        update.set("accessToken", accessToken);
        template.updateFirst(query, update, SPOTIFY_DETAILS_CN);

    }

    // check if artiste has linked spotify
    /*
        db.spotifyDetails.count({
            _id: <artisteId>
        }) > 0
    */
    public boolean artisteExists(String artisteId) {

        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        return template.exists(query, SPOTIFY_DETAILS_CN);

    } 

    // retrieve access token
    /*
        db.spotifyDetails.findOne(
            { _id: <artisteId> },
            { 
                accessToken: 1,
                accessExpiresAt: 1
            }
        ) 
    */
    public Document getTokensAndExpiry(String artisteId) {

        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        query.fields().include("accessToken", "refreshToken", "accessExpiresAt");
        return template.findOne(query, Document.class, SPOTIFY_DETAILS_CN);

    }

    // update playlist (updates everytime so no need to check anyth)
    /*
        db.spotifyDetails.updateOne(
            { _id: <artisteId },
            { $set: { 
                playlistName: <playlistName>
                playlistUrl: <playlistUrl>
                playlistId: <playlistId>
              } 
            }
        )
    */
    public void updatePlaylistDetails(String artisteId, ArtisteSpotifyDetails playlistDetails) {

        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        Update update = new Update();
        update.set("playlistName", playlistDetails.getPlaylistName());
        update.set("playlistUrl", playlistDetails.getPlaylistUrl());
        update.set("playlistId", playlistDetails.getPlaylistId());
        template.updateFirst(query, update, SPOTIFY_DETAILS_CN);

    }

    // get playlist url
    /*
        db.spotifyDetails.findOne(
            { _id: <artisteId },
            { playlistUrl: 1 }
        )
    */
    public String getPlaylistUrl(String artisteId) {
        
        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        query.fields().include("playlistUrl");
        return template.findOne(query, Document.class, SPOTIFY_DETAILS_CN)
            .getString("playlistUrl");

    }
    
}
