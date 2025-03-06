package csf.finalmp.app.server.models;

// PURPOSE OF THIS MODEL
// STORE BASIC INFO OF MUSICIANS FOR CLIENT DISPLAY

public class Musician {
    
    // variables
    private Long id;
    private String name;
    private String location;

    // constructors
    public Musician() {}
    public Musician(Long id, String name, String location) {
        this.id = id;
        this.name = name;
        this.location = location;
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    // to string
    @Override
    public String toString() {
        return "Musician [id=" + id + ", name=" + name + ", location=" + location + "]";
    }

}
