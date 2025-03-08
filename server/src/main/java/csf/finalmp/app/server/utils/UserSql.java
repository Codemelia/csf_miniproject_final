package csf.finalmp.app.server.utils;

// PURPOSE OF THIS UTILS CLASS
// SQL QUERY STATEMENTS FOR USER DATABASE

public class UserSql {
    
    // check count in table
    public static final String CHECK_USERS_TABLE_COUNT = """
        SELECT COUNT(*) FROM users;        
    """;
    
    // create table
    public static final String CREATE_USERS_TABLE = """
        CREATE TABLE IF NOT EXISTS users (
            id bigint auto_increment primary key,
            username varchar(255) unique not null,
            password varchar(255) not null,
            role varchar(50) not null
        );
    """;

    // insert user
    public static final String INSERT_USER = """
        INSERT INTO users (username, password, role)
            VALUES (?, ?, ?)    
    """;

    // select user by username
    public static final String SELECT_USER_BY_USERNAME = """
        SELECT * FROM users
            WHERE username = ?;
    """;

    // check if username exists in db
    public static final String CHECK_USERNAME = """
        SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)         
    """;

}
