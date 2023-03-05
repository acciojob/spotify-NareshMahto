package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;
   // public HashMap<Artist,List<Song>> artistlikedMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();
        //artistlikedMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        User u = new User(name,mobile);
        users.add(u);
        userPlaylistMap.put(u,new ArrayList<>());
        return u;
    }

    public Artist createArtist(String name) {
        Artist a = new Artist(name);
        artists.add(a);
        artistAlbumMap.put(a,new ArrayList<>());
        return a;
    }

    public Album createAlbum(String title, String artistName) {
        boolean present = false;
        Artist a = null;
        for(Artist at:artists)
        {
            if(at.getName().equals(artistName))
            {
                present = true;
                a = at;
                break;
            }
        }

        if(present == false)
        {
            a = new Artist(artistName);
            artists.add(a);
            artistAlbumMap.put(a,new ArrayList<>());
        }

        Album alm = new Album(title);
        //alm.setReleaseDate();
        albums.add(alm);
        if(artistAlbumMap.containsKey(a))
        {
            artistAlbumMap.get(a).add(alm);
        }
        else {
            List<Album> l = new ArrayList<>();
            l.add(alm);
            artistAlbumMap.put(a,l);
        }
        return alm;

    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        //If the album does not exist in database, throw "Album does not exist" exception
        //Create and add the song to respective album
        Album alm = null;
        Song s = new Song(title,length);
        for(Album am : albums)
        {
            if(am.getTitle().equals(albumName))
            {
                alm = am;
                break;
            }
        }
        if(alm == null)
        {
            throw new Exception("Album does not exist");
        }
        else {
            songs.add(s);
            if(albumSongMap.containsKey(alm))
            {
                albumSongMap.get(alm).add(s);
            }
            else {
                List<Song> l = new ArrayList<>();
                l.add(s);
                albumSongMap.put(alm,l);
            }
            return s;
        }

    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        //Create a playlist with given title and add all songs having the given length in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception
        for(Playlist p : playlists)
        {
            if(p.getTitle().equals(title))
            {
                return p;
            }
        }
        User us = null;
        Playlist p = new Playlist(title);
        for(User u : users)
        {
            if(u.getMobile().equals(mobile))
            {
                us = u;
                break;
            }
        }
        if(us == null)
        {
            throw new Exception("User does not exist");
        }
        else {
            List<Song> l = new ArrayList<>();
            for(Song s : songs)
            {
                if(s.getLength() == length)
                {
                    l.add(s);
                }
            }
            playlistSongMap.put(p,l);
            creatorPlaylistMap.put(us,p);

                List<User> lt = new ArrayList<>();
                if(playlistListenerMap.containsKey(p))
                {
                    lt = playlistListenerMap.get(p);
                }
                lt.add(us);
                playlistListenerMap.put(p,lt);


            playlists.add(p);
            List<Playlist> userpl = new ArrayList<>();
            if(userPlaylistMap.containsKey(us)){
                userpl=userPlaylistMap.get(us);
            }
            userpl.add(p);
            userPlaylistMap.put(us,userpl);


            return p;

        }
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        //Create a playlist with given title and add all songs having the given titles in the database to that playlist
        //The creater of the playlist will be the given user and will also be the only listener at the time of playlist creation
        //If the user does not exist, throw "User does not exist" exception

        for(Playlist playlist : playlists){
            if(playlist.getTitle().equals(title))
                return  playlist;
        }

        User us = null;
        Playlist p = new Playlist(title);
        for(User u : users)
        {
            if(u.getMobile().equals(mobile))
            {
                us = u;
                break;
            }
        }
        if(us == null)
        {
            throw new Exception("User does not exist");
        }
        else {
            List<Song> l = new ArrayList<>();

                for(Song s : songs)
                {
                    if(songTitles.contains(s.getTitle()))
                    {
                        l.add(s);
                    }
                }

            playlistSongMap.put(p,l);
            creatorPlaylistMap.put(us,p);

            List<User> lt = new ArrayList<>();
            if(playlistListenerMap.containsKey(p))
            {
                lt = playlistListenerMap.get(p);
            }
            lt.add(us);
            playlistListenerMap.put(p,lt);


            playlists.add(p);
            List<Playlist> userpl = new ArrayList<>();
            if(userPlaylistMap.containsKey(us)){
                userpl=userPlaylistMap.get(us);
            }
            userpl.add(p);
            userPlaylistMap.put(us,userpl);
            return p;
        }


    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        //Find the playlist with given title and add user as listener of that playlist and update user accordingly
        //If the user is creater or already a listener, do nothing
        //If the user does not exist, throw "User does not exist" exception
        //If the playlist does not exists, throw "Playlist does not exist" exception
        // Return the playlist after updating
        User us = null;
        Playlist p = null;
        for(Playlist pl : playlists)
        {
            if(pl.getTitle().equals(playlistTitle))
            {
                p = pl;
                break;
            }
        }

        for(User u : users)
        {
            if(u.getMobile().equals(mobile))
            {
                us = u;
                break;
            }
        }

        if(p == null)
        {
            throw new Exception("Playlist does not exist");
        }

        else if(us == null)
        {
            throw new Exception("User does not exist");
        }

        else
        {
            if(creatorPlaylistMap.containsKey(us) == false && playlistListenerMap.get(p).contains(us) == false)
            {
                if(playlistListenerMap.containsKey(p))
                {
                    playlistListenerMap.get(p).add(us);
                }
                else {
                    List<User> l = new ArrayList<>();
                    l.add(us);
                    playlistListenerMap.put(p,l);
                }
                creatorPlaylistMap.put(us,p);

                List<Playlist>userpl = new ArrayList<>();
                if(userPlaylistMap.containsKey(us)){
                    userpl=userPlaylistMap.get(us);
                }
                if(!userpl.contains(p))userpl.add(p);
                userPlaylistMap.put(us,userpl);
            }
            return p;

        }


    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        //The user likes the given song. The corresponding artist of the song gets auto-liked
        //A song can be liked by a user only once. If a user tried to like a song multiple times, do nothing
        //However, an artist can indirectly have multiple likes from a user, if the user has liked multiple songs of that artist.
        //If the user does not exist, throw "User does not exist" exception
        //If the song does not exist, throw "Song does not exist" exception
        //Return the song after updating
        User us = null;
        Song song = null;
        for(User u : users)
        {
            if(u.getMobile().equals(mobile))
            {
                us = u;
                break;
            }
        }

        for(Song s : songs)
        {
            if(s.getTitle().equals(songTitle))
            {
                song = s;
                break;
            }
        }

        if(us == null)
        {
            throw new Exception("User does not exist");
        }

        else if(song == null)
        {
            throw new Exception("Song does not exist");
        }
        else {
            Album alm = null;
            Artist art = null;

            for(Album alb : albumSongMap.keySet())
            {
                if(albumSongMap.get(alb).contains(song))
                {
                    alm = alb;
                    break;
                }
            }

            for(Artist a : artistAlbumMap.keySet())
            {
                if(artistAlbumMap.get(a).contains(alm))
                {
                    art = a;
                    break;
                }
            }
            if(songLikeMap.containsKey(song))
            {
                if(songLikeMap.get(song).contains(us) == false)
                {
                    songLikeMap.get(song).add(us);
                    song.setLikes(song.getLikes()+1);
                    art.setLikes(art.getLikes()+1);

                }

            }
            else {
                List<User> l = new ArrayList<>();
                l.add(us);
                songLikeMap.put(song,l);
                song.setLikes(song.getLikes()+1);
                art.setLikes(art.getLikes()+1);
            }
            return song;

        }
    }

    public String mostPopularArtist() {
        int max = Integer.MIN_VALUE;
        StringBuilder name = new StringBuilder();
        for(Artist a : artists)
        {
            if(a.getLikes() > max)
            {
                max = a.getLikes();
                name.append(a.getName());
            }
        }

        return name.toString();

    }

    public String mostPopularSong() {
        int max = Integer.MIN_VALUE;
        String name = "";
        for(Song s : songs)
        {
            if(s.getLikes()> max)
            {
                max = s.getLikes();
                //name.delete(0,name.length());
                name = s.getTitle();
            }
        }

        return name;
    }
}
