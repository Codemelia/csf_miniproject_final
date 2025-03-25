package csf.finalmp.app.server.repositories;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.MongoWriteException;

import csf.finalmp.app.server.models.ArtisteProfile;

// MONGO CRUD OPS FOR ARTISTE PROFILE DETAILS

@Repository
public class ArtisteProfileRepository {

    @Autowired
    private MongoTemplate template;

    private final String ARTISTE_PROFILES_CN = "artisteProfiles";

    // insert artiste profile data
    /*
        db.artisteProfiles.insert({
            _id: <artisteId>,
            stageName: <stageName>,
            bio: <bio>,
            photo: BinData(0, <photo>),
            qrCode: BinData(0, <qrCode>),
            qrCodeUrl: <qrCodeUrl>,
            thankYouMessage: <thankYouMessage>,
            createdAt: <createdAt>,
            updatedAt: <updatedAt>
        })
    */
    public boolean saveArtisteProfile(ArtisteProfile profile) {

        try {

            // convert artiste profile to document
            Document doc = new Document()
            .append("_id", profile.getArtisteId())
            .append("stageName", profile.getStageName())
            .append("categories", profile.getCategories() != null 
                ? profile.getCategories() : new ArrayList<>().add("Others")) // default to others
            .append("bio", profile.getBio() != null 
                ? profile.getBio() : "Hi, I am a Vibee!") // default bio
            .append("photo", profile.getPhoto())
            .append("qrCode", profile.getQrCode())
            .append("qrCodeUrl", profile.getQrCodeUrl())
            .append("thankYouMessage", profile.getThankYouMessage() != null 
                ? profile.getThankYouMessage() : "Thank you for supporting our Vibees!") // default message
            .append("createdAt", LocalDateTime.now().toString())
            .append("updatedAt", LocalDateTime.now().toString());

            // insert the document into collection - return true if success
            template.insert(doc, ARTISTE_PROFILES_CN);
            return true;

        } catch (MongoWriteException e) {
            return false;
        } catch (Exception e) {
            return false;
        }

    }

    // update artiste profile
    /*
        db.artisteProfiles.updateOne(
            { _id: <artisteId> },
            {
                $set: {
                    categories: <categories>,
                    bio: <bio>,
                    photo: <photo>,
                    thankYouMessage: <thankYouMessage>
                }
            }
        )
    */
    public boolean updateArtisteProfile(ArtisteProfile profile) {

        // get profile values
        List<String> categories = profile.getCategories();
        String bio = profile.getBio();
        byte[] photoBytes = profile.getPhoto();
        String thankYouMessage = profile.getThankYouMessage();

        try {

            Query query = new Query(Criteria.where("_id")
                .is(profile.getArtisteId()));
            Update update = new Update();

            // set the fields if provided
            if (categories != null && categories.size() > 0) update.set("categories", categories);
            if (bio != null && !bio.isBlank()) update.set("bio", bio);
            if (photoBytes != null && photoBytes.length >= 0) update.set("photo", photoBytes);
            if (thankYouMessage != null && !thankYouMessage.isBlank()) update.set("thankYouMessage", thankYouMessage);

            template.updateFirst(query, update, ARTISTE_PROFILES_CN);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // check if artiste stage name alr exists in mongodb
    /*
        db.artisteProfiles.count({
            stageName: <stageName>
        }) > 0
    */
    public boolean checkArtisteStageName(String artisteStageName) {
        
        Criteria crit = Criteria.where("stageName").is(artisteStageName);
        Query query = new Query(crit);
        return template.exists(query, ARTISTE_PROFILES_CN);

    }

    // check if artiste id alr exists in mongodb
    /*
        db.artisteProfiles.count({
            _id: <artisteId>
        }) > 0
    */
    public boolean checkArtisteId(String artisteId) {
        
        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        return template.exists(query, ARTISTE_PROFILES_CN);

    }

    // get artiste id by stagename
    /*
        db.artisteProfiles.findOne(
            { stageName: <artisteStageName> },
            { _id: 1 }
        )
    */
    public String getArtisteIdByStageName(String artisteStageName) {
        
        Criteria crit = Criteria.where("stageName").is(artisteStageName);
        Query query = new Query(crit);
        query.fields().include("_id");
        Document result = template.findOne(query, Document.class, ARTISTE_PROFILES_CN);
        return result != null ? result.getString("_id") : null;

    }

    // get artiste ty msg by id
    /*
        db.artisteProfiles.findOne(
            { _id: <artisteId> },
            { thankYouMessage: 1 }
        )
    */
    public String getArtisteThankYouMsgById(String artisteId) {
        
        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        query.fields().include("thankYouMessage");
        Document result = template.findOne(query, Document.class, ARTISTE_PROFILES_CN);
        return result != null ? result.getString("thankYouMessage") 
            : "Thank you for supporting our Vibees!";

    }

    // get artiste profile by id
    /*
        db.artisteProfiles.findOne(
            { _id: <artisteId> },
            {
                stageName: 1,
                categories: 1,
                bio: 1,
                photo: 1,
                qrCode: 1,
                qrCodeUrl: 1,
                thankYouMessage: 1
            }
        )
    */
    public ArtisteProfile getArtisteProfileById(String artisteId) {
        
        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        query.fields().include("stageName", "categories", "bio", "photo", "qrCode", "qrCodeUrl", "thankYouMessage");
        return template.findOne(query, ArtisteProfile.class, ARTISTE_PROFILES_CN);

    }

    // get artiste stage name by id
    /*
        db.artisteProfiles.findOne(
            { _id: <artisteId> },
            { stageName: 1 }
        )
    */
    public String getArtisteStageNameById(String artisteId) {
        
        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);
        query.fields().include("stageName");
        Document result = template.findOne(query, Document.class, ARTISTE_PROFILES_CN);
        return result.getString("stageName");

    }

    // get all artiste profiles
    /*
        db.artisteProfiles.find()
    */
    public List<ArtisteProfile> getAllArtisteProfiles() {
        return template.findAll(ArtisteProfile.class, ARTISTE_PROFILES_CN);
    }

}
