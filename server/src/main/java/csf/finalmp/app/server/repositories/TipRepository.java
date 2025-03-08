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
    public Long insertTip(Tip tip) {

        // keyholder to hold new id
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        // insert tip
        template.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                INSERT_TIP,
                Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, tip.getMusicianId());
            ps.setDouble(2, tip.getAmount());
            ps.setString(3, tip.getStripeChargeId());
            return ps;},
            keyHolder);

        Long id = keyHolder.getKey().longValue();
        return id;
        
    }

    // get tips for musician by fk musician id
    public List<Tip> getTipsByMusicianId(Long musicianId) {
        return template.query(
            SELECT_TIPS_BY_MID, 
            (rs, rowNum) -> new Tip(
                rs.getLong("id"), 
                rs.getDouble("amount"), 
                rs.getString("stripe_charge_id"), 
                rs.getLong("musician_id")),
            musicianId);
    }
    
}
