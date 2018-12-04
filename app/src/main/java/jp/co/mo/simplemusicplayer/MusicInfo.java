package jp.co.mo.simplemusicplayer;

public class MusicInfo {

    private String path;
    private String songName;
    private String albumName;
    private String artistName;

    public MusicInfo(String path, String songName, String albumName, String artistName) {
        this.path = path;
        this.songName = songName;
        this.albumName = albumName;
        this.artistName = artistName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }
}
