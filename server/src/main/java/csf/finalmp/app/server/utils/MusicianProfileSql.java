package csf.finalmp.app.server.utils;

// PURPOSE OF THIS UTIL CLASS
// SQL QUERY STATEMENTS FOR MUSICIANS

public class MusicianProfileSql {

    // check count in table
    public static final String CHECK_MUSICIANS_TABLE_COUNT = """
        SELECT COUNT(*) FROM musician_profiles;        
    """;
    
    // create table
    public static final String CREATE_MUSICIANS_TABLE = """
        CREATE TABLE IF NOT EXISTS musician_profiles (
            id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
            user_id BIGINT NOT NULL, 
            display_name VARCHAR(255), 
            bio TEXT, 
            photo BLOB,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (user_id) REFERENCES users(id));
    """;

    // insert musician into table
    public static final String INSERT_MUSICIAN = """
        INSERT INTO musician_profiles (user_id, display_name, bio, photo)
            VALUES (?, ?, ?, ?);
    """;

    // select musician by ID
    public static final String SELECT_MUSICIAN_BY_ID = """
        SELECT * FROM musician_profiles
            WHERE id = ?;        
    """;

    // select all musicians
    public static final String SELECT_ALL_MUSICIANS = """
        SELECT * FROM musician_profiles;        
    """;

    // check if row for id exists in db
    public static final String CHECK_MUSICIAN_ID = """
        SELECT EXISTS(SELECT 1 FROM musician_profiles WHERE id = ?);
    """;

    // update musician info
    public static final String UPDATE_MUSICIAN = """
        UPDATE musician_profiles SET display_name = ?, bio = ?, photo = ?
            WHERE id = ?;        
    """;

    // delete musician from db
    public static final String DELETE_MUSICIAN = """
        DELETE FROM musician_profiles
            WHERE id = ?;        
    """;

}
