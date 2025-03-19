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
    public String saveTip(Tip tip) {

        // perform insert/update
        template.update(
            INSERT_TIP,
            tip.getTipperName(),
            tip.getTipperMessage(),
            tip.getTipperEmail(),
            tip.getTipperId(),
            tip.getArtisteId(),
            tip.getAmount(),
            tip.getPaymentIntentId(),
            tip.getPaymentStatus()
        );

        return tip.getArtisteId();
        
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
                null,
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
                null,
                null),
            artisteId);

    }
    
}
