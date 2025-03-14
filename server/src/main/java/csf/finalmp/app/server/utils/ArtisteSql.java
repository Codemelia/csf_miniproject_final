package csf.finalmp.app.server.utils;

// SQL QUERY STATEMENTS FOR ARTISTES

public class ArtisteSql {

    // check count in table
    public static final String CHECK_ARTISTES_TABLE_COUNT = """
        SELECT COUNT(*) FROM artistes;        
    """;
    
    // create table
    public static final String CREATE_ARTISTES_TABLE = """
        CREATE TABLE IF NOT EXISTS artistes (
            artiste_id CHAR(8) PRIMARY KEY, 
            stage_name VARCHAR(50) NOT NULL UNIQUE, 
            bio TEXT, 
            photo LONGBLOB,
            qr_code BLOB NOT NULL,
            qr_code_url VARCHAR(255) NOT NULL UNIQUE,
            stripe_account_id VARCHAR(255) UNIQUE,
            stripe_access_token VARCHAR(255),
            stripe_refresh_token VARCHAR(255),
            wallet_balance DECIMAL(10, 2) DEFAULT 0.00,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (artiste_id) REFERENCES users(user_id) ON DELETE CASCADE);
    """;

    // insert artiste into table
    // intial insert will be without stripe access token/id
    public static final String INSERT_ARTISTE = """
        INSERT INTO artistes (artiste_id, stage_name, bio, photo, qr_code, qr_code_url)
            VALUES (?, ?, ?, ?, ?, ?);
    """;

    // update artiste access token and account id
    public static final String UPDATE_ARTISTE_STRIPE = """
        UPDATE artistes SET stripe_account_id = ?, stripe_access_token = ?, stripe_refresh_token = ?
            WHERE artiste_id = ?;        
    """;

    // update artiste basic info
    public static final String UPDATE_ARTISTE_PROFILE = """
        UPDATE artistes SET stage_name = ?, bio = ?, photo = ?
            WHERE artiste_id = ?;        
    """;

    // indiv method to update artiste wallet to avoid any confusion
    public static final String UPDATE_WALLET = """
        UPDATE artistes SET wallet_balance = ?
            WHERE artiste_id =?;        
    """;

    // select artiste by ID
    public static final String SELECT_ARTISTE_BY_ID = """
        SELECT * FROM artistes
            WHERE artiste_id = ?;        
    """;

    // select all artistes
    public static final String SELECT_ALL_ARTISTES = """
        SELECT * FROM artistes;        
    """;

    // select artiste wallet balance
    public static final String SELECT_ARTISTE_BALANCE_BY_ID = """
        SELECT wallet_balance FROM artistes
            WHERE artiste_id = ?;
    """;

    // get artiste stripe account id via id
    public static final String SELECT_ARTISTE_STRIPE_ACCOUNT_ID_BY_ID = """
        SELECT stripe_account_id FROM artistes
            WHERE artiste_id = ?;
    """;

    // get artiste id via stage name
    public static final String SELECT_ARTISTE_ID_BY_STAGE_NAME = """
        SELECT artiste_id FROM artistes
            WHERE stage_name = ?
    """;

    // check if row for id exists in db
    public static final String CHECK_ARTISTE_ID = """
        SELECT EXISTS(SELECT 1 FROM artistes WHERE artiste_id = ?);
    """;

    // check if row for stagename exists in db
    public static final String CHECK_ARTISTE_STAGE_NAME = """
        SELECT EXISTS(SELECT 1 FROM artistes WHERE stage_name = ?);    
    """;

    // delete artiste from db
    public static final String DELETE_ARTISTE = """
        DELETE FROM artistes
            WHERE artiste_id = ?;        
    """;

    // check if artiste id row has valid stripe access token
    public static final String CHECK_ARTISTE_ACCESS_TOKEN = """
        SELECT COUNT(*) FROM artistes
            WHERE artiste_id = ? 
            AND stripe_access_token IS NOT NULL       
    """;

}
