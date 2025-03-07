package csf.finalmp.app.server.utils;

// PURPOSE OF THIS UTIL CLASS
// SQL QUERY STATEMENTS FOR MUSICIANS

public class MusicSql {

    // check count in table
    public static final String CHECK_MUSICIANS_TABLE_COUNT = """
        SELECT COUNT(*) FROM musicians;        
    """;
    
    // create table
    public static final String CREATE_MUSICIANS_TABLE = """
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

    // check if row for id exists in db
    public static final String CHECK_MUSICIAN_ID = """
        SELECT EXISTS(SELECT 1 FROM users WHERE id = ?);
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
