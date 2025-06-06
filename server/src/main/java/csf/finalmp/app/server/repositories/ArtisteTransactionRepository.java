package csf.finalmp.app.server.repositories;

import java.util.List;

import static csf.finalmp.app.server.utils.ArtisteTransactionSql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import csf.finalmp.app.server.exceptions.custom.UserNotFoundException;
import csf.finalmp.app.server.models.ArtisteTransactionDetails;

// MYSQL CRUD OPS FOR ARTISTE TRANSACTION DETAILS

@Repository
public class ArtisteTransactionRepository {
    
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

    // insert artiste stripe details
    public int saveArtisteStripe(ArtisteTransactionDetails artisteDetails) {
        
        return template.update(
            INSERT_ARTISTE_STRIPE,
            artisteDetails.getArtisteId(),
            artisteDetails.getStripeAccountId(),
            artisteDetails.getStripeAccessToken(),
            artisteDetails.getStripeRefreshToken(),
            0.0); // insert 0$ to earnings

    }

    // update artiste earnings
    // return int of rows affected
    public int updateArtisteEarnings(String artisteId, double balance) {

        return template.update(
            UPDATE_ARTISTE_EARNINGS, 
            balance, 
            artisteId);

    }

    // get artiste by id
    // on error, throw custom error
    public ArtisteTransactionDetails getArtisteById(String artisteId) {

        try {
            return template.queryForObject(
                SELECT_ARTISTE_BY_ID, 
                (rs, rowNum) -> new ArtisteTransactionDetails(
                    rs.getString("artiste_id"), 
                    rs.getString("stripe_account_id"), 
                    rs.getString("stripe_access_token"),
                    rs.getString("stripe_refresh_token"),
                    rs.getDouble("earnings"),
                    rs.getTimestamp("created_at").toLocalDateTime(), 
                    rs.getTimestamp("updated_at").toLocalDateTime()),
                artisteId);
        } catch (Exception e) {
            throw new UserNotFoundException(
                "Vibee could not be found: %s".formatted(e.getMessage()));
        }

    }

    // get all artistes in db
    public List<ArtisteTransactionDetails> getAllArtistes() {

        return template.query(
            SELECT_ALL_ARTISTES, 
            (rs, rowNum) -> new ArtisteTransactionDetails(
                rs.getString("artiste_id"), 
                rs.getString("stripe_account_id"),
                rs.getString("stripe_access_token"),
                rs.getString("stripe_refresh_token"),
                rs.getDouble("earnings"), 
                rs.getTimestamp("created_at").toLocalDateTime(), 
                rs.getTimestamp("updated_at").toLocalDateTime()));

    }

    // check existing rows for id in db
    public Integer checkArtisteId(String artisteId) {

        return template.queryForObject(CHECK_ARTISTE_ID,
        Integer.class,
        artisteId);

    }

    // get artiste curr balance
    public Double getArtisteEarnings(String artisteId) {

        return template.queryForObject(
            SELECT_ARTISTE_EARNINGS_BY_ID, 
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

    // check if artiste has stripe access token
    public Integer checkArtisteStripeAccess(String artisteId) {
        return template.queryForObject(
            CHECK_ARTISTE_ACCESS_TOKEN, 
            Integer.class,
            artisteId);
    }

}
