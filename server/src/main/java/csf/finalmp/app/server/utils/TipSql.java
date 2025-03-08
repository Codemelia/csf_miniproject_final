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
            id bigint auto_increment primary key,
            amount double not null,
            stripe_charge_id varchar(255) not null,
            musician_id bigint not null,
            foreign key (musician_id) references musicians(id)
        );
    """;

    // insert tip into table
    public static final String INSERT_TIP = """
        INSERT INTO tips (amount, stripe_charge_id, musician_id)
            VALUES (?, ?, ?);
    """;

    // select tip by tip ID
    public static final String SELECT_TIP_BY_ID = """
        SELECT * FROM tips
            WHERE id = ?;        
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
