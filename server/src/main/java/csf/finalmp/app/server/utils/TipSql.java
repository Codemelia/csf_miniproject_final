package csf.finalmp.app.server.utils;

// PURPOSE OF THIS UTILS CLASS
// SQL QUERY STATEMENTS FOR TIPPERS

public class TipSql {

    // check count in table
    public static final String CHECK_TIPS_TABLE_COUNT = """
        SELECT COUNT(*) FROM tips;        
    """;
    
    // create table
    public static final String CREATE_TIPS_TABLE = """
        CREATE TABLE IF NOT EXISTS tips (
            tip_id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, 
            tipper_id CHAR(8) NOT NULL, 
            artiste_id CHAR(8) NOT NULL, 
            amount DECIMAL(10, 2) DEFAULT 0.00 NOT NULL, 
            payment_intent_id VARCHAR(255) NOT NULL UNIQUE, 
            payment_status VARCHAR(30) DEFAULT 'pending' NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
            FOREIGN KEY (tipper_id) REFERENCES users(user_id),
            FOREIGN KEY(artiste_id) REFERENCES artistes(artiste_id) ON DELETE CASCADE);
    """;

    // insert tip into table
    public static final String INSERT_TIP = """
        INSERT INTO tips (tipper_id, artiste_id, amount, payment_intent_id, payment_status)
            VALUES (?, ?, ?, ?, ?);
    """;

    // select tips by artiste ID
    public static final String SELECT_TIPS_BY_MID = """
        SELECT * FROM tips
            WHERE artiste_id = ?;    
    """;

    // select tip by tip ID
    public static final String SELECT_TIP_BY_PID = """
        SELECT * FROM tips
            WHERE payment_intent_id = ?;        
    """;

    // select all tips
    public static final String SELECT_ALL_TIPS = """
        SELECT * FROM tips;        
    """;

    // update payment status
    public static final String UPDATE_PAYMENT_STATUS = """
        UPDATE tips SET payment_status = ?
            WHERE payment_intent_id = ?;        
    """;

}
