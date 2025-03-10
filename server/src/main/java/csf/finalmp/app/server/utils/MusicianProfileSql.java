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
            user_id BIGINT PRIMARY KEY NOT NULL, 
            stage_name VARCHAR(50) NOT NULL, 
            bio TEXT, 
            photo BLOB,
            qr_code_url VARCHAR(255) NOT NULL UNIQUE,
            stripe_account_id VARCHAR(255) NOT NULL UNIQUE,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE);
    """;

    // insert musician into table
    public static final String INSERT_MUSICIAN = """
        INSERT INTO musician_profiles (user_id, stage_name, bio, photo, qr_code_url, stripe_account_id)
            VALUES (?, ?, ?, ?, ?, ?);
    """;

    // select musician by ID
    public static final String SELECT_MUSICIAN_BY_ID = """
        SELECT * FROM musician_profiles
            WHERE user_id = ?;        
    """;

    // select all musicians
    public static final String SELECT_ALL_MUSICIANS = """
        SELECT * FROM musician_profiles;        
    """;

    // check if row for id exists in db
    public static final String CHECK_MUSICIAN_ID = """
        SELECT EXISTS(SELECT 1 FROM musician_profiles WHERE user_id = ?);
    """;

    // update musician info
    public static final String UPDATE_MUSICIAN = """
        UPDATE musician_profiles SET stage_name = ?, bio = ?, photo = ?
            WHERE user_id = ?;        
    """;

    // delete musician from db
    public static final String DELETE_MUSICIAN = """
        DELETE FROM musician_profiles
            WHERE user_id = ?;        
    """;

}
