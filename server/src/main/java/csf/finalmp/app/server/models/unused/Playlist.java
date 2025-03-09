package csf.finalmp.app.server.models.unused;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    // variables
    private Long id;
    private Long musicianId;
    private String name;
    private List<String> songs;

    // constructors
    public Playlist() { this.songs = new ArrayList<>(); }

    public Playlist(Long id, Long musicianId, String name, List<String> songs) {
        this.id = id;
        this.musicianId = musicianId;
        this.name = name;
        this.songs = songs != null ? new ArrayList<>(songs) : new ArrayList<>();
    }

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMusicianId() { return musicianId; }
    public void setMusicianId(Long musicianId) { this.musicianId = musicianId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getSongs() { return songs; }
    public void setSongs(List<String> songs) { this.songs = songs; }

    // to string
    @Override
    public String toString() {
        return "Playlist [id=" + id + ", musicianId=" + musicianId + ", name=" + name + ", songs=" + songs + "]";
    }

    // utility methods
    public void addSong(String song) { this.songs.add(song); }
    
}
