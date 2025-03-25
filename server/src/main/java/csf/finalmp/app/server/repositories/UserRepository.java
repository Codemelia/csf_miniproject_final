package csf.finalmp.app.server.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import csf.finalmp.app.server.models.User;

import static csf.finalmp.app.server.utils.UserSql.*;

import java.util.UUID;

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
    public String insertUser(User user) {

        // gen user id for inserts
        String userId = UUID.randomUUID().toString().substring(0, 8);

        // insert to mysql
        template.update(
            INSERT_USER, 
            userId,
            user.getEmail(),
            user.getPassword(),
            user.getPhoneNumber(),
            user.getRole());

        // return user id
        return userId;

    }

    // select user by username
    public User getUserByEmail(String email) {

        try {
            return template.queryForObject(
            SELECT_USER_BY_EMAIL, 
            (rs, rowNum) -> new User(
                rs.getString("user_id"), 
                rs.getString("email"),
                rs.getString("password"), 
                rs.getString("phone_number"),
                rs.getString("role"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("updated_at").toLocalDateTime()),
            email);
        } catch (EmptyResultDataAccessException e) {
            return null; // return null to handle on service layer
        }
        

    }

    // check if email exists in mysql
    public Integer emailExists(String email) {
        
        return template.queryForObject(
            CHECK_EMAIL, 
            Integer.class,
            email);
    
    }

    // get user's email via user id
    public String getUserEmailById(String userId) {

        return template.queryForObject(
            GET_USER_EMAIL_BY_ID, 
            String.class,
            userId);

    }

    // get user role via id
    public String getUserRoleById(String userId) {
        return template.queryForObject(
            GET_USER_ROLE_BY_ID, 
            String.class,
            userId);
    }

}
