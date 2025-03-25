package csf.finalmp.app.server.utils;

// SQL QUERY STATEMENTS FOR ARTISTE TRANSACTION DETAILS

public class ArtisteTransactionSql {

    // check count in table
    public static final String CHECK_ARTISTES_TABLE_COUNT = """
        SELECT COUNT(*) FROM artiste_transaction_details;        
    """;
    
    // create table
    public static final String CREATE_ARTISTES_TABLE = """
        CREATE TABLE IF NOT EXISTS artiste_transaction_details (
            artiste_id CHAR(8) PRIMARY KEY, 
            stripe_account_id VARCHAR(255) NOT NULL UNIQUE,
            stripe_access_token VARCHAR(255) NOT NULL,
            stripe_refresh_token VARCHAR(255) NOT NULL,
            earnings DECIMAL(10, 2) DEFAULT 0.00,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
            FOREIGN KEY (artiste_id) REFERENCES users(user_id) ON DELETE CASCADE);
    """;

    // update artiste access token and account id
    public static final String INSERT_ARTISTE_STRIPE = """
        INSERT INTO artiste_transaction_details (artiste_id, stripe_account_id, stripe_access_token, stripe_refresh_token, earnings)
            VALUES (?, ?, ?, ?, ?);  
    """;

    // indiv method to update artiste earnings to avoid any confusion
    public static final String UPDATE_ARTISTE_EARNINGS = """
        UPDATE artiste_transaction_details SET earnings = ?
            WHERE artiste_id =?;        
    """;

    // select artiste by ID
    public static final String SELECT_ARTISTE_BY_ID = """
        SELECT * FROM artiste_transaction_details
            WHERE artiste_id = ?;        
    """;

    // select all artistes
    public static final String SELECT_ALL_ARTISTES = """
        SELECT * FROM artiste_transaction_details;        
    """;

    // select artiste earnings balance
    public static final String SELECT_ARTISTE_EARNINGS_BY_ID = """
        SELECT earnings FROM artiste_transaction_details
            WHERE artiste_id = ?;
    """;

    // get artiste stripe account id via id
    public static final String SELECT_ARTISTE_STRIPE_ACCOUNT_ID_BY_ID = """
        SELECT stripe_account_id FROM artiste_transaction_details
            WHERE artiste_id = ?;
    """;

    // get artiste id via stage name
    public static final String SELECT_ARTISTE_ID_BY_STAGE_NAME = """
        SELECT artiste_id FROM artiste_transaction_details
            WHERE stage_name = ?
    """;

    // check if row for id exists in db
    public static final String CHECK_ARTISTE_ID = """
        SELECT EXISTS(SELECT 1 FROM artiste_transaction_details WHERE artiste_id = ?);
    """;
    
    // check if artiste id row has valid stripe access token
    public static final String CHECK_ARTISTE_ACCESS_TOKEN = """
        SELECT COUNT(*) FROM artiste_transaction_details
            WHERE artiste_id = ? 
            AND stripe_access_token IS NOT NULL       
    """;

}