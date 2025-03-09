package csf.finalmp.app.server.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static csf.finalmp.app.server.utils.MusicianProfileSql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import csf.finalmp.app.server.exceptions.custom.MusicianNotFoundException;
import csf.finalmp.app.server.models.MusicianProfile;

// PURPOSE OF THIS REPO: DB CRUD OPS FOR MUSICIANS

@Repository
public class MusicianProfileRepository {
    
    @Autowired
    private JdbcTemplate template;

    // check table count in mysql
    public Integer checkCount() {
        return template.queryForObject(
            CHECK_MUSICIANS_TABLE_COUNT, 
            Integer.class
        );
    }

    // create table
    public void createTable() {
        template.execute(CREATE_MUSICIANS_TABLE);
    }

    // insert musician into table and return id
    // update musician if profile id is passed in
    public Long saveMusician(MusicianProfile musician) {

        // boolean based on id
        Boolean isInsert = musician.getId() == null;

        // new key holder to hold key of latest inserted musician
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // dtf to format dates before storing
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // insert musican profile into db if id does not exist
        // update musician profile if id exists
        template.update(connection -> {
            PreparedStatement ps;
    
            if (isInsert) {
                ps = connection.prepareStatement(INSERT_MUSICIAN, 
                    Statement.RETURN_GENERATED_KEYS);
                ps.setLong(1, musician.getUserId());
                ps.setString(2, musician.getDisplayName());
                ps.setString(3, musician.getBio());
                ps.setBytes(4, musician.getPhoto());
            } else {
                ps = connection.prepareStatement(UPDATE_MUSICIAN);
                ps.setString(1, musician.getDisplayName());
                ps.setString(2, musician.getBio());
                ps.setBytes(3, musician.getPhoto());
                ps.setLong(4, musician.getId());
            }
    
            return ps;
        }, keyHolder);
    
        // return new id if is insert, existing id otherwise
        return isInsert ? Objects.requireNonNull(keyHolder.getKey())
            .longValue() : musician.getId();

    }

    // get musician by id
    // on error, throw custom error
    public MusicianProfile getMusicianById(Long id) {

        try {
            return template.queryForObject(
                SELECT_MUSICIAN_BY_ID, 
                new BeanPropertyRowMapper<>(MusicianProfile.class),
                id);
        } catch (Exception e) {
            throw new MusicianNotFoundException(
                "Musician with ID %d could not be found: %s".formatted(id, e.getMessage()));
        }

    }

    // get all musicians in db
    public List<MusicianProfile> getAllMusicians() {

        return template.query(
            SELECT_ALL_MUSICIANS, 
            new BeanPropertyRowMapper<>(MusicianProfile.class));

    }

    // check existing rows for id in db
    public Integer checkMusicianId(Long id) {

        return template.queryForObject(CHECK_MUSICIAN_ID,
        Integer.class,
        id);

    }

    // update musician data
    // return int of rows affected
    public int updateMusician(Long id, MusicianProfile musician) {

        return template.update(
            UPDATE_MUSICIAN, 
            musician.getDisplayName(), musician.getBio(), musician.getPhoto(),
            id);

    }

    // delete musician from db
    // return int of rows affected
    public int deleteMusician(Long id) {

        return template.update(DELETE_MUSICIAN, id);

    }

}
