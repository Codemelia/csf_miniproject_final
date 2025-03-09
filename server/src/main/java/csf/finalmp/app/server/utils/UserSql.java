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
            id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, 
            username VARCHAR(255) UNIQUE NOT NULL, 
            password VARCHAR(255) NOT NULL, 
            role VARCHAR(50) NOT NULL, 
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;
    """;

    // insert user
    public static final String INSERT_USER = """
        INSERT INTO users (username, password, role) 
            VALUES (?, ?, ?);  
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

    // update user details
    public static final String UPDATE_USER = """
        UPDATE users SET password = ?, role = ?
            WHERE id = ?;        
    """;

}
