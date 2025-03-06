package csf.finalmp.app.server.utils;

public class Sql {

    // check count in table
    public static final String CHECK_TABLE_COUNT = """
        SELECT COUNT(*) FROM musicians;        
    """;
    
    // create table
    public static final String CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS musicians (
            id bigint auto_increment primary key,
            name varchar(255) not null,
            location varchar(255) not null
        );
    """;

    // insert musician into table
    public static final String INSERT_MUSICIAN = """
        INSERT INTO musicians (name, location)
        VALUES (?, ?);
    """;

    // select musician by ID
    public static final String SELECT_MUSICIAN_BY_ID = """
        SELECT * FROM musicians
            WHERE id = ?;        
    """;

    // select musicians by location
    public static final String SELECT_MUSICIANS_BY_LOCATION = """
        SELECT * FROM musicians
            WHERE location = ?;        
    """;

    // select all musicians
    public static final String SELECT_ALL_MUSICIANS = """
        SELECT * FROM musicians;        
    """;

    // update musician info
    public static final String UPDATE_MUSICIAN = """
        UPDATE musicians SET name = ?, location = ?
            WHERE id = ?;        
    """;

    // delete musician from db
    public static final String DELETE_MUSICIAN = """
        DELETE FROM musicians
            WHERE id = ?;        
    """;

}
