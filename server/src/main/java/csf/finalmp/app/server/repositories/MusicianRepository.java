package csf.finalmp.app.server.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

import static csf.finalmp.app.server.utils.Sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import csf.finalmp.app.server.exceptions.MusicianNotFoundException;
import csf.finalmp.app.server.models.Musician;

// PURPOSE OF THIS REPO: DB CRUD OPS FOR MUSICIANS

@Repository
public class MusicianRepository {
    
    @Autowired
    private JdbcTemplate template;

    // check table count in mysql
    public Integer checkCount() {
        return template.queryForObject(
            CHECK_TABLE_COUNT, 
            Integer.class
        );
    }

    // create table
    public void createTable() {
        template.execute(CREATE_TABLE);
    }

    // insert musician into table and return musician object
    public Musician insertMusician(Musician musician) {

        // new key holder to hold key of latest inserted musician
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // insert musican into db
        template.update(
            connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    INSERT_MUSICIAN,
                    Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, musician.getName());
                ps.setString(2, musician.getLocation());
                return ps;
            }, keyHolder
        );

        // return musician object with id from mysql
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        musician.setId(id);
        return musician;

    }

    // get musician by id
    public Musician getMusicianById(Long id) {

        // execute query and return musician object
        // on error, throw custom error
        try {
            return template.queryForObject(
                SELECT_MUSICIAN_BY_ID, 
                new BeanPropertyRowMapper<>(Musician.class),
                id);
        } catch (Exception e) {
            throw new MusicianNotFoundException(
                String.format("Musician %s not found: %s", id, e.getMessage())
            );
        }

    }

    // get musicians based on location
    public List<Musician> getMusiciansByLocation(String location) {

        // execute query and return list of musicians
        return template.query(
            SELECT_MUSICIANS_BY_LOCATION,
            (rs, rowNum) -> new Musician(
                rs.getLong("id"), 
                rs.getString("name"),
                rs.getString("location")
            ),
            location
        );

    }

    // get all musicians in db
    public List<Musician> getAllMusicians() {

        // execute query and return list of musicians
        return template.query(
            SELECT_ALL_MUSICIANS, 
            (rs, rowNum) -> new Musician(
                rs.getLong("id"), 
                rs.getString("name"), 
                rs.getString("location")
            )
        );

    }

    // update musician data
    // return int of rows affected
    public int updateMusician(Long id, Musician musician) {

        return template.update(
            UPDATE_MUSICIAN, 
            musician.getName(), musician.getLocation(),
            id
        );

    }

    // delete musician from db
    // return int of rows affected
    public int deleteMusician(Long id) {
        return template.update(DELETE_MUSICIAN, 
            id);
    }

}
