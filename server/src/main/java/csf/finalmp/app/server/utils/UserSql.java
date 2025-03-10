package csf.finalmp.app.server.utils;

// PURPOSE OF THIS UTILS CLASS
// SQL QUERY STATEMENTS FOR USER DATABASE

public class UserSql {
    
    // check count in table
    public static final String CHECK_USERS_TABLE_COUNT = """
        SELECT COUNT(*) FROM users;        
    """;
    
    // create table
    // unique keys: id for db functions | email for security | username for client display (profiles)
    public static final String CREATE_USERS_TABLE = """
        CREATE TABLE IF NOT EXISTS users (
            id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, 
            email VARCHAR(255) NOT NULL UNIQUE,
            username VARCHAR(30) NOT NULL UNIQUE, 
            password VARCHAR(60) NOT NULL, 
            phone_number VARCHAR(50),
            role VARCHAR(50) NOT NULL, 
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL);
    """;

    // insert user
    public static final String INSERT_USER = """
        INSERT INTO users (email, username, password, phone_number, role) 
            VALUES (?, ?, ?, ?, ?);  
    """;

    // select user by displayname
    public static final String SELECT_USER_BY_EMAIL = """
        SELECT * FROM users
            WHERE email = ?;
    """;

    // check if display name exists in db
    public static final String CHECK_USERNAME = """
        SELECT EXISTS(SELECT 1 FROM users WHERE username = ?)         
    """;

    // check if email exists in db
    public static final String CHECK_EMAIL = """
        SELECT EXISTS(SELECT 1 FROM users WHERE email = ?)         
    """;

    // update user details
    public static final String UPDATE_USER = """
        UPDATE users SET email = ?, username = ?, password = ?, phone_number = ?, role = ?
            WHERE id = ?;        
    """;

}
