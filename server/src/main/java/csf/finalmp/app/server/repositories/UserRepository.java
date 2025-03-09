package csf.finalmp.app.server.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import csf.finalmp.app.server.models.User;

import static csf.finalmp.app.server.utils.UserSql.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;

@Repository
public class UserRepository {
    
    @Autowired
    private JdbcTemplate template;

    // check table count in mysql
    public Integer checkCount() {
        return template.queryForObject(
            CHECK_USERS_TABLE_COUNT, 
            Integer.class
        );
    }

    // create table
    public void createTable() {
        template.execute(CREATE_USERS_TABLE);
    }

    // insert user and return user id
    // update user details if id exists
    public Long saveUser(User user) {

        // boolean based on id
        boolean isInsert = user.getId() == null;

        // new key holder to hold key of latest inserted musician
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // insert musican into db
        template.update(connection -> {
            PreparedStatement ps;

            if (isInsert) {
                ps = connection.prepareStatement(
                    INSERT_USER, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getRole());
            } else {
                ps = connection.prepareStatement(UPDATE_USER);
                ps.setString(1, user.getPassword());
                ps.setString(2, user.getRole());
                ps.setLong(3, user.getId());
            }

             
            return ps;}, 
            keyHolder);

        // return id based on whether it is a update/regis
        return isInsert ? Objects.requireNonNull(keyHolder.getKey())
            .longValue() : user.getId();

    }

    // select user by username
    public User getUserByUsername(String username) {

        try {
            return template.queryForObject(
            SELECT_USER_BY_USERNAME, 
            (rs, rowNum) -> new User(
                rs.getLong("id"), 
                rs.getString("username"), 
                rs.getString("password"), 
                rs.getString("role"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()),
            username);
        } catch (EmptyResultDataAccessException e) {
            return null; // return null to handle on service layer
        }
        

    }

    // check is user exists in mysql
    public Integer userExists(String username) {
        
        return template.queryForObject(
            CHECK_USERNAME, 
            Integer.class,
            username);

    }


}
