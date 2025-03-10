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
            id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL, 
            tipper_id BIGINT NOT NULL, 
            musician_id BIGINT NOT NULL, 
            amount DECIMAL(10, 2) DEFAULT 0.00 NOT NULL, 
            stripe_charge_id VARCHAR(255) NOT NULL UNIQUE, 
            transaction_status VARCHAR(30) DEFAULT 'pending' NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
            updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
            FOREIGN KEY (tipper_id) REFERENCES users(id),
            FOREIGN KEY(musician_id) REFERENCES musician_profiles(user_id) ON DELETE CASCADE);
    """;

    // insert tip into table
    public static final String INSERT_TIP = """
        INSERT INTO tips (tipper_id, musician_id, amount, stripe_charge_id, transaction status)
            VALUES (?, ?, ?, ?, ?);
    """;

    // select tip by tip ID
    public static final String SELECT_TIP_BY_ID = """
        SELECT * FROM tips
            WHERE id = ?;        
    """;

    // select tips by tipper ID
    public static final String SELECT_TIPS_BY_TID = """
        SELECT * FROM tips
            WHERE tipper_id = ?;    
    """;

    // select tips by musician ID
    public static final String SELECT_TIPS_BY_MID = """
        SELECT * FROM tips
            WHERE musician_id = ?;    
    """;

    // select all tips
    public static final String SELECT_ALL_TIPS = """
        SELECT * FROM tips;        
    """;

}
