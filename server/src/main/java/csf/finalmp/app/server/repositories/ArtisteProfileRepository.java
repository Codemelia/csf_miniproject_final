package csf.finalmp.app.server.repositories;

import java.time.LocalDateTime;
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

    private final String ARTISTE_PROFILES_CN = "artiste_profiles";

    // insert artiste profile data
    /*
        db.artiste_profiles.insert({
            _id: <artisteId>,
            stage_name: <stageName>,
            bio: <bio>,
            photo: BinData(0, <photo>),
            qr_code: BinData(0, <qrCode>),
            qr_code_url: <qrCodeUrl>,
            thank_you_message: <thankYouMessage>,
            created_at: <createdAt>,
            updated_at: <updatedAt>
        })
    */
    public boolean saveArtisteProfile(ArtisteProfile profile) {

        try {
        // convert artiste profile to document
        Document doc = new Document()
            .append("_id", profile.getArtisteId())
            .append("stage_name", profile.getStageName())
            .append("categories", profile.getCategories())
            .append("bio", profile.getBio())
            .append("photo", profile.getPhoto())
            .append("qr_code_url", profile.getQrCodeUrl())
            .append("thank_you_message", 
                profile.getThankYouMessage() != null ? profile.getThankYouMessage() 
                    : "Thank you for supporting our Vibees!")
            .append("created_at", LocalDateTime.now().toString())
            .append("updated_at", LocalDateTime.now().toString());

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
        db.artiste_profiles.updateOne(
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
            // match the artiste by  ID
            Query query = new Query(Criteria.where("_id")
                .is(profile.getArtisteId()));

            // update object
            Update update = new Update();

            // set the fields if provided
            if (categories != null && categories.size() > 0) {
                update.set("categories", categories);
            }
            if (bio != null) {
                update.set("bio", bio);
            }
            if (photoBytes != null && photoBytes.length >= 0) {
                update.set("photo", photoBytes);
            }
            if (thankYouMessage != null) {
                update.set("thank_you_message", thankYouMessage);
            }

            // perform the update and return true if successful
            template.updateFirst(query, update, ARTISTE_PROFILES_CN);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // check if artiste stage name alr exists in mongodb
    /*
        db.artiste_profiles.count({
            stage_name: <stageName>
        }) > 0
    */
    public boolean checkArtisteStageName(String artisteStageName) {
        
        // new query with criteria definition
        Criteria crit = Criteria.where("stage_name").is(artisteStageName);
        Query query = new Query(crit);

        // check if any doc match query and return boolean
        return template.exists(query, ARTISTE_PROFILES_CN);

    }

    // check if artiste id alr exists in mongodb
    /*
        db.artiste_profiles.count({
            _id: <artisteId>
        }) > 0
    */
    public boolean checkArtisteId(String artisteId) {
        
        // new query with criteria definition
        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);

        // check if any doc match query and return boolean
        return template.exists(query, ARTISTE_PROFILES_CN);

    }

    // get artiste id by stagename
    /*
        db.artiste_profiles.findOne(
            { stage_name: <artisteStageName> },
            { _id: 1 }
        )
    */
    public String getArtisteIdByStageName(String artisteStageName) {
        
        // new query with criteria definition
        Criteria crit = Criteria.where("stage_name").is(artisteStageName);
        Query query = new Query(crit);

        // include id only
        query.fields().include("_id");

        // find the document and return id as string
        Document result = template.findOne(query, Document.class, ARTISTE_PROFILES_CN);
        return result != null ? result.getObjectId("_id").toString() : null;

    }

    // get artiste ty msg by id
    /*
        db.artiste_profiles.findOne(
            { _id: <artisteId> },
            { thank_you_message: 1 }
        )
    */
    public String getArtisteThankYouMsgById(String artisteId) {
        
        // new query with criteria definition
        Criteria crit = Criteria.where("_id").is(artisteId);
        Query query = new Query(crit);

        // include id only
        query.fields().include("thank_you_message");

        // find the document and return id as string
        Document result = template.findOne(query, Document.class, ARTISTE_PROFILES_CN);
        return result != null ? result.getString("thank_you_message") 
            : "Thank you for supporting our Vibees!";

    }

}
