package csf.finalmp.app.server.utils.unused;

// FOR WALLET FUNCTIONS

public class WalletSql {
    
    // check count in table
    public static final String CHECK_WALLETS_TABLE_COUNT = """
        SELECT COUNT(*) FROM wallets;        
    """;
    
    // create table
    public static final String CREATE_WALLETS_TABLE = """
        CREATE TABLE IF NOT EXISTS wallets (
            user_id BIGINT PRIMARY KEY NOT NULL,
            balance DECIMAL(10, 2) DEFAULT 0.00 NOT NULL,
            created_at DATETIME DEFAULT CURRENT_TIMESTAMP NOT NULL,
            updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
            FOREIGN KEY (user_id) REFERENCES users(id));
    """;

    // insert wallet
    public static final String INSERT_WALLET = """
        INSERT INTO wallets (user_id, balance) 
            VALUES (?, ?);  
    """;

    // select wallet by user id 
    public static final String SELECT_WALLET_BY_UID = """
        SELECT * FROM wallets
            WHERE user_id = ?;        
    """;

    // update wallet
    public static final String UPDATE_WALLET = """
        UPDATE wallets SET balance = ?
            WHERE user_id = ?;
    """;

}
