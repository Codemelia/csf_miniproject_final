package csf.finalmp.app.server.repositories;

import java.util.List;

import static csf.finalmp.app.server.utils.ArtisteSql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.models.Artiste;

// PURPOSE OF THIS REPO: DB CRUD OPS FOR ARTISTES

@Repository
public class ArtisteRepository {
    
    @Autowired
    private JdbcTemplate template;

    // check table count in mysql
    public Integer checkCount() {
        return template.queryForObject(
            CHECK_ARTISTES_TABLE_COUNT, 
            Integer.class
        );
    }

    // create table
    public void createTable() {
        template.execute(CREATE_ARTISTES_TABLE);
    }

    // insert artiste into table and return rows inserted
    public int insertArtiste(Artiste artiste) {

        return template.update(INSERT_ARTISTE, 
            artiste.getArtisteId(),
            artiste.getStageName(),
            artiste.getBio(),
            artiste.getPhoto(),
            artiste.getQrCode(),
            artiste.getQrCodeUrl());
        
    }

    // update artiste stripe details
    public int updateArtisteStripe(Artiste artiste) {
        
        return template.update(
            UPDATE_ARTISTE_STRIPE,
            artiste.getStripeAccountId(),
            artiste.getStripeAccessToken(),
            artiste.getStripeRefreshToken(),
            artiste.getArtisteId());

    }

    // update artiste and return rows updated
    public int updateArtisteProfile(Artiste artiste) {

        return template.update(
            UPDATE_ARTISTE_PROFILE,
            artiste.getStageName(),
            artiste.getBio(),
            artiste.getPhoto(),
            artiste.getThankYouMessage(),
            artiste.getArtisteId());

    }

    // update artiste wallet
    // return int of rows affected
    public int updateArtisteWallet(String artisteId, double balance) {

        return template.update(
            UPDATE_WALLET, 
            balance, 
            artisteId);

    }

    // get artiste by id
    // on error, throw custom error
    public Artiste getArtisteById(String artisteId) {

        try {
            return template.queryForObject(
                SELECT_ARTISTE_BY_ID, 
                (rs, rowNum) -> new Artiste(
                    rs.getString("artiste_id"), 
                    rs.getString("stage_name"), 
                    rs.getString("bio"), 
                    rs.getBytes("photo"), 
                    rs.getBytes("qr_code"),
                    rs.getString("qr_code_url"),
                    rs.getString("stripe_account_id"), 
                    rs.getString("stripe_access_token"),
                    rs.getString("stripe_refresh_token"),
                    rs.getDouble("wallet_balance"),
                    rs.getString("thank_you_message"),
                    rs.getTimestamp("created_at").toLocalDateTime(), 
                    rs.getTimestamp("updated_at").toLocalDateTime()),
                artisteId);
        } catch (Exception e) {
            throw new UserNotFoundException(
                "Vibee could not be found: %s".formatted(e.getMessage()));
        }

    }

    // get all artistes in db
    public List<Artiste> getAllArtistes() {

        return template.query(
            SELECT_ALL_ARTISTES, 
            (rs, rowNum) -> new Artiste(
                rs.getString("artiste_id"), 
                rs.getString("stage_name"), 
                rs.getString("bio"), 
                rs.getBytes("photo"), 
                rs.getBytes("qr_code"),
                rs.getString("qr_code_url"),
                rs.getString("stripe_account_id"),
                rs.getString("stripe_access_token"),
                rs.getString("stripe_refresh_token"),
                rs.getDouble("wallet_balance"), 
                rs.getString("thank_you_message"),
                rs.getTimestamp("created_at").toLocalDateTime(), 
                rs.getTimestamp("updated_at").toLocalDateTime()));

    }

    // check existing rows for id in db
    public Integer checkArtisteId(String artisteId) {

        return template.queryForObject(CHECK_ARTISTE_ID,
        Integer.class,
        artisteId);

    }

    // check existing stagename in db
    public Integer checkArtisteStageName(String stageName) {

        return template.queryForObject(
            CHECK_ARTISTE_STAGE_NAME,
            Integer.class,
            stageName);

    }

    // get artiste curr balance
    public Double getArtisteBalance(String artisteId) {

        return template.queryForObject(
            SELECT_ARTISTE_BALANCE_BY_ID, 
            Double.class,
            artisteId);

    }
    
    // get stripe account id by artiste id
    public String getStripeAccountId(String artisteId) {
        return template.queryForObject(
            SELECT_ARTISTE_STRIPE_ACCOUNT_ID_BY_ID, 
            String.class,
            artisteId);
    }

    // get artiste id by stage name
    public String getArtisteIdByStageName(String artisteStageName) {
        return template.queryForObject(
            SELECT_ARTISTE_ID_BY_STAGE_NAME, 
            String.class,
            artisteStageName);
    }

    // get artiste ty message by stage name
    public String getArtisteThankYouMessage(String artisteStageName) {
        return template.queryForObject(
            SELECT_ARTISTE_THANK_YOU_MSG_BY_STAGE_NAME, 
            String.class,
            artisteStageName);
    }

    // delete artiste from db
    // return int of rows affected
    public Integer deleteArtiste(String artisteId) {

        return template.update(DELETE_ARTISTE,
            Integer.class,
            artisteId);

    }

    // check if artiste has stripe access token
    public Integer checkArtisteStripeAccess(String artisteId) {
        return template.queryForObject(
            CHECK_ARTISTE_ACCESS_TOKEN, 
            Integer.class,
            artisteId);
    }

}
