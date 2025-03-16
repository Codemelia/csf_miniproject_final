package csf.finalmp.app.server.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import csf.finalmp.app.server.models.Tip;

import static csf.finalmp.app.server.utils.TipSql.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Repository
public class TipRepository {

    @Autowired
    private JdbcTemplate template;

    // check table count in mysql
    public Integer checkCount() {

        return template.queryForObject(
            CHECK_TIPS_TABLE_COUNT, 
            Integer.class
        );

    }

    // create table
    public void createTable() {

        template.execute(CREATE_TIPS_TABLE);

    }

    // insert tip into db and return mysql id
    public Long saveTip(Tip tip) {

        // set insert boolean to determine if save is an insert
        boolean isInsert = tip.getId() == null;

        // keyholder to hold new id
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        // perform insert/update
        template.update(connection -> {
            PreparedStatement ps;

            if (isInsert) {
                ps = connection.prepareStatement(
                    INSERT_TIP,
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, tip.getTipperName());
                ps.setString(2, tip.getTipperMessage());
                ps.setString(3, tip.getTipperEmail());
                ps.setString(4, tip.getTipperId());
                ps.setString(5, tip.getArtisteId());
                ps.setDouble(6, tip.getAmount());
                ps.setString(7, tip.getPaymentIntentId());
                ps.setString(8, tip.getPaymentStatus());
            } else {
                ps = connection.prepareStatement(UPDATE_PAYMENT_STATUS);
                ps.setString(1, tip.getPaymentStatus());
                ps.setString(2, tip.getPaymentIntentId());
            }

            return ps;}, 
            keyHolder);

        // return id based on whether it is a update/insert
        return isInsert ? Objects.requireNonNull(keyHolder.getKey())
            .longValue() : tip.getId();
        
    }

    // get tip by payment intent id for payment confirmation
    public Tip getTipByPid(String paymentIntentId) {
        return template.queryForObject(
            SELECT_TIP_BY_PID, 
            (rs, rowNum) -> new Tip(
                rs.getLong("tip_id"), 
                rs.getString("tipper_name"),
                rs.getString("tipper_message"),
                rs.getString("tipper_email"),
                rs.getString("tipper_id"),
                rs.getString("artiste_id"),
                rs.getDouble("amount"), 
                rs.getString("payment_intent_id"), 
                rs.getString("payment_status"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                null),
            paymentIntentId);
    }

    // get tips for artiste by fk artiste id
    public List<Tip> getTipsByArtisteId(String artisteId) {
        return template.query(
            SELECT_TIPS_BY_MID, 
            (rs, rowNum) -> new Tip(
                rs.getLong("tip_id"), 
                rs.getString("tipper_name"),
                rs.getString("tipper_message"),
                rs.getString("tipper_email"),
                rs.getString("tipper_id"),
                rs.getString("artiste_id"),
                rs.getDouble("amount"), 
                rs.getString("payment_intent_id"), 
                rs.getString("payment_status"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime(),
                null),
            artisteId);

    }
    
}
